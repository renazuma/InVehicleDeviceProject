package com.kogasoftware.odt.invehicledevice.view.fragment.utils;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class OperationPhase implements Serializable {
  public List<OperationSchedule> operationSchedules;
  public List<PassengerRecord> passengerRecords;

  public OperationPhase(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
    this.operationSchedules = operationSchedules;
    this.passengerRecords = passengerRecords;
  }


  public static List<List> getAllPhaseOperationSchedules(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
        List<List> operationScheduleListChunk = Lists.newLinkedList();

        for (List<OperationSchedule> samePlatformOperationSchedules : getOperationScheduleListSamePlatformChunk(operationSchedules)) {
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
                operationScheduleListChunk.add(arrivalOperationSchedules);
            }
            if (departureOperationSchedules.size() > 0) {
                operationScheduleListChunk.add(departureOperationSchedules);
            }
        }
        return operationScheduleListChunk;
    }

  private static LinkedList<List> getOperationScheduleListSamePlatformChunk(List<OperationSchedule> operationSchedules) {

        boolean first = true;
        OperationSchedule previousOS = null;

        LinkedList<List> platformOrderOperationScheduleLists = Lists.newLinkedList();

        for (OperationSchedule currentOS : operationSchedules) {
            if (first || !previousOS.platformId.equals(currentOS.platformId)) {
                List<OperationSchedule> samePlatformOperationSchedules = Lists.newArrayList();
                samePlatformOperationSchedules.add(currentOS);
                platformOrderOperationScheduleLists.add(samePlatformOperationSchedules);
                first = false;}
            else {
                platformOrderOperationScheduleLists.getLast().add(currentOS);
            }
            previousOS = currentOS;
        }
        return platformOrderOperationScheduleLists;
    }

  public List<OperationSchedule> getCurrentOperationSchedules() {
    return getCurrentOperationSchedules(operationSchedules, passengerRecords);
  }

  public static List<OperationSchedule> getCurrentOperationSchedules(List<OperationSchedule> operationSchedules , List<PassengerRecord> passengerRecords) {
        List<List> chunkList = getAllPhaseOperationSchedules(operationSchedules, passengerRecords);
        List<OperationSchedule> currentChunk = Lists.newArrayList();

        for (List<OperationSchedule> chunkOperationSchedules : chunkList) {
            for (OperationSchedule operationSchedule : chunkOperationSchedules) {
                if (operationSchedule.arrivedAt == null || operationSchedule.departedAt == null) {
                    currentChunk = chunkOperationSchedules;
                    break;
                }
            }
            if (!currentChunk.isEmpty()) {
                break;
            }
        }

        return currentChunk;
    }

  public OperationSchedule getCurrentRepresentativeOS() {
      return getCurrentRepresentativeOS(operationSchedules, passengerRecords);
  }

  public static OperationSchedule getCurrentRepresentativeOS(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
        if (isExistCurrent(operationSchedules, passengerRecords)) {
            return getCurrentOperationSchedules(operationSchedules, passengerRecords).get(0);
        } else {
            return null;
        }
    }

  public boolean isExistCurrent() {
    return isExistCurrent(operationSchedules, passengerRecords);
  }

  public static boolean isExistCurrent(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
        return !getCurrentOperationSchedules(operationSchedules, passengerRecords).isEmpty();
    }

  public static List<OperationSchedule> getNextOperationSchedules(List<OperationSchedule> operationSchedules , List<PassengerRecord> passengerRecords) {
        List<List> chunkList = getAllPhaseOperationSchedules(operationSchedules, passengerRecords);
        List<OperationSchedule> nextChunk = Lists.newArrayList();

        for (int i = 0; i < chunkList.size() - 1; i++) {
            List<OperationSchedule> chunkOperationSchedules = chunkList.get(i);
            for (OperationSchedule operationSchedule : chunkOperationSchedules) {
                if (operationSchedule.arrivedAt == null || operationSchedule.departedAt == null) {
                    nextChunk = chunkList.get(i + 1);
                    break;
                }
            }
            if (!nextChunk.isEmpty()) {
                break;
            }
        }

        return nextChunk;
    }

  public OperationSchedule getNextRepresentativeOS() {
    return getNextRepresentativeOS(operationSchedules, passengerRecords);
  }

  public static OperationSchedule getNextRepresentativeOS(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
        if (isExistNext(operationSchedules, passengerRecords)) {
            return getNextOperationSchedules(operationSchedules, passengerRecords).get(0);
        } else {
            return null;
        }
    }

  public boolean isExistNext() {
    return isExistNext(operationSchedules, passengerRecords);
  }

  public static boolean isExistNext(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
        return !getNextOperationSchedules(operationSchedules, passengerRecords).isEmpty();
    }
}
