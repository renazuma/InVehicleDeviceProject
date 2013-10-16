# == Schema Information
#
# Table name: service_unit_status_logs
#
#  id              :integer(4)      not null, primary key
#  service_unit_id :integer(4)
#  latitude        :decimal(17, 14) not null
#  longitude       :decimal(17, 14) not null
#  orientation     :integer(4)
#  temperature     :integer(4)
#  status          :integer(4)
#  created_at      :datetime        not null
#  updated_at      :datetime        not null
#

class ServiceUnitStatusLog < ActiveRecord::Base
  belongs_to :service_unit
end
