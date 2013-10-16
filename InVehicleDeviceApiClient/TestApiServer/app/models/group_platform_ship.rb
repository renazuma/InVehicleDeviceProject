# == Schema Information
#
# Table name: group_platform_ships
#
#  id            :integer(4)      not null, primary key
#  user_group_id :integer(4)
#  platform_id   :integer(4)
#  created_at    :datetime        not null
#  updated_at    :datetime        not null
#

class GroupPlatformShip < ActiveRecord::Base
  belongs_to :user_group
  belongs_to :platform
end
