# encoding: utf-8
# == Schema Information
#
# Table name: vehicles
#
#  id                  :integer(4)      not null, primary key
#  number              :string(255)     not null
#  model_name          :string(255)     default(""), not null
#  capacity            :integer(4)      default(0), not null
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  deleted_at          :datetime
#  image               :string(255)
#  service_provider_id :integer(4)
#


class Vehicle < ActiveRecord::Base
  # Define Validation
  validates_presence_of :number, :model_name, :capacity
  validates_inclusion_of :capacity, :in => 1..100, :message => "が範囲外の値です。"
  validates_uniqueness_of :number
  # Define Relation
  belongs_to :service_provider
  has_many :service_units

  # Operation Audit
  acts_as_audited

  # Define For Logical Deletion
  acts_as_paranoid

  # Define Uploader
#  mount_uploader :image, VehicleImageUploader

  def number_and_model
    number + " " + model_name
  end
end
