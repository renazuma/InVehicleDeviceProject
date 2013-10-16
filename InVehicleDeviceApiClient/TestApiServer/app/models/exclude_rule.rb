# == Schema Information
#
# Table name: exclude_rules
#
#  id                    :integer(4)      not null, primary key
#  exclude_date          :date
#  demand_repeat_rule_id :integer(4)
#  created_at            :datetime        not null
#  updated_at            :datetime        not null
#

class ExcludeRule < ActiveRecord::Base
  belongs_to :demand_repeat_rule
end
