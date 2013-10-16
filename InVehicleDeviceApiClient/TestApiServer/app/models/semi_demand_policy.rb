# coding: utf-8
# == Schema Information
#
# Table name: semi_demand_policies
#
#  id                  :integer(4)      not null, primary key
#  name                :string(255)     not null
#  pickup_average      :integer(4)      not null
#  departure_time      :string(255)
#  arrival_time        :string(255)
#  unit_assignment_id  :integer(4)
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  service_provider_id :integer(4)
#

class SemiDemandPolicy < ActiveRecord::Base
  # Define Relations
  belongs_to :service_provider
  has_many :semi_demand_course_orders, :order => "position ASC", :inverse_of => :semi_demand_policy, :dependent => :delete_all
  has_many :demand_areas, :through => :semi_demand_course_orders, :order => "position ASC"
  belongs_to :unit_assignment

  # Operation Audit
  acts_as_audited

  # Define Validation
  validates_presence_of :name, :unit_assignment
  validates_presence_of :departure_time, :if => Proc.new {|record| record.arrival_time.blank?}
  validates_presence_of :arrival_time, :if => Proc.new {|record| record.departure_time.blank?}
  validates_format_of :departure_time, :with => /^\d{1,2}:\d{1,2}$/, :allow_nil => true
  validates_format_of :arrival_time, :with => /^\d{1,2}:\d{1,2}$/, :allow_nil => true

  accepts_nested_attributes_for :semi_demand_course_orders, :allow_destroy => true

  #TODO: pickup_averageの持ち先が確定したら本格実装
  before_save do |record|
    record.pickup_average = 5
  end

  before_validation do |record|
    record.departure_time.gsub!(/：/, ":") if record.departure_time
    record.arrival_time.gsub!(/：/, ":") if record.arrival_time
  end
end
