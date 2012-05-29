package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import com.kogasoftware.odt.invehicledevice.backgroundtask.NextDateChecker;
import com.kogasoftware.odt.invehicledevice.logic.CommonLogic;
import com.kogasoftware.odt.invehicledevice.logic.event.NewOperationStartEvent;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.test.util.Subscriber;
import com.kogasoftware.odt.invehicledevice.test.util.TestUtil;

public class NewDateCheckerTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	CommonLogic cl;
	NextDateChecker ndc;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		if (cl != null) {
			cl.dispose();
		}
		super.tearDown();
	}

	public void testしきい時間を過ぎるとNewOperationStartEvent送信() throws Exception {
		TestUtil.setDate(new DateTime(2012, 1, 23, CommonLogic.NEW_SCHEDULE_DOWNLOAD_HOUR - 1, 50, 0));

		cl = newCommonLogic();
		ndc = new NextDateChecker(cl);
		
		Subscriber<NewOperationStartEvent> s = Subscriber.of(
				NewOperationStartEvent.class, cl);
		cl.registerEventListener(s);

		ndc.run();
		assertFalse(s.s.tryAcquire(5, TimeUnit.SECONDS));
		TestUtil.setDate(new DateTime(2012, 1, 23, CommonLogic.NEW_SCHEDULE_DOWNLOAD_HOUR, 0, 0));
		ndc.run();
		assertTrue(s.s.tryAcquire(5, TimeUnit.SECONDS));

		TestUtil.setDate(new DateTime(2012, 1, 23, CommonLogic.NEW_SCHEDULE_DOWNLOAD_HOUR + 1, 0, 0));
		ndc.run();
		assertFalse(s.s.tryAcquire(5, TimeUnit.SECONDS));
		
		TestUtil.setDate(new DateTime(2012, 1, 24, CommonLogic.NEW_SCHEDULE_DOWNLOAD_HOUR - 2, 0, 0));
		ndc.run();
		assertFalse(s.s.tryAcquire(5, TimeUnit.SECONDS));
		
		TestUtil.setDate(new DateTime(2012, 1, 30, 0, 0, 0));
		ndc.run();
		assertTrue(s.s.tryAcquire(5, TimeUnit.SECONDS));
	}
}
