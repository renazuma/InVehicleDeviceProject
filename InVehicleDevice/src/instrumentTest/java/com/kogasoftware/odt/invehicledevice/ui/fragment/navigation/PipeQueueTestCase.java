package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation;

import java.util.Comparator;

import junit.framework.TestCase;

import com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline.PipeQueue;

public class PipeQueueTestCase extends TestCase {
	public void test1() {
		Object o = new Object();
		PipeQueue<Integer, Object> pq = new PipeQueue<Integer, Object>(3,
				new PipeQueue.OnDropListener<Integer>() {
					@Override
					public void onDrop(Integer key) {
					}
				}, new Comparator<Integer>() {
					@Override
					public int compare(Integer lhs, Integer rhs) {
						return lhs.compareTo(rhs);
					}
				});
		assertFalse(pq.add(1, o).isPresent());
		assertFalse(pq.add(2, o).isPresent());
		assertFalse(pq.add(3, o).isPresent());
		assertEquals(1, pq.add(4, o).get().getKey().intValue());
		assertEquals(1, pq.add(1, o).get().getKey().intValue());
		assertEquals(0, pq.add(0, o).get().getKey().intValue());
		assertEquals(2, pq.add(3, o).get().getKey().intValue());
	}
}
