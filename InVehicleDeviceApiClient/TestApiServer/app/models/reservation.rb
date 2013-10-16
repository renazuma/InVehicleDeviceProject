# coding: utf-8
# == Schema Information
#
# Table name: reservations
#
#  id                    :integer(4)      not null, primary key
#  departure_time        :datetime        not null
#  departure_lock        :boolean(1)      default(FALSE)
#  arrival_time          :datetime        not null
#  arrival_lock          :boolean(1)      default(FALSE)
#  passenger_count       :integer(4)      not null
#  memo                  :string(255)
#  payment               :integer(4)      default(0), not null
#  status                :integer(4)      default(1), not null
#  user_id               :integer(4)      not null
#  demand_id             :integer(4)      not null
#  unit_assignment_id    :integer(4)      not null
#  operator_id           :integer(4)
#  departure_platform_id :integer(4)
#  arrival_platform_id   :integer(4)
#  created_at            :datetime        not null
#  updated_at            :datetime        not null
#  deleted_at            :datetime
#  departure_schedule_id :integer(4)
#  arrival_schedule_id   :integer(4)
#  service_provider_id   :integer(4)
#  transferred_at        :datetime
#

class Reservation < ActiveRecord::Base
  # Define Validation
  validates_presence_of :departure_time, :arrival_time, :departure_platform, :arrival_platform, :user, :unit_assignment

  # Define Relations
  belongs_to :service_provider
  belongs_to :user
  belongs_to :operator
  belongs_to :demand
  belongs_to :unit_assignment
  belongs_to :departure_platform, :class_name => "Platform", :foreign_key => "departure_platform_id"
  belongs_to :arrival_platform, :class_name => "Platform", :foreign_key => "arrival_platform_id"
  belongs_to :departure_schedule, :class_name => "OperationSchedule", :foreign_key => "departure_schedule_id"
  belongs_to :arrival_schedule, :class_name => "OperationSchedule", :foreign_key => "arrival_schedule_id"

  # Define Scopes
  scope :for_cluster, lambda {|time| where("WEEKDAY(departure_time + interval :offset second) = WEEKDAY(:time + interval :offset second)", {:offset => time.utc_offset, :time => time}).includes([:departure_platform, :arrival_platform]).order("departure_time DESC")}

  # Define For Logical Deletion
  acts_as_paranoid

  # Operation Audit
  acts_as_audited

  # Define Const
  COUNT_FOR_CLUSTER = 1000


  # クラスタリング計算のために必要なパラメーターをまとめる。
  def to_hash_for_cluster
    ActiveSupport::HashWithIndifferentAccess.new(
      {
        :departure_latitude => self.departure_platform.latitude.to_f,
        :departure_longitude => self.departure_platform.longitude.to_f,
        :departure_time => self.departure_time,
        :departure_platform_id => self.departure_platform.id,
        :arrival_latitude => self.arrival_platform.latitude.to_f,
        :arrival_longitude => self.arrival_platform.longitude.to_f,
        :arrival_time => self.arrival_time,
        :arrival_platform_id => self.arrival_platform.id,
        :service_provider_id => self.service_provider.try(:id)
      }
    )
  end

  def to_json_for_cluster
    to_hash_for_cluster.to_json
  end

  def self.get_history_for_cluster(time)
    self.for_cluster(time).map{|r| r.to_hash_for_cluster}
  end


  # 予約希望から予約オブジェクトを作成する
  def self.build_from_demand(demand)
    r = self.new
    r.departure_time = demand.departure_time
    r.arrival_time = demand.arrival_time
    r.departure_platform = demand.departure_platform
    r.arrival_platform = demand.arrival_platform
    r.passenger_count = demand.passenger_count
    r.memo = demand.memo
    r.user = demand.user
    r.unit_assignment = demand.unit_assignment
    r.demand = demand
    r
  end

  # ビューに表示するためのdeparture_platformとarrival_platformの合成文字列を返す
  def platforms_for_view
    departure = departure_platform ? departure_platform.name : I18n.t("activerecord.models.deleted")
    arrival = arrival_platform ? arrival_platform.name : I18n.t("activerecord.models.deleted")
    "#{departure} ⇒ #{arrival}"
  end

  # ビューに表示するためのdeparture_timeとarrival_timeの合成文字列を返す
  def times_for_view
    "#{I18n.l departure_time, :format => :middle} ⇒ #{I18n.l arrival_time, :format => :nodate}"
  end

  protected
  # 自身のデータからOperationScheduleを作成する
  def build_departure_schedule_with_self_data
    operation_schedule = self.build_departure_schedule_without_self_data
    operation_schedule.departure_estimate = self.departure_time
    operation_schedule.arrival_estimate = self.departure_time.since(self.demand.stoppage_time)
    operation_schedule.platform = self.departure_platform
    operation_schedule.unit_assignment = self.unit_assignment
    operation_schedule
  end
  alias_method_chain :build_departure_schedule, :self_data

end
