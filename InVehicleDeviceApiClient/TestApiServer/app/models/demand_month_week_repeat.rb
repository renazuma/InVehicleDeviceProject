# == Schema Information
#
# Table name: demand_repeat_rules
#
#  id         :integer(4)      not null, primary key
#  type       :string(255)
#  start_at   :date
#  end_at     :date
#  demand_id  :integer(4)
#  sunday     :boolean(1)
#  monday     :boolean(1)
#  tuesday    :boolean(1)
#  wednesday  :boolean(1)
#  thursday   :boolean(1)
#  friday     :boolean(1)
#  saturday   :boolean(1)
#  nth_week   :integer(4)
#  created_at :datetime        not null
#  updated_at :datetime        not null
#

class DemandMonthWeekRepeat < DemandRepeatRule
  validates_presence_of :nth_week
end
