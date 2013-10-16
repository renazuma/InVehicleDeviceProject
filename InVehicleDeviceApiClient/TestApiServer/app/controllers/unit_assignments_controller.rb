class UnitAssignmentsController < ApplicationController
  def index
    @unit_assignments = UnitAssignment.all
    respond_to do | format |
      format.json do
        render json: @unit_assignments.to_json
      end
    end
  end

  def show
    @unit_assignment = UnitAssignment.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @unit_assignment.to_json
      end
    end
  end

  def create
    @unit_assignment = UnitAssignment.new(params[:unit_assignment])

    respond_to do |format|
      if @unit_assignment.save
        format.json { render json: @unit_assignment, status: :created}
      else
        format.json { render json: @unit_assignment.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @unit_assignment = UnitAssignment.find(params[:id])
    @unit_assignment.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

