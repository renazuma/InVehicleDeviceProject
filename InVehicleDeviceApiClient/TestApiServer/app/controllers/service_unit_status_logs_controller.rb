class ServiceUnitStatusLogsController < ApplicationController
  def index
    @service_unit_status_logs = ServiceUnitStatusLog.all
    respond_to do | format |
      format.json do
        render json: @service_unit_status_logs.to_json
      end
    end
  end

  def show
    @service_unit_status_log = ServiceUnitStatusLog.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @service_unit_status_log.to_json
      end
    end
  end

  def create
    @service_unit_status_log = ServiceUnitStatusLog.new(params[:service_unit_status_log])

    respond_to do |format|
      if @service_unit_status_log.save
        format.json { render json: @service_unit_status_log, status: :created}
      else
        format.json { render json: @service_unit_status_log.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @service_unit_status_log = ServiceUnitStatusLog.find(params[:id])
    @service_unit_status_log.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

