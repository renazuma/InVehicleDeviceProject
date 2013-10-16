class VehicleNotificationsController < ApplicationController
  def index
    @vehicle_notifications = VehicleNotification.all
    respond_to do | format |
      format.json do
        render json: @vehicle_notifications.to_json(:include => { :operator => {}})
      end
    end
  end

  def show
    @vehicle_notification = VehicleNotification.find(params[:id])

    respond_to do |format|
      format.json { render json: @vehicle_notification.to_json }
    end
  end

  def create
    @vehicle_notification = VehicleNotification.new(params[:vehicle_notification
])

    respond_to do |format|
      if @vehicle_notification.save
        format.json { render json: @vehicle_notification, status: :created}
      else
        format.json { render json: @vehicle_notification.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @vehicle_notification = VehicleNotification.find(params[:id])
    @vehicle_notification.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

  def truncate
    VehicleNotification.truncate

    respond_to do |format|
      format.json { head :no_content }
    end
  end
end

