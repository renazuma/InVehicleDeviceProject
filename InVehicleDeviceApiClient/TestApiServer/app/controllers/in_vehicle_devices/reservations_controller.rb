class InVehicleDevices::ReservationsController < ApplicationController
  # POST /in_vehicle_devices/reservations/search(.:format)
  def search
    respond_to do |format|
      format.json do
        render json: [
          {
            id: 10, departure_time: Date.today, arrival_time: Date.today, passenger_count: 1, created_at: Date.today, updated_at: Date.today,
            departure_platform_id: 100,
            departure_platform: {id: 100},
            arrival_platform_id: 200,
            arrival_platform: {id: 200},
          },
          {
            id: 11, departure_time: Date.today, arrival_time: Date.today, passenger_count: 1, created_at: Date.today, updated_at: Date.today,
            departure_platform_id: 200,
            departure_platform: {id: 200},
            arrival_platform_id: 300,
            arrival_platform: {id: 300},
          },
          {
            id: 12, departure_time: Date.today, arrival_time: Date.today, passenger_count: 1, created_at: Date.today, updated_at: Date.today,
            departure_platform_id: 300,
            departure_platform: {id: 300},
            arrival_platform_id: 400,
            arrival_platform: {id: 400},
          },
        ]
      end
    end
  end

  # POST /in_vehicle_devices/reservations(.:format)
  def create
    respond_to do |format|
      format.json do
        render json: {
          id: 1, departure_time: Date.today, arrival_time: Date.today, passenger_count: 1,
          created_at: Date.today, updated_at: Date.today,
          departure_platform_id: 1,
          departure_platform: {id: 1},
          arrival_platform_id: 2,
          arrival_platform: {id: 2},
        }.to_json
      end
    end
  end
end
