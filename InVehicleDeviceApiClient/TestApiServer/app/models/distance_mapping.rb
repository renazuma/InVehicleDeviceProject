# == Schema Information
#
# Table name: distance_mappings
#
#  id                    :integer(4)      not null, primary key
#  departure_platform_id :integer(4)
#  arrival_platform_id   :integer(4)
#  distance              :integer(4)
#  duration              :integer(4)
#  created_at            :datetime        not null
#  updated_at            :datetime        not null
#

require "net/http"
require "bigdecimal/math"
class DistanceMapping < ActiveRecord::Base
  belongs_to :departure_platform, :class_name => "Platform", :foreign_key => "departure_platform_id"
  belongs_to :arrival_platform, :class_name => "Platform", :foreign_key => "arrival_platform_id"

  GOOGLE_DIRECTIONS_API = "http://maps.googleapis.com/maps/api/directions/json"
  VEHICLE_SPEED = 20000 # 車両の時速 (メートル)

  def self.generate(service_provider_id)
    now = Time.zone.now.utc
    ActiveRecord::Base.connection.execute("INSERT IGNORE INTO #{self.table_name}
                                          SELECT NULL as id,
                                          p1.id as departure_platform_id,
                                          p2.id as arrival_platform_id,
                                          NULL as distance,
                                          NULL as duration,
                                          '#{now.strftime("%Y-%m-%d %H:%M:%S")}' as created_at,
                                          '#{now.strftime("%Y-%m-%d %H:%M:%S")}' as updated_at
                                          from platforms as p1, platforms as p2 where p1.service_provider_id = #{service_provider_id} AND p2.service_provider_id = #{service_provider_id} AND p1.id <> p2.id;")
  end

  def self.find_by_platforms(departure_platform, arrival_platform)
    if departure_platform && arrival_platform
      self.where(:departure_platform_id => departure_platform.id, :arrival_platform_id => arrival_platform.id).includes([:departure_platform, :arrival_platform]).first
    end
  end

  # 関連する乗降場の緯度・経度を使って、Google APIから距離と移動時間を取得する。
  def fetch_distance_and_duration
    return true if self.distance && self.duration

    uri = URI.parse(ENV["HTTP_PROXY"].to_s)
    if uri.userinfo
      proxy_user, proxy_pass = uri.userinfo.split(/:/)
    else
      proxy_user, proxy_pass = nil, nil
    end
    client_class = Net::HTTP.Proxy(uri.host, uri.port, proxy_user, proxy_pass)
    json = client_class.get(URI.parse(GOOGLE_DIRECTIONS_API + "?origin=#{self.departure_platform.latitude.to_s},#{self.departure_platform.longitude.to_s}&destination=#{self.arrival_platform.latitude.to_s},#{self.arrival_platform.longitude.to_s}&sensor=false"))
    info = ActiveSupport::JSON.decode(json)

    if info["status"] == "OK"
      result = info["routes"][0]["legs"][0]
      self.distance = result["distance"]["value"]
      self.duration = result["duration"]["value"]
      return self.save
    else
      return nil
    end
  rescue Timeout::Error, StandardError => e
    puts e.backtrace
    logger.warn("Fetch distance and duration failed for Platform:#{self.departure_platform_id} to Platform:#{self.arrival_platform_id}\n#{e.message}")
    return nil
  end

  # 関連する乗降場の緯度・経度から、直線距離を算出する
  # cf. ヒュベニの距離計算式
  def calc_distance_and_duration
    latitude_ave = (self.departure_platform.latitude_rad + self.arrival_platform.latitude_rad) / 2 # 二点間の平均緯度(ラジアン)
    d_latitude = self.arrival_platform.latitude_rad - self.departure_platform.latitude_rad # 二点間の緯度の差(ラジアン)
    d_longitude = self.arrival_platform.longitude_rad - self.departure_platform.longitude_rad # 二点間の経度の差(ラジアン)
    m = 6334834 / Math::sqrt((1 - 0.006674 * Math::sin(latitude_ave) * Math::sin(latitude_ave)) ** 3) # 子午線曲率半径
    n = 6377397 / Math::sqrt(1 - 0.006674 * Math::sin(latitude_ave) * Math::sin(latitude_ave)) # 卯酉線曲率半径

    d_meter = Math::sqrt(((m * d_latitude) * (m * d_latitude) + (n * Math::cos(latitude_ave) * d_longitude) * (n * Math::cos(latitude_ave) * d_longitude)))

    # 時速を20km/hと仮定して移動時間を算出する (秒)
    duration = d_meter / VEHICLE_SPEED * 3600

    self.update_attributes(:distance => d_meter, :duration => duration)
  end
end
