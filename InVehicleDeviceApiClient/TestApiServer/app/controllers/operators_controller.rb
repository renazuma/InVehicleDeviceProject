class OperatorsController < ApplicationController
  def index
    @operator = Operator.all
    respond_to do | format |
      format.json do
        render json: @operator.to_json
      end
    end
  end

  def show
    @operator = Operator.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @operator.to_json
      end
    end
  end

  def create
    @operator = Operator.new(params[:operator])

    respond_to do |format|
      if @operator.save
        format.json { render json: @operator, status: :created}
      else
        format.json { render json: @operator.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @operator = Operator.find(params[:id])
    @operator.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

