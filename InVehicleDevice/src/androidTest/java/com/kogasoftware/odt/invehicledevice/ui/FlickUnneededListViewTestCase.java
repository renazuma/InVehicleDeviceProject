package com.kogasoftware.odt.invehicledevice.ui;

import java.util.concurrent.Callable;

import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.common.base.Throwables;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;

public class FlickUnneededListViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	FlickUnneededListView v;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			runTestOnUiThread(new Runnable() {
				@Override
				public void run() {
					v = new FlickUnneededListView(getInstrumentation()
							.getTargetContext(), null);
					getActivity().setContentView(v);
				}
			});
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
		assertNotNull(v);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test表示() throws Throwable {
		runTestOnUiThread(new Runnable() {
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

		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				v.getListView().setAdapter(
						new ArrayAdapter<String>(getInstrumentation()
								.getTargetContext(),
								android.R.layout.simple_list_item_1,
								new String[] { "bar", "baz" }));
			}
		});

		assertFalse(solo.searchText("foo", true));
		assertTrue(solo.searchText("bar", true));
		assertTrue(solo.searchText("baz", true));

		Button up = solo.getButton(solo.getString(R.string.scroll_up));
		Button down = solo.getButton(solo.getString(R.string.scroll_down));

		assertFalse(up.isEnabled());
		assertFalse(down.isEnabled());
	}

	public void testスクロール() throws Throwable {
		final String[] strings = new String[] { "bar", "baz", "baz", "baz",
				"baz", "baz", "baz", "baz", "baz", "baz", "baz", "baz", "baz",
				"baz", "baz", "baz", "foo" };
		runTestOnUiThread(new Runnable() {
			@Override
			public void run() {
				v.getListView().setAdapter(
						new ArrayAdapter<String>(getInstrumentation()
								.getTargetContext(),
								android.R.layout.simple_list_item_1, strings));
			}
		});

		Button up = solo.getButton(solo.getString(R.string.scroll_up));
		Button down = solo.getButton(solo.getString(R.string.scroll_down));

		assertEnabled(up, false);
		assertEnabled(down, true);

		solo.clickOnView(down);
		assertEnabled(up, true);
		assertEnabled(down, true);

		solo.clickOnView(up);
		solo.clickOnView(up);

		assertTrue(solo.searchText("bar", true));
		assertEnabled(up, false);
		assertEnabled(down, true);

		assertTrue(solo.searchText("bar", true));

		solo.clickOnView(down);
		solo.clickOnView(down);

		assertFalse(solo.searchText("bar", true));

		for (Integer i = 0; i < strings.length; ++i) {
			solo.clickOnView(up);
		}

		assertTrue(solo.searchText("bar", true));
		assertTrue(solo.searchText("foo", true));

		assertEnabled(down, false);
		solo.clickOnView(up);
		assertEnabled(down, true);
	}

	private void assertEnabled(final Button button, final boolean enabled) {
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return button.isEnabled() == enabled;
			}
		});
	}
}
