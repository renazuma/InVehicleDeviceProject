package com.kogasoftware.odt.invehicledevice.view.fragment.utils

import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord
import com.google.common.collect.Lists
import java.io.Serializable
import java.util.*

object ScheduleUtil : Serializable {
    @JvmStatic
    fun getOperationSchedulesSortedPerPlatform(operationSchedules: List<OperationSchedule>, passengerRecords: List<PassengerRecord>): List<List<OperationSchedule>> {
        val phaseOperationSchedulesList: MutableList<List<OperationSchedule>> = Lists.newLinkedList()
        for (samePlatformOperationSchedules in getOperationScheduleListSamePlatformChunk(operationSchedules, passengerRecords)) {
            val arrivalOperationSchedules: MutableList<OperationSchedule> = Lists.newArrayList()
            val departureOperationSchedules: MutableList<OperationSchedule> = Lists.newArrayList()
            for (operationSchedule in samePlatformOperationSchedules) {
                for (passengerRecord in passengerRecords) {
                    if (passengerRecord.departureScheduleId == operationSchedule.id && !departureOperationSchedules.contains(operationSchedule)) {
                        departureOperationSchedules.add(operationSchedule)
                    } else if (passengerRecord.arrivalScheduleId == operationSchedule.id && !arrivalOperationSchedules.contains(operationSchedule)) {
                        arrivalOperationSchedules.add(operationSchedule)
                    }
                }
            }
            if (arrivalOperationSchedules.size > 0) {
                phaseOperationSchedulesList.add(arrivalOperationSchedules)
            }
            if (departureOperationSchedules.size > 0) {
                phaseOperationSchedulesList.add(departureOperationSchedules)
            }
        }
        return phaseOperationSchedulesList
    }

    private fun getOperationScheduleListSamePlatformChunk(operationSchedules: List<OperationSchedule>, passengerRecords: List<PassengerRecord>): LinkedList<MutableList<OperationSchedule>> {
        var first = true
        var previousOS: OperationSchedule? = null
        val platformOrderOperationScheduleLists = Lists.newLinkedList<MutableList<OperationSchedule>>()
        for (currentOS in operationSchedules) {
            if (first || previousOS!!.platformId != currentOS.platformId) {
                val samePlatformOperationSchedules: MutableList<OperationSchedule> = Lists.newArrayList()
                samePlatformOperationSchedules.add(currentOS)
                platformOrderOperationScheduleLists.add(samePlatformOperationSchedules)
                first = false
            } else {
                platformOrderOperationScheduleLists.last.add(currentOS)
            }
            previousOS = currentOS
        }
        return platformOrderOperationScheduleLists
    }
}
