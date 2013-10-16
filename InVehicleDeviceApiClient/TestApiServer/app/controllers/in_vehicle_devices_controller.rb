class InVehicleDevicesController < ApplicationController
  def index
    @in_vehicle_devices = InVehicleDevice.all
    respond_to do | format |
      format.json do
        render json: @in_vehicle_devices.to_json
      end
    end
  end

  def show
    @in_vehicle_device = InVehicleDevice.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @in_vehicle_device.to_json
      end
    end
  end

  def create
    @in_vehicle_device = InVehicleDevice.new(params[:in_vehicle_device])

    respond_to do |format|
      if @in_vehicle_device.save
        format.json { render json: @in_vehicle_device.to_json(:force_except => [:encrypted_password, :login]), status: :created}
puts "before: #{@in_vehicle_device.authentication_token}"
@in_vehicle_device.reload
puts "after reload: #{@in_vehicle_device.authentication_token}"

      else
        format.json { render json: @in_vehicle_device.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @in_vehicle_device = InVehicleDevice.find(params[:id])
    @in_vehicle_device.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

