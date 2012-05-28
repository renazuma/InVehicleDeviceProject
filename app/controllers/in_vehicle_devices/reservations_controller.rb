class InVehicleDevices::ReservationsController < ApplicationController
  # POST /in_vehicle_devices/reservations/search(.:format)
  def search
    respond_to do |format|
      format.json do
        render json: [
          {id: 1, departure_time: Date.today, arrival_time: Date.today, passenger_count: 1, departure_platform_id: 1, arrival_platform_id: 2, created_at: Date.today, updated_at: Date.today},
          {id: 2, departure_time: Date.today, arrival_time: Date.today, passenger_count: 1, departure_platform_id: 1, arrival_platform_id: 2, created_at: Date.today, updated_at: Date.today},
          {id: 3, departure_time: Date.today, arrival_time: Date.today, passenger_count: 1, departure_platform_id: 1, arrival_platform_id: 2, created_at: Date.today, updated_at: Date.today},
        ]
      end
    end
  end

  # POST /in_vehicle_devices/reservations(.:format)
  def create
    respond_to do |format|
      format.json do
        render json:
          {id: 1, departure_time: Date.today, arrival_time: Date.today, passenger_count: 1, departure_platform_id: 1, arrival_platform_id: 2, created_at: Date.today, updated_at: Date.today}.to_json
      end
    end
  end
end
