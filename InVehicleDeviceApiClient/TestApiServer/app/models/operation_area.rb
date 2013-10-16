# == Schema Information
#
# Table name: operation_areas
#
#  id              :integer(4)      not null, primary key
#  service_unit_id :integer(4)
#  demand_area_id  :integer(4)
#  start_time      :time            not null
#  end_time        :time            not null
#  shift_type      :string(255)
#  created_at      :datetime        not null
#  updated_at      :datetime        not null
#  deleted_at      :datetime
#

class OperationArea < ActiveRecord::Base

  # Define Validation
  # 保存前に整合性をチェック
  validate :valid_shift?
  validates_presence_of :demand_area_id, :service_unit_id, :start_time, :end_time

  # Define Relation
  belongs_to :service_unit
  belongs_to :demand_area

  # Operation Audit
  acts_as_audited

  #
  # 指定号車、指定日に有効な稼働時間を取得
  #
  def self.find_shifts_of(service_unit_id, shift_type = nil)
    where(:service_unit_id => service_unit_id)
      .where(:shift_type => shift_type)
      .order("date_format(start_time, '%H%i')")
  end

  private

  #
  # 稼働時間が有効化かのチェック処理
  #
  def valid_shift?

    # 時刻の日付を整え
    if self.end_time.nil? || self.start_time.nil?
      return false
    end
    self.start_time = DateTime.new(2000, 01, 01, self.start_time.hour, self.start_time.min)
    self.end_time = DateTime.new(2000, 01, 01, self.end_time.hour, self.end_time.min)

    # 開始時間、終了日時間のチェック
    unless valid_time?
      errors.add(:end_time, :invalid_time)
      return false
    end

    # 更新時にチェックするときは自分のIDは除く
    shifts = self.class.find_shifts_of(self.service_unit_id, self.shift_type)
    shifts = shifts.where(["id <> ?", self.id.presence]) if self.id

    # NG 時間が重複
    if shifts.index{ |shift| duplicate_time?(shift) }
      errors.add(:base, :overlapped_time)
      return false
    end

    # OK チェック処理を通り抜けた場合
    return true
  end


  # 時間の重複チェック
  def duplicate_time?(shift)
    self.start_time <= shift.end_time && self.end_time >= shift.start_time
  end

  # 開始時間と終了時間の整合性をチェック
  def valid_time?
    return false unless self.start_time && self.end_time
    self.start_time <= self.end_time
  end
end
