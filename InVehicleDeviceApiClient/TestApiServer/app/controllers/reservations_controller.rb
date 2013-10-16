class ReservationsController < ApplicationController
  def index
    @reservations = Reservation.all
    respond_to do | format |
      format.json do
        render json: @reservations.to_json
      end
    end
  end

  def show
    @reservation = Reservation.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @reservation.to_json
      end
    end
  end

  def create
    @reservation = Reservation.new(params[:reservation])

    respond_to do |format|
      if @reservation.save
        format.json { render json: @reservation, status: :created}
      else
        format.json { render json: @reservation.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @reservation = Reservation.find(params[:id])
    @reservation.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

