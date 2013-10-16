class PassengerRecordsController < ApplicationController
  def index
    @passenger_records = PassengerRecord.all
    respond_to do | format |
      format.json do
        render json: @passenger_records.to_json
      end
    end
  end

  def show
    @passenger_record = PassengerRecord.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @passenger_record.to_json
      end
    end
  end

  def create
    @passenger_record = PassengerRecord.new(params[:passenger_record])

    respond_to do |format|
      if @passenger_record.save
        format.json { render json: @passenger_record, status: :created}
      else
        format.json { render json: @passenger_record.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @passenger_record = PassengerRecord.find(params[:id])
    @passenger_record.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

