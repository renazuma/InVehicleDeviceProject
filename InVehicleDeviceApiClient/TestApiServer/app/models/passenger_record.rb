# == Schema Information
#
# Table name: passenger_records
#
#  id                              :integer(4)      not null, primary key
#  get_on_time                     :datetime        not null
#  get_off_time                    :datetime
#  passenger_count                 :integer(4)      not null
#  payment                         :integer(4)
#  reservation_id                  :integer(4)
#  departure_operation_schedule_id :integer(4)      not null
#  arrival_operation_schedule_id   :integer(4)
#  created_at                      :datetime        not null
#  updated_at                      :datetime        not null
#  deleted_at                      :datetime
#  timestamp                       :datetime
#  service_provider_id             :integer(4)
#

class PassengerRecord < ActiveRecord::Base
  # Define Relations
  belongs_to :service_provider
  belongs_to :reservation
  belongs_to :user
  belongs_to :departure_operation_schedule, :class_name => "OperationSchedule"
  belongs_to :arrival_operation_schedule, :class_name => "OperationSchedule"

  # Define Validation
  validates_presence_of :passenger_count, :departure_operation_schedule, :arrival_operation_schedule

  # Define For Logical Deletion
  acts_as_paranoid
end
