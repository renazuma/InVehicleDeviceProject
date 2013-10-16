class ServiceProvidersController < ApplicationController
  def index
    @service_providers = ServiceProvider.all
    respond_to do | format |
      format.json do
        render json: @service_providers.to_json
      end
    end
  end

  def show
    @service_provider = ServiceProvider.find(params[:id])

    respond_to do |format|
      format.json do
        render json: @service_provider.to_json
      end
    end
  end

  def create
    @service_provider = ServiceProvider.new(params[:service_provider])

    respond_to do |format|
      if @service_provider.save
        format.json { render json: @service_provider, status: :created}
      else
        format.json { render json: @service_provider.errors, status: :unprocessable_entity }
      end
    end
  end

  def destroy
    @service_provider = ServiceProvider.find(params[:id])
    @service_provider.destroy

    respond_to do |format|
      format.json { head :no_content }
    end
  end

end

