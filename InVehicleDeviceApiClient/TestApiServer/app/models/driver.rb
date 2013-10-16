# == Schema Information
#
# Table name: drivers
#
#  id                  :integer(4)      not null, primary key
#  last_name           :string(255)     not null
#  first_name          :string(255)     not null
#  telephone_number    :string(255)     not null
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  deleted_at          :datetime
#  service_provider_id :integer(4)
#

class Driver < ActiveRecord::Base
  # Define Validation
  validates_presence_of :last_name, :first_name, :telephone_number
  validates_format_of :telephone_number, :with => /^(?:[0-9]+[\-\s]?)+[0-9]+$/

  # Define Relation
  belongs_to :service_provider
  has_many :service_units

  # Operation Audit
  acts_as_audited

  # Define For Logical Deletion
  acts_as_paranoid

  def fullname
    last_name + " " + first_name
  end
end
