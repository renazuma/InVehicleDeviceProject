class DriversController < ApplicationController
  def index
    @drivers = Driver.all
    respond_to do | format |
      format.json do
        render json: @drivers.to_json
      end
    end
  end

  def show
    @driver = Driver.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @driver.to_json
      end
    end
  end

  def create
    @driver = Driver.new(params[:driver])

    respond_to do |format|
      if @driver.save
        format.json { render json: @driver, status: :created}
      else
        format.json { render json: @driver.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @driver = Driver.find(params[:id])
    @driver.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

