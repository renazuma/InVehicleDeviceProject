package com.kogasoftware.odt.invehicledevice.test.unit.service.invehicledeviceservice;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import android.location.GpsStatus;
import android.location.Location;
import android.os.HandlerThread;
import android.test.ServiceTestCase;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.InVehicleDeviceService;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.LocalData.VehicleNotificationStatus;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.OperationScheduleLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.PassengerRecordLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.ServiceUnitStatusLogLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.VehicleNotificationLogic;
import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.VoiceServiceConnector;
import com.kogasoftware.odt.webapi.model.OperationSchedule;
import com.kogasoftware.odt.webapi.model.PassengerRecord;
import com.kogasoftware.odt.webapi.model.VehicleNotification;

public class InVehicleDeviceServiceTestCase extends
		ServiceTestCase<InVehicleDeviceService> {

	public InVehicleDeviceServiceTestCase() {
		super(InVehicleDeviceService.class);
	}

	public void testForwarding() throws Exception {
		Integer m = 200;

		final OperationScheduleLogic osl = mock(OperationScheduleLogic.class);
		final PassengerRecordLogic prl = mock(PassengerRecordLogic.class);
		final VehicleNotificationLogic vnl = mock(VehicleNotificationLogic.class);
		final ServiceUnitStatusLogLogic susll = mock(ServiceUnitStatusLogLogic.class);
		final VoiceServiceConnector vsc = mock(VoiceServiceConnector.class);

		final AtomicReference<InVehicleDeviceService> sar = new AtomicReference<InVehicleDeviceService>();
		InVehicleDeviceService s;
		final CountDownLatch cdl = new CountDownLatch(1);
		HandlerThread ht = new HandlerThread("ht") {
			@Override
			protected void onLooperPrepared() {
				sar.set(new InVehicleDeviceService(osl, prl, vnl, susll, vsc));
				cdl.countDown();
			}
		};
		try {
			ht.start();
			assertTrue(cdl.await(10, TimeUnit.SECONDS));
			s = sar.get();
			{
				reset(prl);
				PassengerRecord pr = new PassengerRecord();
				pr.setId(1);
				s.canGetOff(pr);
				Thread.sleep(m);
				verify(prl, only()).canGetOff(eq(pr));
			}

			{
				reset(prl);
				PassengerRecord pr = new PassengerRecord();
				pr.setId(2);
				s.canGetOn(pr);
				Thread.sleep(m);
				verify(prl, only()).canGetOn(eq(pr));
			}

			{
				reset(susll);
				Location l = new Location("foo");
				// Optional<GpsStatus> gs = Optional.of(mock(GpsStatus.class));
				Optional<GpsStatus> gs = Optional.absent();
				s.changeLocation(l, gs);
				Thread.sleep(m);
				verify(susll, only()).changeLocation(l, gs);
			}

			{
				reset(susll);
				Double d = 1.2345;
				s.changeOrientation(d);
				Thread.sleep(m);
				verify(susll, only()).changeOrientation(d);
			}

			{
				reset(susll);
				Double d = 1.2345;
				s.changeOrientation(d);
				Thread.sleep(m);
				verify(susll, only()).changeOrientation(d);
			}

			{
				reset(susll);
				Double d = 25.012345;
				s.changeTemperature(d);
				Thread.sleep(m);
				verify(susll, only()).changeTemperature(d);
			}

			{
				reset(osl);
				s.enterDrivePhase();
				Thread.sleep(m);
				verify(osl, only()).enterDrivePhase();
			}

			{
				reset(osl);
				s.enterFinishPhase();
				Thread.sleep(m);
				verify(osl, only()).enterFinishPhase();
			}

			{
				reset(osl);
				s.getCurrentOperationSchedule();
				Thread.sleep(m);
				verify(osl, only()).getCurrentOperationSchedule();
			}

			{
				reset(prl);
				s.getNoGettingOffPassengerRecords();
				Thread.sleep(m);
				verify(prl, only()).getNoGettingOffPassengerRecords();
			}

			{
				reset(prl);
				s.getNoGettingOnPassengerRecords();
				Thread.sleep(m);
				verify(prl, only()).getNoGettingOnPassengerRecords();
			}

			{
				reset(prl);
				s.getNoPaymentPassengerRecords();
				Thread.sleep(m);
				verify(prl, only()).getNoPaymentPassengerRecords();
			}

			{
				reset(prl);
				s.getPassengerRecords();
				Thread.sleep(m);
				verify(prl, only()).getPassengerRecords();
			}

			{
				reset(vnl);
				Integer i = 589023;
				VehicleNotificationStatus vns = VehicleNotificationStatus.REPLIED;
				s.getVehicleNotifications(i, vns);
				Thread.sleep(m);
				verify(vnl, only()).getVehicleNotifications(i, vns);
			}

			{
				reset(prl);
				PassengerRecord pr = new PassengerRecord();
				pr.setId(3);
				s.isGetOffScheduled(pr);
				Thread.sleep(m);
				verify(prl, only()).isGetOffScheduled(pr);
			}

			{
				reset(prl);
				PassengerRecord pr = new PassengerRecord();
				pr.setId(4);
				s.isGetOnScheduled(pr);
				Thread.sleep(m);
				verify(prl, only()).isGetOnScheduled(pr);
			}

			{
				reset(prl);
				PassengerRecord pr = new PassengerRecord();
				pr.setId(5);
				s.isSelected(pr);
				Thread.sleep(m);
				verify(prl, only()).isSelected(pr);
			}

			{
				reset(osl);
				List<OperationSchedule> oss = Lists.newLinkedList();
				List<VehicleNotification> vns = Lists.newLinkedList();
				s.mergeOperationSchedules(oss, vns);
				Thread.sleep(m);
				verify(osl, only()).mergeOperationSchedules(oss, vns);
			}

			{
				reset(vnl);
				List<VehicleNotification> vns = Lists.newLinkedList();
				s.receiveVehicleNotification(vns);
				Thread.sleep(m);
				verify(vnl, only()).receiveVehicleNotification(vns);
			}

			{
				reset(vnl);
				List<VehicleNotification> vns = Lists.newLinkedList();
				s.replyUpdatedOperationScheduleVehicleNotifications(vns);
				Thread.sleep(m);
				verify(vnl, only()).replyUpdatedOperationScheduleVehicleNotifications(vns);
			}

			{
				reset(vnl);
				VehicleNotification vn = new VehicleNotification();
				vn.setId(10);
				s.replyVehicleNotification(vn);
				Thread.sleep(m);
				verify(vnl, only()).replyVehicleNotification(vn);
			}

			{
				reset(prl);
				PassengerRecord pr = new PassengerRecord();
				pr.setId(11);
				s.unselect(pr);
				Thread.sleep(m);
				verify(prl, only()).unselect(pr);
			}

			{
				reset(osl);
				s.startNewOperation();
				Thread.sleep(m);
				verify(osl, only()).startNewOperation();
			}
		} finally {
			ht.quit();
			ht.interrupt();
		}
	}
}
