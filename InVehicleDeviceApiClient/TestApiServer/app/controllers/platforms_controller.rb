class PlatformsController < ApplicationController
  def index
    @platforms = Platform.all
    respond_to do | format |
      format.json do
        render json: @platforms.to_json
      end
    end
  end

  def show
    @platform = Platform.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @platform.to_json
      end
    end
  end

  def create
    @platform = Platform.new(params[:platform])

    respond_to do |format|
      if @platform.save
        format.json { render json: @platform, status: :created}
      else
        format.json { render json: @platform.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @platform = Platform.find(params[:id])
    @platform.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

