package com.kogasoftware.odt.invehicledevice.ui;

import android.widget.ArrayAdapter;
import android.widget.Button;

import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;
import com.kogasoftware.odt.invehicledevice.ui.FlickUnneededListView;

public class FlickUnneededListViewTestCase extends
		EmptyActivityInstrumentationTestCase2 {

	FlickUnneededListView v;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		TestUtil.runOnUiThreadSync(getActivity(), new Runnable() {
			@Override
			public void run() {
				v = new FlickUnneededListView(getInstrumentation()
						.getTargetContext(), null);
				getActivity().setContentView(v);
			}
		});
		assertNotNull(v);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test表示() throws InterruptedException {
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

		runOnUiThreadSync(new Runnable() {
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

		Button up = solo.getButton("△ 上へ移動");
		Button down = solo.getButton("▽ 下へ移動");

		assertFalse(up.isEnabled());
		assertFalse(down.isEnabled());
	}

	public void testスクロール() throws Exception {
		final String[] strings = new String[] { "bar", "baz", "baz", "baz",
				"baz", "baz", "baz", "baz", "baz", "baz", "baz", "baz", "baz",
				"baz", "baz", "baz", "foo" };
		runOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				v.getListView().setAdapter(
						new ArrayAdapter<String>(getInstrumentation()
								.getTargetContext(),
								android.R.layout.simple_list_item_1, strings));
			}
		});

		Button up = solo.getButton("△ 上へ移動");
		Button down = solo.getButton("▽ 下へ移動");

		assertFalse(up.isEnabled());
		assertTrue(down.isEnabled());

		solo.clickOnView(down);

		assertTrue(up.isEnabled());
		assertTrue(down.isEnabled());

		solo.clickOnView(up);
		solo.clickOnView(up);

		assertTrue(solo.searchText("bar", true));
		assertFalse(up.isEnabled());
		assertTrue(down.isEnabled());

		assertTrue(solo.searchText("bar", true));

		solo.clickOnView(down);
		solo.clickOnView(down);

		assertFalse(solo.searchText("bar", true));

		for (Integer i = 0; i < strings.length; ++i) {
			solo.clickOnView(up);
		}

		assertTrue(solo.searchText("bar", true));
		assertTrue(solo.searchText("foo", true));

		assertFalse(down.isEnabled());
		solo.clickOnView(up);
		assertTrue(down.isEnabled());
	}
}