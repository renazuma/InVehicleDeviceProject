class ReservationUsersController < ApplicationController
  def index
    @reservation_users = ReservationUser.all
    respond_to do | format |
      format.json do
        render json: @reservation_users.to_json
      end
    end
  end

  def show
    @reservation_user = ReservationUser.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @reservation_user.to_json
      end
    end
  end

  def create
    @reservation_user = ReservationUser.new(params[:reservation_user])

    respond_to do |format|
      if @reservation_user.save
        format.json { render json: @reservation_user, status: :created}
      else
        format.json { render json: @reservation_user.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @reservation_user = ReservationUser.find(params[:id])
    @reservation_user.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

