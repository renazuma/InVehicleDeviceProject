# == Schema Information
#
# Table name: operation_schedules
#
#  id                  :integer(4)      not null, primary key
#  departure_estimate  :datetime        not null
#  arrival_estimate    :datetime        not null
#  unit_assignment_id  :integer(4)
#  platform_id         :integer(4)
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  deleted_at          :datetime
#  service_provider_id :integer(4)
#

class OperationSchedule < ActiveRecord::Base
  # Define Relations
  belongs_to :service_provider
  belongs_to :unit_assignment
  belongs_to :platform
  has_many :reservations_as_departure, :class_name => "Reservation", :foreign_key => "departure_schedule_id"
  has_many :reservations_as_arrival, :class_name => "Reservation", :foreign_key => "arrival_schedule_id"
  has_one :operation_record

  # Define Validations
  validates_presence_of :departure_estimate, :arrival_estimate, :unit_assignment


  # Define For Logical Deletion
  acts_as_paranoid

  # Define Scopes
  scope :day, lambda {|d| where(:departure_estimate => d.beginning_of_day..d.end_of_day) }
  scope :before_equal_from, lambda {|record| day(record.departure_estimate).where("departure_estimate <= ? AND unit_assignment_id = ?", record.departure_estimate, record.unit_assignment.id)}

  # 運行一覧表示のjsonに渡すため
  def formatted_departure_estimate
    departure_estimate.strftime("%H:%M") if departure_estimate
  end

  def formatted_arrival_estimate
    arrival_estimate.strftime("%H:%M") if arrival_estimate
  end

  # 現在の運行での乗客数の変化量
  def delta_passenger_count
    increment = self.reservations_as_departure.sum {|r| r.passenger_count}
    decrement = self.reservations_as_arrival.sum {|r| r.passenger_count}
    return (increment - decrement)
  end


  # 現在の運行時点での残席を取得する
  # service_unitを持たないunit_assignmentと紐付いている場合は、nilを返す
  def remain
    if capacity = self.unit_assignment.capacity
      delta_sum = self.class.before_equal_from(self).sum {|schedule| schedule.delta_passenger_count}
      capacity - delta_sum
    end
  end
end
