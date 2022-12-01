package com.kogasoftware.odt.invehicledevice.view.fragment.utils;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class ScheduleUtil implements Serializable {
    public static List<List<OperationSchedule>> getOperationSchedulesSortedPerPlatform(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {
        List<List<OperationSchedule>> phaseOperationSchedulesList = Lists.newLinkedList();

        for (List<OperationSchedule> samePlatformOperationSchedules : getOperationScheduleListSamePlatformChunk(operationSchedules, passengerRecords)) {
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

    private static LinkedList<List<OperationSchedule>> getOperationScheduleListSamePlatformChunk(List<OperationSchedule> operationSchedules, List<PassengerRecord> passengerRecords) {

        boolean first = true;
        OperationSchedule previousOS = null;

        LinkedList<List<OperationSchedule>> platformOrderOperationScheduleLists = Lists.newLinkedList();

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
}
