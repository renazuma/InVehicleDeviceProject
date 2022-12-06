package com.kogasoftware.odt.invehicledevice.view.fragment.utils;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OperationPhase implements Serializable {
    public final List<OperationSchedule> operationSchedules;
    public final List<PassengerRecord> passengerRecords;

    public OperationPhase(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
        this.operationSchedules = operationSchedules;
        this.passengerRecords = passengerRecords;
    }

    public OperationSchedule.Phase getPhase() {
        OperationPhase operationPhase = new OperationPhase(operationSchedules, passengerRecords);
        List<OperationSchedule> currentPhaseOperationSchedules = operationPhase.getCurrentOperationSchedules();

        if (currentPhaseOperationSchedules.isEmpty()) {
            return OperationSchedule.Phase.FINISH;
        } else {
            if (isDrive(currentPhaseOperationSchedules)) {
                return OperationSchedule.Phase.DRIVE;
            } else if (isGetOn(currentPhaseOperationSchedules, passengerRecords)) {
                return OperationSchedule.Phase.PLATFORM_GET_ON;
            } else {
                return OperationSchedule.Phase.PLATFORM_GET_OFF;
            }
        }
    }

    private Boolean isDrive(List<OperationSchedule> operationSchedules) {
        for(OperationSchedule operationSchedule : operationSchedules) {
            if (operationSchedule.arrivedAt == null) {
                return true;
            }
        }
        return false;
    }

    private Boolean isGetOn(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
        if (isDrive(operationSchedules)) {
            return false;
        }
        for(OperationSchedule operationSchedule : operationSchedules) {
            if (operationSchedule.completeGetOff || operationSchedule.getGetOffScheduledPassengerRecords(passengerRecords).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<List> getOperationSchedulesSortedPerPlatform() {
        List<List> phaseOperationSchedulesList = Lists.newLinkedList();

        for (List<OperationSchedule> samePlatformOperationSchedules : getOperationScheduleListSamePlatformChunk()) {
            List<OperationSchedule> arrivalOperationSchedules = Lists.newArrayList();
            List<OperationSchedule> departureOperationSchedules = Lists.newArrayList();

            for (OperationSchedule operationSchedule : samePlatformOperationSchedules) {
                for (PassengerRecord passengerRecord : passengerRecords) {
                    if (passengerRecord.departureScheduleId.equals(operationSchedule.id) && !departureOperationSchedules.contains((operationSchedule))) {
                        departureOperationSchedules.add(operationSchedule);
                    } else if (passengerRecord.arrivalScheduleId.equals(operationSchedule.id) && !arrivalOperationSchedules.contains((operationSchedule))) {
                        arrivalOperationSchedules.add(operationSchedule);
                    }
                }
            }

            if (arrivalOperationSchedules.size() > 0) {
                phaseOperationSchedulesList.add(arrivalOperationSchedules);
            }
            if (departureOperationSchedules.size() > 0) {
                phaseOperationSchedulesList.add(departureOperationSchedules);
            }
        }
        return phaseOperationSchedulesList;
    }

    private LinkedList<List> getOperationScheduleListSamePlatformChunk() {

        boolean first = true;
        OperationSchedule previousOS = null;

        LinkedList<List> platformOrderOperationScheduleLists = Lists.newLinkedList();

        for (OperationSchedule currentOS : operationSchedules) {
            if (first || !previousOS.platformId.equals(currentOS.platformId)) {
                List<OperationSchedule> samePlatformOperationSchedules = Lists.newArrayList();
                samePlatformOperationSchedules.add(currentOS);
                platformOrderOperationScheduleLists.add(samePlatformOperationSchedules);
                first = false;
            } else {
                platformOrderOperationScheduleLists.getLast().add(currentOS);
            }
            previousOS = currentOS;
        }
        return platformOrderOperationScheduleLists;
    }

    public List<OperationSchedule> getCurrentOperationSchedules() {
        List<List> phaseOperationSchedulesList = getOperationSchedulesSortedPerPlatform();
        List<OperationSchedule> currentPhaseOperationSchedules = Lists.newArrayList();

        for (List<OperationSchedule> phaseOperationSchedules : phaseOperationSchedulesList) {
            for (OperationSchedule operationSchedule : phaseOperationSchedules) {
                if (operationSchedule.arrivedAt == null || operationSchedule.departedAt == null) {
                    currentPhaseOperationSchedules = phaseOperationSchedules;
                    break;
                }
            }
            if (!currentPhaseOperationSchedules.isEmpty()) {
                break;
            }
        }

        return currentPhaseOperationSchedules;
    }

    public OperationSchedule getCurrentRepresentativeOS() {
        if (isExistCurrent()) {
            return getCurrentOperationSchedules().get(0);
        } else {
            return null;
        }
    }

    public boolean isExistCurrent() {
        return !getCurrentOperationSchedules().isEmpty();
    }

    public List<OperationSchedule> getNextOperationSchedules() {
        List<List> phaseOperationSchedulesList = getOperationSchedulesSortedPerPlatform();
        List<OperationSchedule> nextPhaseOperationSchedules = Lists.newArrayList();

        for (int i = 0; i < phaseOperationSchedulesList.size() - 1; i++) {
            List<OperationSchedule> phaseOperationSchedules = phaseOperationSchedulesList.get(i);
            for (OperationSchedule operationSchedule : phaseOperationSchedules) {
                if (operationSchedule.arrivedAt == null || operationSchedule.departedAt == null) {
                    nextPhaseOperationSchedules = phaseOperationSchedulesList.get(i + 1);
                    break;
                }
            }
            if (!nextPhaseOperationSchedules.isEmpty()) {
                break;
            }
        }

        return nextPhaseOperationSchedules;
    }

    public OperationSchedule getNextRepresentativeOS() {
        if (isExistNext()) {
            return getNextOperationSchedules().get(0);
        } else {
            return null;
        }
    }

    public boolean isExistNext() {
        return !getNextOperationSchedules().isEmpty();
    }
}
