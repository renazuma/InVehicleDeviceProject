# coding: utf-8
# == Schema Information
#
# Table name: users
#
#  id                     :integer(4)      not null, primary key
#  login                  :string(255)     not null
#  last_name              :string(255)     not null
#  first_name             :string(255)     not null
#  last_name_ruby         :string(255)     not null
#  first_name_ruby        :string(255)     not null
#  birthday               :date            not null
#  age                    :integer(4)      not null
#  sex                    :integer(4)      default(1), not null
#  address                :string(500)     not null
#  telephone_number       :string(255)     not null
#  email                  :string(255)
#  encrypted_password     :string(255)     default(""), not null
#  remember_created_at    :datetime
#  sign_in_count          :integer(4)      default(0)
#  current_sign_in_at     :datetime
#  last_sign_in_at        :datetime
#  current_sign_in_ip     :string(255)
#  last_sign_in_ip        :string(255)
#  created_at             :datetime        not null
#  updated_at             :datetime        not null
#  deleted_at             :datetime
#  telephone_number2      :string(255)
#  email2                 :string(255)
#  felica_id              :string(255)
#  needed_care            :boolean(1)
#  handicapped            :boolean(1)
#  wheelchair             :boolean(1)
#  recommend_ok           :boolean(1)
#  update_notification    :boolean(1)
#  recommend_notification :boolean(1)
#  reserve_notification   :boolean(1)
#  service_provider_id    :integer(4)
#

class User < ActiveRecord::Base
  # Include default devise modules. Others available are:
  # :token_authenticatable, :encryptable, :confirmable, :lockable, :timeoutable and :omniauthable
  devise :database_authenticatable

  # Setup accessible (or protected) attributes for your model
  attr_accessible :id, :login, :last_name, :first_name, :last_name_ruby, :first_name_ruby, :birthday, :age, :sex, :email, :email2, :password, :password_confirmation, :remember_me, :zip, :address, :telephone_number, :telephone_number2, :felica_id, :needed_care, :handicapped, :wheelchair, :recommend_ok, :update_notification, :recommend_notification, :reserve_notification, :service_provider_id

  # Operation Audit
  acts_as_audited :except => [:password]

  # Define For Logical Deletion
  acts_as_paranoid

  # Define Callback
  before_validation :calc_age, :email_blank_to_nil
  # 人のフルネームと同名のグループを作成する。
  after_create :create_initial_group

  # Define Validation
  validates_presence_of :login, :birthday, :age, :sex, :address, :telephone_number
  validates_presence_of :last_name, :unless => Proc.new {|record| record.first_name.blank?}
  validates_presence_of :last_name_ruby, :unless => Proc.new {|record| record.first_name_ruby.blank?}
  validates_presence_of :first_name, :unless => Proc.new {|record| record.last_name.blank?}
  validates_presence_of :first_name_ruby, :unless => Proc.new {|record| record.last_name_ruby.blank?}
  validates_presence_of :fullname, :if => Proc.new {|record| record.last_name.blank? && record.first_name.blank?}
  validates_presence_of :fullname_ruby, :if => Proc.new {|record| record.last_name_ruby.blank? && record.first_name_ruby.blank?}
  validates_uniqueness_of :login
  validates_uniqueness_of :email, :allow_blank => true, :allow_nil => true
  validates_numericality_of :age, :sex
  validates_format_of :telephone_number, :with => /^(?:[0-9]+[\-\s]?)+[0-9]+$/
  validates_format_of :email, :with => /\A([^@\s]+)@((?:[-a-z0-9]+\.)+[a-z]{2,})\Z/i, :allow_nil => true # cf. Rails API Document

  # Define Relation
  belongs_to :service_provider
  has_many :reservations
  has_many :reservation_candidates
  has_many :demands
  has_many :user_group_ships
  has_many :user_groups, :through => :user_group_ships
  has_one :user_group_as_head, :class_name => "UserGroup", :foreign_key => "head_user_id"

  # Define Constant
  MALE = 1
  FEMALE = 2

  def fullname
    last_name + " " + first_name if last_name && first_name
  end

  def fullname=(name)
    f, l = name.split(/[\s　]/)
    self.last_name = f
    self.first_name = l
  end

  def fullname_ruby
    last_name_ruby + " " + first_name_ruby if last_name_ruby && first_name_ruby
  end

  def fullname_ruby=(ruby)
    f, l = ruby.split(/[\s　]/)
    self.last_name_ruby = f
    self.first_name_ruby = l
  end

  def sex_for_view
    case sex
    when MALE
      "男性"
    when FEMALE
      "女性"
    end
  end

  def self.search_by_keyword(keyword = "")
    converted_keyword = keyword.gsub(/[　 ]/, " ")
    left_match_keyword = "#{converted_keyword }%"
    partial_match_keyword = "%#{converted_keyword}%"
    query =  ""
    query << " concat(last_name, first_name) like :name"
    query << " OR concat(last_name_ruby,  first_name_ruby) like :name"
    query << " OR telephone_number like :tel"
    query << " OR login like :login"
    where(query, 
          :name => partial_match_keyword,
          :tel =>  left_match_keyword,
          :login => left_match_keyword)
  end

  private
  def calc_age
    if self.birthday && self.age.nil?
      today = Date.today
      if self.birthday.month < today.month || (self.birthday.month == today.month && self.birthday.day <= today.day)
        self.age = today.year - self.birthday.year
      else
        self.age = today.year - self.birthday.year - 1
      end
    end
  end

  def email_blank_to_nil
    self.email = nil if self.email == ""
  end

  # MEMO: 何故かリロードしないとグループが二つ作られる。
  def create_initial_group
    self.user_groups.create(:name => self.fullname, :head_user_id => self.id)
    self.reload
  end
end
