class Ability
  include CanCan::Ability

  def initialize(operator)
    if operator
      operator.roles.each do |role|
        case role.name
        when "operator"
          operator_can(operator)
          can :manage, Operator, :id => operator.id
        when "super_operator"
          operator_can(operator)
          super_operator_can(operator)
          can :manage, ServiceProvider, :id => operator.service_provider.id
        when "system_admin"
          system_admin_can(operator)
        when "super_system_admin"
          system_admin_can(operator)
          super_system_admin_can(operator)
        end
      end
    end
    # Define abilities for the passed in user here. For example:
    #
    #   user ||= User.new # guest user (not logged in)
    #   if user.admin?
    #     can :manage, :all
    #   else
    #     can :read, :all
    #   end
    #
    # The first argument to `can` is the action you are giving the user permission to do.
    # If you pass :manage it will apply to every action. Other common actions here are
    # :read, :create, :update and :destroy.
    #
    # The second argument is the resource the user can perform the action on. If you pass
    # :all it will apply to every resource. Otherwise pass a Ruby class of the resource.
    #
    # The third argument is an optional hash of conditions to further filter the objects.
    # For example, here the user can only update published articles.
    #
    #   can :update, Article, :published => true
    #
    # See the wiki for details: https://github.com/ryanb/cancan/wiki/Defining-Abilities
  end

  private
  def operator_can(operator)
    can :manage, [
      Demand,
      OperationSchedule,
      PassengerRecord,
      Platform,
      Reservation,
      UnitAssignment,
      User,
      UserGroup,
      VehicleNotificationTemplate,
    ], :service_provider_id => operator.service_provider.id
    can :manage, [
      OperationRecord,
      ServiceUnit,
      VehicleNotification,
    ]
  end

  def super_operator_can(operator)
    can :manage, [
      DemandArea,
      Driver,
      InVehicleDevice,
      InitialVector,
      Operator,
      Vehicle,
      SemiDemandPolicy,
    ], :service_provider_id => operator.service_provider.id
    can :manage, [
      Audit,
      OperationArea,
      SemiDemandCourseOrder,
    ]
  end

  def system_admin_can(operator)
    can :manage, [
      Operator,
      ServiceProvider,
    ]
  end

  def super_system_admin_can(operator)
    can :manage, [
      SystemAdmin,
    ]
  end
end
