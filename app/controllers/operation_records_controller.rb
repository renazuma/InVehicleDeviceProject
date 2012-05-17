class OperationRecordsController < ApplicationController
  def index
    @operation_records = OperationRecord.all
    respond_to do | format |
      format.json do
        render json: @operation_records.to_json
      end
    end
  end

  def show
    @operation_record = OperationRecord.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @operation_record.to_json
      end
    end
  end

  def create
    @operation_record = OperationRecord.new(params[:operation_record])

    respond_to do |format|
      if @operation_record.save
        format.json { render json: @operation_record, status: :created}
      else
        format.json { render json: @operation_record.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @operation_record = OperationRecord.find(params[:id])
    @operation_record.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

