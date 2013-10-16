class VehiclesController < ApplicationController
  def index
    @vehicles = Vehicle.all
    respond_to do | format |
      format.json do
        render json: @vehicles.to_json
      end
    end
  end

  def show
    @vehicle = Vehicle.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @vehicle.to_json
      end
    end
  end

  def create
    @vehicle = Vehicle.new(params[:vehicle])

    respond_to do |format|
      if @vehicle.save
        format.json { render json: @vehicle, status: :created}
      else
        format.json { render json: @vehicle.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @vehicle = Vehicle.find(params[:id])
    @vehicle.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

