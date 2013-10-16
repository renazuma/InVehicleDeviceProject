# == Schema Information
#
# Table name: system_admins
#
#  id                     :integer(4)      not null, primary key
#  login                  :string(255)     not null
#  email                  :string(255)
#  encrypted_password     :string(255)     default(""), not null
#  reset_password_token   :string(255)
#  reset_password_sent_at :datetime
#  remember_created_at    :datetime
#  sign_in_count          :integer(4)      default(0)
#  current_sign_in_at     :datetime
#  last_sign_in_at        :datetime
#  current_sign_in_ip     :string(255)
#  last_sign_in_ip        :string(255)
#  last_name              :string(255)
#  first_name             :string(255)
#  created_at             :datetime        not null
#  updated_at             :datetime        not null
#  deleted_at             :datetime
#

class SystemAdmin < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :token_authenticatable, :encryptable, :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable, :registerable,
         :rememberable, :trackable

  # Define Validation
  validates_presence_of :login
  validates_uniqueness_of :login
  validates_uniqueness_of :email, :allow_nil => true

  # Define Relations
  has_many :role_ships, :as => :roleable
  has_many :roles, :through => :role_ships

  # Setup accessible (or protected) attributes for your model
  attr_accessible :login, :email, :last_name, :first_name, :password, :password_confirmation, :remember_me

  # Define Callback
  before_validation :email_blank_to_nil

  # Define For Logical Deletion
  acts_as_paranoid

  def fullname
    self.last_name + " " + self.first_name
  end

  private
  def email_blank_to_nil
    self.email = nil if email == ""
  end
end
