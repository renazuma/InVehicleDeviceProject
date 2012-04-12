# == Schema Information
#
# Table name: vector_clusters
#
#  id                    :integer(4)      not null, primary key
#  departure_latitude    :decimal(17, 14)
#  departure_longitude   :decimal(17, 14)
#  departure_time        :datetime
#  departure_platform_id :integer(4)
#  arrival_latitude      :decimal(17, 14)
#  arrival_longitude     :decimal(17, 14)
#  arrival_time          :datetime
#  arrival_platform_id   :integer(4)
#  service_provider_id   :integer(4)
#  cluster_id            :integer(4)
#  created_at            :datetime        not null
#  updated_at            :datetime        not null
#

class VectorCluster < ActiveRecord::Base
  # Define Relations
  belongs_to :service_provider
  belongs_to :departure_platform, :class_name => "Platform", :foreign_key => "departure_platform_id"
  belongs_to :arrival_platform, :class_name => "Platform", :foreign_key => "arrival_platform_id"

  # TODO: 仮実装
  def self.generate_from_initial_vectors(service_provider)
    unless service_provider.instance_of?(ServiceProvider)
      service_provider = ServiceProvider.find(service_provider)
    end
    service_provider.initial_vectors.each do |iv|
      now = Time.zone.now
      case iv.time_scope
      when 1
        departure_time = Time.zone.local(now.year, now.month, now.day, 6, 0)
        arrival_time = Time.zone.local(now.year, now.month, now.day, 10, 0)
      when 2
        departure_time = Time.zone.local(now.year, now.month, now.day, 10, 0)
        arrival_time = Time.zone.local(now.year, now.month, now.day, 14, 0)
      when 3
        departure_time = Time.zone.local(now.year, now.month, now.day, 14, 0)
        arrival_time = Time.zone.local(now.year, now.month, now.day, 18, 0)
      end
      service_provider.vector_clusters.create(
        :departure_time => departure_time,
        :departure_latitude => iv.departure_latitude,
        :departure_longitude => iv.departure_longitude,
        :arrival_time => arrival_time,
        :arrival_latitude => iv.arrival_latitude,
        :arrival_longitude => iv.arrival_longitude,
      )
    end
  end
end
