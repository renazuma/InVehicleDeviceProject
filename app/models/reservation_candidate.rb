# == Schema Information
#
# Table name: reservation_candidates
#
#  id                    :integer(4)      not null, primary key
#  departure_time        :datetime        not null
#  arrival_time          :datetime        not null
#  passenger_count       :integer(4)      not null
#  accuracy              :float
#  departure_platform_id :integer(4)      not null
#  arrival_platform_id   :integer(4)      not null
#  user_id               :integer(4)
#  demand_id             :integer(4)
#  unit_assignment_id    :integer(4)
#  service_provider_id   :integer(4)
#  created_at            :datetime        not null
#  updated_at            :datetime        not null
#  deleted_at            :datetime
#

class ReservationCandidate < ActiveRecord::Base
  # Define Validation
  validates_presence_of :departure_time, :arrival_time, :departure_platform, :arrival_platform

  # Define Relations
  belongs_to :service_provider
  belongs_to :user
  belongs_to :demand
  belongs_to :unit_assignment
  belongs_to :departure_platform, :class_name => "Platform", :foreign_key => "departure_platform_id"
  belongs_to :arrival_platform, :class_name => "Platform", :foreign_key => "arrival_platform_id"

  def to_reservation
    Reservation.new({
      :departure_time => self.departure_time,
      :arrival_time => self.arrival_time,
      :departure_platform => self.departure_platform,
      :arrival_platform => self.arrival_platform,
      :demand => self.demand,
      :user => self.user,
      :unit_assignment => self.unit_assignment,
      :service_provider => self.service_provider,
    })
  end
end
