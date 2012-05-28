class InVehicleDevices::ReservationsController < ApplicationController
  # POST /in_vehicle_devices/reservations/search(.:format)
  def search
    respond_to do |format|
      format.json do
        @reservation_candidates = [
          ReservationCandidate.new,
          ReservationCandidate.new,
          ReservationCandidate.new,
        ]

        if @reservation_candidates.empty?
          render text: "no reservation candidate", status: :not_found
        else
          render json: @reservation_candidates
        end
      end
    end
  end

  # POST /in_vehicle_devices/reservations(.:format)
  def create
    respond_to do |format|
      format.json do
        render json: Reservation.new
      end
    end
  end
end
