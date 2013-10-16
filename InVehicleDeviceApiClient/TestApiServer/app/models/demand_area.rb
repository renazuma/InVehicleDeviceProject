# == Schema Information
#
# Table name: demand_areas
#
#  id                  :integer(4)      not null, primary key
#  name                :string(255)
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  type_of_demand      :integer(1)
#  service_provider_id :integer(4)
#

class DemandArea < ActiveRecord::Base
  # Define Validation
  validates_presence_of :name

  # Define Relation
  belongs_to :service_provider
  has_many :platforms
  has_many :lat_lngs, :inverse_of => :demand_area, :dependent => :delete_all, :order => "position ASC"
  has_one :lat_lng_as_head, :inverse_of => :head_area, :class_name => "LatLng", :foreign_key => "head_area_id", :dependent => :delete
  has_many :semi_demand_course_orders
  has_many :semi_demand_policies, :through => :semi_demand_course_orders

  # Operation Audit
  acts_as_audited

  # Const
  FULL_DEMAND_TYPE = 1
  SEMI_DEMAND_TYPE = 2

  # Define Scope
  scope :semi_demand_areas, -> { where(:type_of_demand => SEMI_DEMAND_TYPE) }
  scope :full_demand_areas, -> { where(:type_of_demand => FULL_DEMAND_TYPE) }

  accepts_nested_attributes_for :lat_lngs
  accepts_nested_attributes_for :lat_lng_as_head

  def self.find_by_contains_point_from_semi_demand(lat, lng)
    find_by_contains_point(lat, lng, SEMI_DEMAND_TYPE)
  end

  def self.find_by_contains_point_from_full_demand(lat, lng)
    find_by_contains_point(lat, lng, FULL_DEMAND_TYPE)
  end

  def self.find_by_contains_point(lat, lng, demand_area_type)
    DemandArea.where(:type_of_demand => demand_area_type).select do |area|
      area.contains_point?(lat, lng)
    end
  end

  def contains_point?(lat, lng)

    # KMLデータを作成し座標として読み込む
    # 面積があるもの(3点以上もつもの)のみ含むかを計算
    # それ以外は、エリアとして成立しなため false
    if self.lat_lngs.count >= 3

      kml_data = ""
      kml_data << KML_HEADER
      self.lat_lngs.order("position desc").each do |p|
        kml_data << " #{p.longitude},#{p.latitude}\n"
      end
      kml_data << KML_FOOTER

      region = BorderPatrol.parse_kml(kml_data)
      point  = BorderPatrol::Point.new(lng, lat)
      region.contains_point?(point)
    else
      false
    end
  end

  private
  KML_HEADER = <<-'EOS'
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://earth.google.com/kml/2.2">
<Document>
<Placemark>
<Polygon>
<coordinates>
  EOS

  KML_FOOTER = <<-'EOS'
</coordinates>
</Polygon>
</Placemark>
</Document>
</kml>
  EOS

end
