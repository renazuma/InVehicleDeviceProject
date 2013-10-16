# == Schema Information
#
# Table name: user_groups
#
#  id                  :integer(4)      not null, primary key
#  name                :string(255)     not null
#  telephone_number    :string(255)
#  memo                :string(255)
#  zipcode             :string(255)
#  address             :string(255)
#  head_user_id        :integer(4)
#  service_provider_id :integer(4)
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#  deleted_at          :datetime
#

class UserGroup < ActiveRecord::Base
  # Define Relations
  has_many :user_group_ships
  has_many :users, :through => :user_group_ships
  has_many :group_platform_ships, :dependent => :destroy
  has_many :platforms, :through => :group_platform_ships
  belongs_to :head_user, :class_name => "User", :foreign_key => "head_user_id"
  belongs_to :service_provider

  # Define Validations
  validates_format_of :telephone_number, :with => /^(?:[0-9]+[\-\s]?)+[0-9]+$/, :allow_nil => true
end
