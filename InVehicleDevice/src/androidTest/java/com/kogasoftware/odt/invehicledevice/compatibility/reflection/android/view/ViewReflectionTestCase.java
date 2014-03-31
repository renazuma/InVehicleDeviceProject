package com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.view;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import junitx.framework.ComparableAssert;

import org.apache.commons.lang3.reflect.MethodUtils;

import android.os.Build;
import android.view.View;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.view.ViewReflection;
import com.kogasoftware.odt.invehicledevice.compatibility.reflection.android.view.ViewReflection.OnSystemUiVisibilityChangeListenerReflection;
import com.kogasoftware.odt.invehicledevice.testutil.EmptyActivityInstrumentationTestCase2;
import com.kogasoftware.odt.invehicledevice.testutil.TestUtil;

public class ViewReflectionTestCase extends
		EmptyActivityInstrumentationTestCase2 {
	public void testSystemUiFlagLowProfile() {
		if (Build.VERSION.SDK_INT < 14) {
			assertFalse(ViewReflection.SYSTEM_UI_FLAG_LOW_PROFILE.isPresent());
			return;
		}
		assertEquals(Optional.of(1), ViewReflection.SYSTEM_UI_FLAG_LOW_PROFILE);
	}

	public void testSystemUiFlagVisible() {
		if (Build.VERSION.SDK_INT < 14) {
			assertFalse(ViewReflection.SYSTEM_UI_FLAG_VISIBLE.isPresent());
			return;
		}
		assertEquals(Optional.of(0), ViewReflection.SYSTEM_UI_FLAG_VISIBLE);
	}

	public void testSetSystemUiVisibility() {
		View v = new View(getInstrumentation().getTargetContext());
		if (Build.VERSION.SDK_INT < 14) {
			assertFalse(ViewReflection.setSystemUiVisibility(v, 0).isPresent());
			return;
		}
		assertTrue(ViewReflection.setSystemUiVisibility(v,
				ViewReflection.SYSTEM_UI_FLAG_LOW_PROFILE.get()).isPresent());
		assertEquals(ViewReflection.SYSTEM_UI_FLAG_LOW_PROFILE,
				ViewReflection.getSystemUiVisibility(v));
		assertTrue(ViewReflection.setSystemUiVisibility(v,
				ViewReflection.SYSTEM_UI_FLAG_VISIBLE.get()).isPresent());
		assertEquals(ViewReflection.SYSTEM_UI_FLAG_VISIBLE,
				ViewReflection.getSystemUiVisibility(v));
	}

	public void testGetSystemUiVisibility() {
		View v = new View(getInstrumentation().getTargetContext());
		if (Build.VERSION.SDK_INT < 14) {
			assertFalse(ViewReflection.getSystemUiVisibility(v).isPresent());
			return;
		}
		assertTrue(ViewReflection.getSystemUiVisibility(v).isPresent());
	}

	public void testNewOnSystemUiVisibilityChangeListener() throws Exception {
		if (Build.VERSION.SDK_INT < 14) {
			assertFalse(ViewReflection.newOnSystemUiVisibilityChangeListener(
					new OnSystemUiVisibilityChangeListenerReflection() {
						@Override
						public void onSystemUiVisibilityChange(int visibility) {
						}
					}).isPresent());
			return;
		}

		final List<Integer> args = Lists.newLinkedList();
		OnSystemUiVisibilityChangeListenerReflection rl = new OnSystemUiVisibilityChangeListenerReflection() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				args.add(visibility);
			}

			@Override
			public String toString() {
				return "foo";
			}
		};
		Object l = ViewReflection.newOnSystemUiVisibilityChangeListener(rl)
				.get();

		// OnSystemUiVisibilityChangeListenerとして機能するか
		assertTrue(Class.forName(
				"android.view.View$OnSystemUiVisibilityChangeListener")
				.isInstance(l));
		MethodUtils.invokeExactMethod(l, "onSystemUiVisibilityChange",
				new Object[] { 0 }, new Class[] { int.class });
		assertEquals(0, args.get(0).intValue());
		assertEquals(1, args.size());

		MethodUtils.invokeExactMethod(l, "onSystemUiVisibilityChange",
				new Object[] { 12345 }, new Class[] { int.class });
		assertEquals(12345, args.get(1).intValue());
		assertEquals(2, args.size());

		// 存在しないメソッドでエラーが発生するか
		try {
			MethodUtils.invokeExactMethod(l, "onSystemUiVisibilityChange2",
					new Object[] { 2 }, new Class[] { int.class });
			fail();
		} catch (NoSuchMethodException e) {
		}
		assertEquals(2, args.size());

		// Objectのメソッドが実行できるか
		assertTrue(l.equals(l));
		assertFalse(l.equals(rl));
		assertFalse(l.equals(0));
		assertTrue(l.getClass().isInstance(l));
		assertFalse(l.getClass().isInstance(rl));
		assertFalse(l.getClass().isInstance(0));
		assertEquals(l.hashCode(), l.hashCode());
		assertEquals(l.toString(), rl.toString());
	}

	public void testSetOnSystemUiVisibilityChangeListener() throws Exception {
		final View v = new View(getInstrumentation().getTargetContext());
		final AtomicInteger count1 = new AtomicInteger(0);
		final AtomicInteger count2 = new AtomicInteger(0);
		final AtomicInteger expectedVisibility = new AtomicInteger(12345);
		final AtomicInteger outputVisibility = new AtomicInteger(12345);
		final OnSystemUiVisibilityChangeListenerReflection listener1 = new OnSystemUiVisibilityChangeListenerReflection() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				count1.incrementAndGet();
				assertEquals(expectedVisibility.get(), visibility);
				outputVisibility.set(visibility);
			}
		};
		final OnSystemUiVisibilityChangeListenerReflection listener2 = new OnSystemUiVisibilityChangeListenerReflection() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				count2.incrementAndGet();
				assertEquals(expectedVisibility.get(), visibility);
				outputVisibility.set(visibility);
			}
		};
		if (Build.VERSION.SDK_INT < 14) {
			assertFalse(ViewReflection.setOnSystemUiVisibilityChangeListener(v,
					listener1).isPresent());
			return;
		}

		final int VISIBLE = ViewReflection.SYSTEM_UI_FLAG_VISIBLE.get();
		final int LOW_PROFILE = ViewReflection.SYSTEM_UI_FLAG_LOW_PROFILE.get();
		int prevCount1 = 0;
		int prevCount2 = 0;

		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				a.setContentView(v);
				assertTrue(ViewReflection
						.setOnSystemUiVisibilityChangeListener(v, listener1)
						.isPresent());
			}
		});

		// LOW_PROFILEに変更
		expectedVisibility.set(LOW_PROFILE);
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ViewReflection.setSystemUiVisibility(v, LOW_PROFILE);
			}
		});
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return outputVisibility.get() == LOW_PROFILE;
			}
		});
		ComparableAssert.assertGreater(prevCount1, count1.get());
		prevCount1 = count1.get();

		// VISIBLEに変更
		expectedVisibility.set(VISIBLE);
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ViewReflection.setSystemUiVisibility(v, VISIBLE);
			}
		});
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return outputVisibility.get() == VISIBLE;
			}
		});
		ComparableAssert.assertGreater(prevCount1, count1.get());
		prevCount1 = count1.get();

		// LOW_PROFILEに変更
		expectedVisibility.set(LOW_PROFILE);
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ViewReflection.setSystemUiVisibility(v, LOW_PROFILE);
			}
		});
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return outputVisibility.get() == LOW_PROFILE;
			}
		});
		ComparableAssert.assertGreater(prevCount1, count1.get());
		prevCount1 = count1.get();

		// listenerを変更
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ViewReflection.setOnSystemUiVisibilityChangeListener(v,
						listener2);
			}
		});

		// VISIBLEに変更
		expectedVisibility.set(VISIBLE);
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ViewReflection.setSystemUiVisibility(v, VISIBLE);
			}
		});
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return outputVisibility.get() == VISIBLE;
			}
		});
		ComparableAssert.assertGreater(prevCount2, count2.get());
		prevCount2 = count2.get();

		// LOW_PROFILEに変更
		expectedVisibility.set(LOW_PROFILE);
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ViewReflection.setSystemUiVisibility(v, LOW_PROFILE);
			}
		});
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return outputVisibility.get() == LOW_PROFILE;
			}
		});
		ComparableAssert.assertGreater(prevCount2, count2.get());
		prevCount2 = count2.get();

		// VISIBLEに変更
		expectedVisibility.set(VISIBLE);
		runTestOnUiThreadSync(new Runnable() {
			@Override
			public void run() {
				ViewReflection.setSystemUiVisibility(v, VISIBLE);
			}
		});
		TestUtil.assertChange(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return outputVisibility.get() == VISIBLE;
			}
		});
		ComparableAssert.assertGreater(prevCount2, count2.get());
		prevCount2 = count2.get();

		assertEquals(prevCount1, count1.get());
	}
}
