class DemandsController < ApplicationController
  def index
    @demands = Demand.all
    respond_to do | format |
      format.json do
        render json: @demands.to_json
      end
    end
  end

  def show
    @demand = Demand.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @demand.to_json
      end
    end
  end

  def create
    @demand = Demand.new(params[:demand])

    respond_to do |format|
      if @demand.save
        format.json { render json: @demand, status: :created}
      else
        format.json { render json: @demand.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @demand = Demand.find(params[:id])
    @demand.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

