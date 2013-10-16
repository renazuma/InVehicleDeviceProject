class UsersController < ApplicationController
  def index
    @users = User.all
    respond_to do | format |
      format.json do
        render json: @users.to_json
      end
    end
  end

  def show
    @user = User.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @user.to_json
      end
    end
  end

  def create
    @user = User.new(params[:user])

    respond_to do |format|
      if @user.save
        format.json { render json: @user, status: :created}
      else
        format.json { render json: @user.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @user = User.find(params[:id])
    @user.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

