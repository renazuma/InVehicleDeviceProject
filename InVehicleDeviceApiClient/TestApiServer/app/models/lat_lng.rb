# == Schema Information
#
# Table name: lat_lngs
#
#  id             :integer(4)      not null, primary key
#  latitude       :decimal(17, 14) not null
#  longitude      :decimal(17, 14) not null
#  demand_area_id :integer(4)
#  position       :integer(4)
#  head_area_id   :integer(4)
#  created_at     :datetime        not null
#  updated_at     :datetime        not null
#

class LatLng < ActiveRecord::Base
  # Define Validation
  validates_presence_of :latitude, :longitude
  validates_presence_of :demand_area, :unless => Proc.new {|record| !!record.head_area}
  validates_presence_of :head_area, :unless => Proc.new {|record| !!record.demand_area}

  belongs_to :demand_area, :inverse_of => :lat_lngs, :dependent => :delete
  belongs_to :head_area, :inverse_of => :lat_lng_as_head, :class_name => "DemandArea", :foreign_key => "head_area_id", :dependent => :delete
end
