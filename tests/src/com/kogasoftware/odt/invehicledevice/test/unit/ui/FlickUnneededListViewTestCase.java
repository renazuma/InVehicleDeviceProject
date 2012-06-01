package com.kogasoftware.odt.invehicledevice.test.unit.ui;

import android.widget.ArrayAdapter;

import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;

public class FlickUnneededListViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	FlickUnneededListView v;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		v = (FlickUnneededListView) inflateAndAddTestLayout(com.kogasoftware.odt.invehicledevice.test.R.layout.test_flick_unneeded_list_view);
		assertNotNull(v);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test1() throws InterruptedException {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				v.getListView().setAdapter(
						new ArrayAdapter<String>(getInstrumentation()
								.getTargetContext(),
								android.R.layout.simple_list_item_1,
								new String[] { "foo", "bar" }));
			}
		});
		assertTrue(solo.searchText("foo", true));
		assertTrue(solo.searchText("bar", true));
		assertFalse(solo.searchText("baz", true));
	}

	public void xtest2() throws InterruptedException {
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				v.getListView().setAdapter(
						new ArrayAdapter<String>(getInstrumentation()
								.getTargetContext(),
								android.R.layout.simple_list_item_1,
								new String[] { "foo", "foo", "foo", "foo",
										"foo", "foo", "foo", "foo", "foo",
										"foo", "bar" }));
			}
		});
		Thread.sleep(1200 * 1000);
	}
}
