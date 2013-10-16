# == Schema Information
#
# Table name: vehicle_notification_templates
#
#  id                  :integer(4)      not null, primary key
#  title               :string(255)
#  body                :text
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  deleted_at          :datetime
#  service_provider_id :integer(4)
#

class VehicleNotificationTemplate < ActiveRecord::Base
  # Define For Logical Deletion
  acts_as_paranoid

  # Operation Audit
  acts_as_audited

  # Define validate
  validates_presence_of :title, :body

  # Defile Relations
  belongs_to :service_provider
end
