# == Schema Information
#
# Table name: vehicle_notifications
#
#  id                   :integer(4)      not null, primary key
#  in_vehicle_device_id :integer(4)      not null
#  body                 :text
#  read_at              :datetime
#  operator_id          :integer(4)      not null
#  created_at           :datetime        not null
#  updated_at           :datetime        not null
#  response             :integer(4)
#

class VehicleNotification < ActiveRecord::Base
  # Define Relation
  belongs_to :operator
  belongs_to :in_vehicle_device

  # Define Validation
  validates_presence_of :operator_id, :in_vehicle_device_id
  
  # Operation Audit
  acts_as_audited
end
