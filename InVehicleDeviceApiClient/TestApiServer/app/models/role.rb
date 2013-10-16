class Role < ActiveRecord::Base
  attr_accessible :name

  # Define Relations
  has_many :role_ships
end
