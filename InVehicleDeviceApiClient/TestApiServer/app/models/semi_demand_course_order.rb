# == Schema Information
#
# Table name: semi_demand_course_orders
#
#  id                    :integer(4)      not null, primary key
#  position              :integer(4)
#  semi_demand_policy_id :integer(4)
#  demand_area_id        :integer(4)
#  created_at            :datetime        not null
#  updated_at            :datetime        not null
#

class SemiDemandCourseOrder < ActiveRecord::Base
  # Define Relations
  belongs_to :semi_demand_policy, :inverse_of => :semi_demand_course_orders
  belongs_to :demand_area

  # Define Validation
  validates_presence_of :position, :semi_demand_policy, :demand_area
end
