# coding: utf-8
# == Schema Information
#
# Table name: service_providers
#
#  id                       :integer(4)      not null, primary key
#  name                     :string(255)     not null
#  semi_demand              :boolean(1)      default(FALSE), not null
#  recommend                :boolean(1)      default(FALSE), not null
#  reservation_time_limit   :text            default(""), not null
#  reservation_start_date   :integer(4)      default(14), not null
#  must_contact_gap         :integer(4)      default(5), not null
#  semi_demand_extent_limit :integer(4)      default(5000), not null
#  created_at               :datetime        not null
#  updated_at               :datetime        not null
#  deleted_at               :datetime
#

class ServiceProvider < ActiveRecord::Base
  # Define Validation
  validates_uniqueness_of :name
  validates_presence_of :name, :reservation_time_limit, :reservation_start_date, :must_contact_gap, :semi_demand_extent_limit
  validates_inclusion_of :semi_demand, :recommend, :in => [true, false]

  serialize :reservation_time_limit

  ServiceProvider::OPERATOR_WEB = :operator_web
  ServiceProvider::CONSUMER_WEB = :consumer_web
  ServiceProvider::ANDROID_APP = :android_app
  ServiceProvider::MOBILE_WEB = :mobile_web
  ServiceProvider::IPHONE_APP = :iphone_app

  # Define For Logical Deletion
  acts_as_paranoid

  # Define Relations
  has_many :users, :dependent => :destroy
  has_many :operators, :dependent => :destroy
  has_many :platforms, :dependent => :destroy
  has_many :drivers, :dependent => :destroy
  has_many :in_vehicle_devices, :dependent => :destroy
  has_many :vehicles, :dependent => :destroy
  has_many :demand_areas, :dependent => :destroy
  has_many :passenger_records, :dependent => :destroy
  has_many :vehicle_notification_templates, :dependent => :destroy
  has_many :demands, :dependent => :destroy
  has_many :reservations, :dependent => :destroy
  has_many :reservation_candidates, :dependent => :destroy
  has_many :unit_assignments, :dependent => :destroy
  has_many :operation_schedules, :dependent => :destroy
  has_many :semi_demand_policies, :dependent => :destroy
  has_many :audits, :dependent => :destroy
  has_many :initial_vectors, :dependent => :destroy
  has_many :vector_clusters, :dependent => :destroy
  has_many :pre_route_plans, :dependent => :destroy
  has_many :user_groups, :dependent => :destroy

  before_validation do |record|
    if record.reservation_time_limit.blank? || !record.reservation_time_limit.is_a?(Hash)
      record.reservation_time_limit = {
        ServiceProvider::OPERATOR_WEB => 30,
        ServiceProvider::CONSUMER_WEB => 30,
        ServiceProvider::ANDROID_APP => 30,
        ServiceProvider::MOBILE_WEB => 30,
        ServiceProvider::IPHONE_APP => 30,
      }
    end
  end

end
