# == Schema Information
#
# Table name: initial_vectors
#
#  id                  :integer(4)      not null, primary key
#  departure_latitude  :decimal(17, 14)
#  departure_longitude :decimal(17, 14)
#  arrival_latitude    :decimal(17, 14)
#  arrival_longitude   :decimal(17, 14)
#  time_scope          :integer(4)
#  service_provider_id :integer(4)
#  created_at          :datetime        not null
#  updated_at          :datetime        not null
#

class InitialVector < ActiveRecord::Base
  belongs_to :service_provider

  # Define Const
  TIME_SCOPE_1 = 1
  TIME_SCOPE_2 = 2
  TIME_SCOPE_3 = 3
  TIME_SCOPE_4 = 4
  TIME_SCOPE_5 = 5
  TIME_SCOPE_6 = 6
  TIME_SCOPES_FOR_OPTION = [["06:00 - 10:00", 1], ["10:00 - 14:00", 2], ["14:00 - 18:00", 3]]
end
