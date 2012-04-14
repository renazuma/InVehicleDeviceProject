# == Schema Information
#
# Table name: demands
#
#  id                    :integer(4)      not null, primary key
#  departure_time        :datetime
#  arrival_time          :datetime
#  passenger_count       :integer(4)      not null
#  stoppage_time         :integer(4)
#  memo                  :string(255)
#  departure_platform_id :integer(4)
#  arrival_platform_id   :integer(4)
#  unit_assignment_id    :integer(4)
#  user_id               :integer(4)      not null
#  created_at            :datetime        not null
#  updated_at            :datetime        not null
#  deleted_at            :datetime
#  service_provider_id   :integer(4)
#  repeat                :boolean(1)      default(FALSE)
#

#require "odt/reservation_generator"

class Demand < ActiveRecord::Base
  # Define Validation
  validates_presence_of :departure_time, :if => Proc.new {|demand| demand.arrival_time.nil?}
  validates_presence_of :arrival_time, :if => Proc.new {|demand| demand.departure_time.nil?}
  validates_presence_of :departure_platform_id, :arrival_platform_id

  validates_presence_of :passenger_count, :user

  # Define Relation
  belongs_to :service_provider
  belongs_to :unit_assignment
  belongs_to :user
  has_one :reservation
  has_many :reservation_candidates
  belongs_to :departure_platform, :class_name => "Platform", :foreign_key => "departure_platform_id"
  belongs_to :arrival_platform, :class_name => "Platform", :foreign_key => "arrival_platform_id"
  has_one :demand_repeat_rule

  # Define For Logical Deletion
  acts_as_paranoid

  # 予約候補検索用パラメーター
  def params_for_reservation_candidates
    {
      :departure_time => self.departure_time,
      :departure_platform_id => self.departure_platform.id,
      :departure_latitude => self.departure_platform.latitude,
      :departure_longitude => self.departure_platform.longitude,
      :arrival_time => self.arrival_time,
      :arrival_platform_id => self.arrival_platform.id,
      :arrival_latitude => self.arrival_platform.latitude,
      :arrival_longitude => self.arrival_platform.longitude,
      :unit_assignment_id => self.unit_assignment.id,
    }
  end

  # TODO: 仮実装
  def search_reservation_candidates
    reservation_candidates_attrs = Odt::ReservationGenerator.search_candidates(self.params_for_reservation_candidates)

    result = []
    reservation_candidates_attrs.to_a.each do |attrs|
      reservation_candidate = ReservationCandidate.new(attrs.merge({
        :passenger_count => self.passenger_count,
        :user => self.user,
        :unit_assignment => self.unit_assignment,
        :service_provider => self.service_provider,
      }))
      if reservation_candidate.save
        result << reservation_candidate
      else
        logger.warn(reservation_candidate.errors.full_messages)
      end
    end
    result
  end

  # TODO: 仮実装
  def search_and_decide_reservation_candidates
    reservation_candidates = self.search_reservation_candidates
    unless reservation_candidates.empty?
      reservation = reservation_candidates.first.to_reservation
      reservation.save
    else
      false
    end
  end

  # 繰り返しルールにマッチする予約希望を検索する
  def self.find_repeated_by_date(date)
    day_name =  case date.wday
                  when 0
                   "sunday"
                  when 1
                    "monday"
                  when 2
                    "tuesday"
                  when 3
                    "wednesday"
                  when 4
                    "thursday"
                  when 5
                    "friday"
                  when 6
                    "saturday"
                  end
    nth_week = (date.day - 1) / 7 + 1

    query1 = ActiveRecord::Base.connection.to_sql(self.joins(:demand_repeat_rule).where("type = ? AND #{day_name} = ?", "DemandWeekRepeat", true))
    query2 = ActiveRecord::Base.connection.to_sql(self.joins(:demand_repeat_rule).where("type = ? AND #{day_name} = ? AND nth_week = ?", "DemandMonthWeekRepeat", true, nth_week))
    Demand.find_by_sql("#{query1} UNION DISTINCT #{query2}")
  end
end
