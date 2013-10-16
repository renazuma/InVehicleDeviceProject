# == Schema Information
#
# Table name: in_vehicle_devices
#
#  id                  :integer(4)      not null, primary key
#  model_name          :string(255)     not null
#  type_number         :string(255)     not null
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  deleted_at          :datetime
#  service_provider_id :integer(4)
#

class InVehicleDevice < ActiveRecord::Base
  devise :database_authenticatable, :rememberable, :trackable, :token_authenticatable

  # Setup accessible (or protected) attributes for your model
  attr_accessible :id, :login, :password, :password_confirmation, :remember_me, :authentication_token, :model_name, :type_number, :service_provider_id, :service_units, :vehicle_notifications

  # Define Validation
  validates_presence_of :model_name, :type_number
  validates_uniqueness_of :login

  # Define Relation
  belongs_to :service_provider
  has_many :service_units
  has_many :vehicle_notifications

  # generate authentication token
  before_save :ensure_authentication_token

  # Operation Audit
  acts_as_audited

  # Define for Logical Deletion
  acts_as_paranoid

  def formatted_id
    sprintf("%03d", id)
  end

  def id_and_model
    formatted_id + " " + model_name
  end
end
