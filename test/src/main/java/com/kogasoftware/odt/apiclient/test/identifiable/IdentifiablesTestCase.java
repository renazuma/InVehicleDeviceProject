package com.kogasoftware.odt.apiclient.test.identifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import com.google.common.collect.Lists;
import com.kogasoftware.odt.apiclient.identifiable.Identifiable;
import com.kogasoftware.odt.apiclient.identifiable.Identifiables;

public class IdentifiablesTestCase extends TestCase {
	static class Test implements Identifiable {
		private final Integer id;

		public Test(Integer id) {
			this.id = id;
		}

		@Override
		public Integer getId() {
			return id;
		}
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * getId()が同一のものが存在しなければfalseを返す
	 */
	public void testContains_1() throws Exception {
		// 空
		List<Test> l = new LinkedList<Test>();
		Test t = new Test(1);
		assertFalse(Identifiables.contains(l, t));
		assertEquals(l.size(), 0);

		// 単数
		l.add(new Test(2));
		assertFalse(Identifiables.contains(l, t));
		assertEquals(l.size(), 1);

		// 複数
		l.add(new Test(3));
		assertFalse(Identifiables.contains(l, t));
		assertEquals(l.size(), 2);
	}

	/**
	 * getId()が同一のものが存在すればtrueを返す
	 */
	public void testContains_2() throws Exception {
		List<Test> l = new LinkedList<Test>();

		// 単数
		l.add(new Test(12345));
		assertFalse(l.contains(new Test(12345))); // 別のオブジェクトと判定されfalse
		assertTrue(Identifiables.contains(l, new Test(12345)));

		// 複数
		l.add(new Test(123));
		assertFalse(l.contains(new Test(123))); // 別のオブジェクトと判定されfalse
		assertTrue(Identifiables.contains(l, new Test(12345)));
		assertTrue(Identifiables.contains(l, new Test(123)));
	}

	/**
	 * ランダムにテスト
	 */
	public void testContains_3() throws Exception {
		Collection<Test> c = Lists.newArrayList(new Test(1), new Test(-1),
				new Test(3), new Test(1), new Test(4));
		assertEquals(c.size(), 5);
		assertFalse(Identifiables.contains(c, new Test(-2)));
		assertTrue(Identifiables.contains(c, new Test(-1)));
		assertFalse(Identifiables.contains(c, new Test(0)));
		assertTrue(Identifiables.contains(c, new Test(1)));
		assertFalse(Identifiables.contains(c, new Test(2)));
		assertTrue(Identifiables.contains(c, new Test(3)));
		assertTrue(Identifiables.contains(c, new Test(4)));
		assertFalse(Identifiables.contains(c, new Test(5)));
		assertEquals(c.size(), 5);
	}

	/**
	 * 追加されないテスト
	 */
	public void testMerge_1_1() throws Exception {
		// 別のオブジェクトの場合
		List<Test> l = new LinkedList<Test>();
		Test t = new Test(56789);
		l.add(t);
		assertFalse(Identifiables.merge(l, new Test(56789)));
		assertTrue(Identifiables.contains(l, t));
	}

	/**
	 * 追加されるテスト
	 */
	public void testMerge_1_2() throws Exception {
		{ // 空
			List<Test> l = new LinkedList<Test>();
			assertTrue(Identifiables.merge(l, new Test(135)));
			assertTrue(Identifiables.contains(l, new Test(135)));
			assertEquals(l.size(), 1);
		}

		{ // 要素1つ
			List<Test> l = new LinkedList<Test>();
			l.add(new Test(579));
			assertTrue(Identifiables.merge(l, new Test(135)));
			assertTrue(Identifiables.contains(l, new Test(135)));
			assertEquals(l.size(), 2);
		}

		{ // 要素複数
			List<Test> l = Lists.newArrayList(new Test(5), new Test(6),
					new Test(6));
			assertTrue(Identifiables.merge(l, new Test(1358)));
			assertTrue(Identifiables.contains(l, new Test(1358)));
			assertEquals(l.size(), 4);
		}
	}

	/**
	 * ランダムにテスト
	 */
	public void testMerge_1_3() throws Exception {
		ArrayList<Test> c = Lists.newArrayList(new Test(1), new Test(-1),
				new Test(3), new Test(1), new Test(4));
		assertEquals(c.size(), 5);
		assertTrue(Identifiables.merge(c, new Test(-2)));
		assertEquals(c.size(), 6);
		assertFalse(Identifiables.merge(c, new Test(-2)));
		assertEquals(c.size(), 6);
		assertFalse(Identifiables.merge(c, new Test(-1)));
		assertEquals(c.size(), 6);
		assertTrue(Identifiables.merge(c, new Test(0)));
		assertEquals(c.size(), 7);
		assertFalse(Identifiables.merge(c, new Test(1)));
		assertEquals(c.size(), 7);
		assertTrue(Identifiables.merge(c, new Test(2)));
		assertEquals(c.size(), 8);
		assertFalse(Identifiables.merge(c, new Test(3)));
		assertEquals(c.size(), 8);
		assertFalse(Identifiables.merge(c, new Test(4)));
		assertEquals(c.size(), 8);
		assertTrue(Identifiables.merge(c, new Test(5)));
		assertEquals(c.size(), 9);

		assertEquals(c.get(0).getId().intValue(), 1);
		assertEquals(c.get(1).getId().intValue(), -1);
		assertEquals(c.get(2).getId().intValue(), 3);
		assertEquals(c.get(3).getId().intValue(), 1);
		assertEquals(c.get(4).getId().intValue(), 4);
		assertEquals(c.get(5).getId().intValue(), -2);
		assertEquals(c.get(6).getId().intValue(), 0);
		assertEquals(c.get(7).getId().intValue(), 2);
		assertEquals(c.get(8).getId().intValue(), 5);
	}

	/**
	 * 追加されないテスト
	 */
	public void testMerge_2_1() throws Exception {
		List<Test> l = Lists.newArrayList(new Test(1));
		assertEquals(l.size(), 1);
		assertFalse(Identifiables.merge(l, new ArrayList<Test>()));
		assertEquals(l.size(), 1);
		l.add(new Test(1));
		assertFalse(Identifiables.merge(l, new ArrayList<Test>()));
		assertEquals(l.size(), 2);
		assertFalse(Identifiables.merge(l, Lists.newArrayList(new Test(1))));
		assertEquals(l.size(), 2);
	}

	/**
	 * 追加されるテスト
	 */
	public void testMerge_2_2() throws Exception {
		{ // 空
			List<Test> l1 = new LinkedList<Test>();
			assertTrue(Identifiables.merge(l1, Lists.newArrayList(new Test(5))));
			assertEquals(l1.size(), 1);
			assertEquals(l1.get(0).getId().intValue(), 5);

			List<Test> l2 = new LinkedList<Test>();
			assertTrue(Identifiables.merge(l2,
					Lists.newArrayList(new Test(5), new Test(6))));
			assertEquals(l2.size(), 2);
			assertEquals(l2.get(0).getId().intValue(), 5);
			assertEquals(l2.get(1).getId().intValue(), 6);
		}

		{ // 要素1つ
			List<Test> l1 = Lists.newArrayList(new Test(8));
			assertTrue(Identifiables.merge(l1, Lists.newArrayList(new Test(5))));
			assertEquals(l1.size(), 2);
			assertEquals(l1.get(0).getId().intValue(), 8);
			assertEquals(l1.get(1).getId().intValue(), 5);

			List<Test> l2 = Lists.newArrayList(new Test(8));
			assertTrue(Identifiables.merge(l2,
					Lists.newArrayList(new Test(8), new Test(6))));
			assertEquals(l2.size(), 2);
			assertEquals(l2.get(0).getId().intValue(), 8);
			assertEquals(l2.get(1).getId().intValue(), 6);

			List<Test> l3 = Lists.newArrayList(new Test(9));
			assertTrue(Identifiables.merge(l3,
					Lists.newArrayList(new Test(8), new Test(9))));
			assertEquals(l3.size(), 2);
			assertEquals(l3.get(0).getId().intValue(), 9);
			assertEquals(l3.get(1).getId().intValue(), 8);
		}

		{ // 要素複数
			List<Test> l1 = Lists.newArrayList(new Test(5), new Test(6),
					new Test(7));
			assertTrue(Identifiables.merge(l1, Lists.newArrayList(new Test(8))));
			assertEquals(l1.size(), 4);
			assertEquals(l1.get(0).getId().intValue(), 5);
			assertEquals(l1.get(1).getId().intValue(), 6);
			assertEquals(l1.get(2).getId().intValue(), 7);
			assertEquals(l1.get(3).getId().intValue(), 8);

			List<Test> l2 = Lists.newArrayList(new Test(5), new Test(6),
					new Test(7));
			assertTrue(Identifiables.merge(l2,
					Lists.newArrayList(new Test(5), new Test(8))));
			assertEquals(l2.size(), 4);
			assertEquals(l2.get(0).getId().intValue(), 5);
			assertEquals(l2.get(1).getId().intValue(), 6);
			assertEquals(l2.get(2).getId().intValue(), 7);
			assertEquals(l2.get(3).getId().intValue(), 8);

			List<Test> l3 = Lists.newArrayList(new Test(5), new Test(6),
					new Test(7));
			assertTrue(Identifiables.merge(l3, Lists.newArrayList(new Test(-1),
					new Test(-1), new Test(6), new Test(5))));
			assertEquals(l3.size(), 4);
			assertEquals(l3.get(0).getId().intValue(), 5);
			assertEquals(l3.get(1).getId().intValue(), 6);
			assertEquals(l3.get(2).getId().intValue(), 7);
			assertEquals(l3.get(3).getId().intValue(), -1);
		}
	}

	/**
	 * ランダムにテスト
	 */
	public void testMerge_2_3() throws Exception {
		ArrayList<Test> c = Lists.newArrayList(new Test(1), new Test(-1),
				new Test(3), new Test(1), new Test(4));
		assertEquals(c.size(), 5);
		assertTrue(Identifiables.merge(c,
				Lists.newArrayList(new Test(-2), new Test(-3))));
		assertEquals(c.size(), 7);
		assertFalse(Identifiables.merge(c,
				Lists.newArrayList(new Test(-2), new Test(-3))));
		assertEquals(c.size(), 7);
		assertFalse(Identifiables.merge(c,
				Lists.newArrayList(new Test(-1), new Test(-1), new Test(-1))));
		assertEquals(c.size(), 7);
		assertTrue(Identifiables.merge(c, Lists.newArrayList(new Test(0))));
		assertEquals(c.size(), 8);
		assertFalse(Identifiables.merge(c,
				Lists.newArrayList(new Test(1), new Test(3), new Test(4))));
		assertEquals(c.size(), 8);
		assertTrue(Identifiables.merge(c, Lists.newArrayList(new Test(2))));
		assertEquals(c.size(), 9);
		assertFalse(Identifiables.merge(c, Lists.newArrayList(new Test(3))));
		assertEquals(c.size(), 9);
		assertFalse(Identifiables.merge(c,
				Lists.newArrayList(new Test(-3), new Test(3), new Test(0))));
		assertEquals(c.size(), 9);
		assertTrue(Identifiables.merge(c,
				Lists.newArrayList(new Test(5), new Test(-5), new Test(0))));
		assertEquals(c.size(), 11);

		assertEquals(c.get(0).getId().intValue(), 1);
		assertEquals(c.get(1).getId().intValue(), -1);
		assertEquals(c.get(2).getId().intValue(), 3);
		assertEquals(c.get(3).getId().intValue(), 1);
		assertEquals(c.get(4).getId().intValue(), 4);
		assertEquals(c.get(5).getId().intValue(), -2);
		assertEquals(c.get(6).getId().intValue(), -3);
		assertEquals(c.get(7).getId().intValue(), 0);
		assertEquals(c.get(8).getId().intValue(), 2);
		assertEquals(c.get(9).getId().intValue(), 5);
		assertEquals(c.get(10).getId().intValue(), -5);
	}

	public void testConstructor() {
		// カバレッジを満たすためのコード.今のところコンストラクタを使うことは無い.
		new Identifiables();
	}
}
