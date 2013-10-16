class RoleShip < ActiveRecord::Base
  attr_accessible :roleable_id, :roleable_type

  # Define Relations
  belongs_to :roleable, :polymorphic => true
  belongs_to :role
end
