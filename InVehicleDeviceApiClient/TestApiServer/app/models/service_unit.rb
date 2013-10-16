# == Schema Information
#
# Table name: service_units
#
#  id                   :integer(4)      not null, primary key
#  activated_at         :date
#  driver_id            :integer(4)      not null
#  vehicle_id           :integer(4)      not null
#  in_vehicle_device_id :integer(4)
#  unit_assignment_id   :integer(4)
#  created_at           :datetime        not null
#  updated_at           :datetime        not null
#  deleted_at           :datetime
#

class ServiceUnit < ActiveRecord::Base
  # Define Validation
  validates_presence_of :activated_at, :driver_id, :vehicle_id
  validates_uniqueness_of :activated_at, :scope => [:unit_assignment_id]

  # Define For Logical Deletion
  acts_as_paranoid

  # Define Relation
  belongs_to :driver
  belongs_to :vehicle
  belongs_to :in_vehicle_device
  belongs_to :unit_assignment
  has_many :operation_records, :dependent => :nullify
  has_many :operation_areas, :dependent => :delete_all, :order => "start_time"

  # Delegation
  delegate :capacity, :to => :vehicle

  #
  # 運行可範囲を取得する
  #
  def find_areas(shift_type = nil)
    OperationArea.find_shifts_of(self.id, shift_type)
  end

  #
  # 自分の次の時点号車情報を取得する
  #
  def next
    ServiceUnit
      .where(["activated_at > ?", self.activated_at])
      .where(:unit_assignment_id => self.unit_assignment_id)
      .order(:activated_at).first
  end

  #
  # 自分が持っている運行可能範囲情報のクローンの配列を作成
  #
  def clone_operation_areas
    operation_areas.map {|operation_area| operation_area.dup}
  end

  #
  # 休憩時間を取得する
  #
  def find_rest_times(shift_type = nil)
    shifts = OperationArea.find_shifts_of(self.id, shift_type)
    rest_time = []
    (1...shifts.size).each do |i|
      # １分以内の休憩時間は含めない
      if (shifts[i].start_time - shifts[i - 1].end_time) > 60
        rest_time << [shifts[i - 1].end_time, shifts[i].start_time]
      end
    end
    rest_time
  end
end
