class ServiceUnitsController < ApplicationController
  def index
    @service_units = ServiceUnit.all
    respond_to do | format |
      format.json do
        render json: @service_units.to_json
      end
    end
  end

  def show
    @service_unit = ServiceUnit.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @service_unit.to_json
      end
    end
  end

  def create
    @service_unit = ServiceUnit.new(params[:service_unit])

    respond_to do |format|
      if @service_unit.save
        format.json { render json: @service_unit, status: :created}
      else
        format.json { render json: @service_unit.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @service_unit = ServiceUnit.find(params[:id])
    @service_unit.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

