class DbController < ApplicationController
  def clean
    DatabaseCleaner.strategy = :transaction
    DatabaseCleaner.clean_with(:truncation)

    respond_to do |format|
      format.json { head :no_content }
    end
  end
end

