# == Schema Information
#
# Table name: operators
#
#  id                   :integer(4)      not null, primary key
#  login                :string(255)     not null
#  last_name            :string(255)     not null
#  first_name           :string(255)     not null
#  email                :string(255)
#  encrypted_password   :string(255)     default(""), not null
#  remember_created_at  :datetime
#  sign_in_count        :integer(4)      default(0)
#  current_sign_in_at   :datetime
#  last_sign_in_at      :datetime
#  current_sign_in_ip   :string(255)
#  last_sign_in_ip      :string(255)
#  created_at           :datetime        not null
#  updated_at           :datetime        not null
#  deleted_at           :datetime
#  authentication_token :string(255)
#  service_provider_id  :integer(4)
#

class Operator < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :token_authenticatable, :encryptable, :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable,
         :rememberable, :trackable, :token_authenticatable

  # Setup accessible (or protected) attributes for your model
  attr_accessible :login, :email, :last_name, :first_name, :password, :password_confirmation, :remember_me, :authentication_token, :service_provider

  # Define Validation
  validates_presence_of :login, :last_name, :first_name, :service_provider
  validates_uniqueness_of :login
  validates_uniqueness_of :email, :allow_nil => true

  default_scope lambda { joins(:service_provider).includes(:service_provider) }

  # Define Relation
  belongs_to :service_provider
  has_many :reservations
  has_many :role_ships, :as => :roleable
  has_many :roles, :through => :role_ships

  # Define Delegate
  delegate :in_vehicle_devices, :drivers, :vehicles, :to => :service_provider

  # Define Callback
  before_validation :email_blank_to_nil
  before_save :ensure_authentication_token!

  # Define For Logical Deletion
  acts_as_paranoid

  # Operation Audit
  acts_as_audited

  def fullname
    last_name + " " + first_name
  end

  def role?(role_name)
    !!self.roles.find_by_name(role_name.to_s)
  end

  private
  def email_blank_to_nil
    self.email = nil if email == ""
  end
end
