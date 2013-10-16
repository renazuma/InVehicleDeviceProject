# == Schema Information
#
# Table name: pre_route_plans
#
#  id                  :integer(4)      not null, primary key
#  cluster_list        :text
#  operation_date      :date
#  unit_assignment_id  :integer(4)
#  service_provider_id :integer(4)
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#

class PreRoutePlan < ActiveRecord::Base
  # Define Relations
  belongs_to :unit_assignment
  belongs_to :service_provider

  serialize :cluster_list, Array
end
