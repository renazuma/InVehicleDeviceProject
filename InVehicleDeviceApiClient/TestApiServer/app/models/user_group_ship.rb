# == Schema Information
#
# Table name: user_group_ships
#
#  id            :integer(4)      not null, primary key
#  user_id       :integer(4)
#  user_group_id :integer(4)
#  created_at    :datetime        not null
#  updated_at    :datetime        not null
#

class UserGroupShip < ActiveRecord::Base
  belongs_to :user
  belongs_to :user_group
end
