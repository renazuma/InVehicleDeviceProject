# == Schema Information
#
# Table name: platforms
#
#  id                  :integer(4)      not null, primary key
#  name                :string(255)     not null
#  name_ruby           :string(255)     not null
#  memo                :string(500)
#  address             :string(500)
#  latitude            :decimal(17, 14) not null
#  longitude           :decimal(17, 14) not null
#  keyword             :string(255)
#  start_at            :datetime
#  end_at              :datetime
#  type_of_demand      :integer(1)
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  deleted_at          :datetime
#  image               :string(255)
#  service_provider_id :integer(4)
#  demand_area_id      :integer(4)
#  semi_demand_area_id :integer(4)
#

require "bigdecimal/math"
class Platform < ActiveRecord::Base
  # Define Validation
  validates_presence_of :name, :name_ruby, :latitude, :longitude
  #validate Proc.new {errors.add(:base, :blank_demand_area) if demand_area_id.blank? && semi_demand_area_id.blank? }

  # Define Relation
  belongs_to :service_provider
  belongs_to :demand_area
  belongs_to :semi_demand_area, :class_name => "DemandArea", :foreign_key => "semi_demand_area_id"
  has_many :reservations_as_departure, :class_name => "Reservation", :foreign_key => "departure_platform_id"
  has_many :reservations_as_arrival, :class_name => "Reservation", :foreign_key => "arrival_platform_id"
  has_many :reservation_candidates_as_departure, :class_name => "ReservationCandidate", :foreign_key => "departure_platform_id"
  has_many :reservation_candidates_as_arrival, :class_name => "ReservationCandidate", :foreign_key => "arrival_platform_id"
  has_many :demands_as_departure, :class_name => "Demand", :foreign_key => "departure_platform_id"
  has_many :demands_as_arrival, :class_name => "Demand", :foreign_key => "arrival_platform_id"
  has_many :group_platform_ships
  has_many :user_groups, :through => :group_platform_ships
  has_many :vector_clusters_as_departure, :class_name => "VectorCluster", :foreign_key => "departure_platform_id"
  has_many :vector_clusters_as_arrival, :class_name => "VectorCluster", :foreign_key => "arrival_platform_id"

  # Define Scope
  scope :search, lambda{|word| where('name LIKE ? OR keyword LIKE ?', "%#{word}%", "%#{word}%")}

  # Define for Logical Deletion
  acts_as_paranoid

  # Define Uploader
  mount_uploader :image, PlatformImageUploader

  # Operation Audit
  acts_as_audited

  def location
    {:latitude => self.latitude, :longitude => self.longitude}
  end

  def location=(location)
    self.latitude = location[:latitude]
    self.longitude = location[:longitude]
  end

  def latitude_rad
    @latitude_rad ||= self.latitude * BigMath::PI(14) / 180
    @latitude_rad
  end

  def longitude_rad
    @longitude_rad ||= self.longitude * BigMath::PI(14) / 180
    @longitude_rad
  end

end
