# coding: utf-8
# == Schema Information
#
# Table name: unit_assignments
#
#  id                  :integer(4)      not null, primary key
#  name                :string(255)     not null
#  working             :boolean(1)      default(TRUE), not null
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  deleted_at          :datetime
#  service_provider_id :integer(4)
#

class UnitAssignment < ActiveRecord::Base
  # Define Validation
  validates_presence_of :name

  # Define Relation
  belongs_to :service_provider
  has_many :service_units, :order => "activated_at desc", :dependent => :destroy
  has_many :reservations, :order => "departure_time"
  has_many :reservation_candidates
  has_many :operation_schedules, :order => "departure_estimate", :dependent => :destroy
  has_many :pre_route_plans

  # Define For Logical Deletion
  acts_as_paranoid

  # Delegation
  delegate :capacity, :to => :current_service_unit, :allow_nil => true

  accepts_nested_attributes_for :service_units, :reject_if => lambda {|i|
    i[:activated_at].blank? || i[:driver_id].blank? || i[:vehicle_id].blank? || i[:in_vehicle_device_id].blank?
  }, :allow_destroy => true

  def name_for_human
    name + "号車"
  end

  def current_service_unit
    service_units.where("activated_at <= ?", Date.today).first
  end

  def next_service_unit
    service_units.where("activated_at > ?", Date.today).last
  end

  # 事前経路探索のために運行可能範囲を返す
  def self.get_data_for_cluster_pre_process
    result = []
    UnitAssignment.scoped.each do |u|
      if u.current_service_unit
        u.current_service_unit.operation_areas.each do |operation_area|
          operation_area.demand_area.platforms.each do |platform|
            result << ActiveSupport::HashWithIndifferentAccess.new({:unit_assignment_id => u.id, :platform_id => platform.id, :start_time => operation_area.start_time, :end_time => operation_area.end_time})
          end
        end
      end
    end
    result
  end
end
