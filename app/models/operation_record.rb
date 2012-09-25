# == Schema Information
#
# Table name: operation_records
#
#  id                    :integer(4)      not null, primary key
#  departed_at           :datetime        not null
#  arrived_at            :datetime        not null
#  service_unit_id       :integer(4)
#  operation_schedule_id :integer(4)
#  created_at            :datetime        not null
#  updated_at            :datetime        not null
#

class OperationRecord < ActiveRecord::Base
  # Define Relations
  belongs_to :service_unit
  belongs_to :operation_schedule

  # Defilne Validations
  validates_presence_of :operation_schedule

  # Scope
  default_scope -> { includes(:operation_schedule) }

  # Delegation
  delegate :platform, :to => :operation_schedule, :allow_nil => true
  delegate :unit_assignment, :to => :operation_schedule, :allow_nil => true
  delegate :reservations_as_departure, :to => :operation_schedule, :allow_nil => true
  delegate :reservations_as_arrival, :to => :operation_schedule, :allow_nil => true
end
