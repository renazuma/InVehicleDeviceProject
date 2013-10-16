class OperationSchedulesController < ApplicationController
  def index
    @operation_schedules = OperationSchedule.all
    respond_to do | format |
      format.json do
        render json: @operation_schedules.to_json
      end
    end
  end

  def show
    @operation_schedule = OperationSchedule.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @operation_schedule.to_json
      end
    end
  end

  def create
    params[:operation_schedule].delete(:departure_estimate)
    params[:operation_schedule].delete(:arrival_estimate)
    @operation_schedule = OperationSchedule.new(params[:operation_schedule])

    respond_to do |format|
      if @operation_schedule.save
        format.json { render json: @operation_schedule, status: :created}
      else
        format.json { render json: @operation_schedule.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @operation_schedule = OperationSchedule.find(params[:id])
    @operation_schedule.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

