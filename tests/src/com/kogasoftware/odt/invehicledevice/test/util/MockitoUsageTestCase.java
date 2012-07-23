package com.kogasoftware.odt.invehicledevice.test.util;

import com.kogasoftware.odt.invehicledevice.test.util.MockitoUsageTest.Warehouse;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;

class Order {

	public Order(String product, int quantity) {
		this.product = product;
		this.quantity = quantity;
	}

	public void fill(Warehouse warehouse) {
		if (warehouse.hasInventory(product, quantity)) {
			warehouse.remove(product, quantity);
			filled = true;
		}
	}

	public boolean isFilled() {
		return filled;
	}

	private boolean filled = false;
	private String product;
	private int quantity;
}

public class MockitoUsageTestCase extends TestCase {

	public static abstract class Warehouse {
		public abstract boolean hasInventory(String product, int quantity);

		public abstract void remove(String product, int quantity);

		public int add(int l, int r) {
			return l + r;
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test1() {
		assertTrue(true);
	}

	public void test2() {
		Warehouse mockWarehouse = mock(Warehouse.class);
		Warehouse implementedWarehouse = new Warehouse() {
			@Override
			public boolean hasInventory(String product, int quantity) {
				return false;
			}

			@Override
			public void remove(String product, int quantity) {
			}
		};
		
		assertNotSame(10, mockWarehouse.add(2, 8));
		assertEquals(10, implementedWarehouse.add(2, 8));
	}

	public void testInStock() {
		Warehouse mockWarehouse = mock(Warehouse.class);

		when(mockWarehouse.hasInventory("Talisker", 50)).thenReturn(true);

		Order order = new Order("Talisker", 50);
		order.fill(mockWarehouse);

		assertTrue(order.isFilled());
		verify(mockWarehouse).remove("Talisker", 50);
	}

	public void testOutOfStock() {
		Warehouse mockWarehouse = mock(Warehouse.class);

		when(mockWarehouse.hasInventory("Talisker", 50)).thenReturn(false);

		Order order = new Order("Talisker", 50);
		order.fill(mockWarehouse);

		assertFalse(order.isFilled());
	}
}
