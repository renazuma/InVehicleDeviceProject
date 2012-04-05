package com.kogasoftware.odt.invehicledevice.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;
import com.kogasoftware.odt.invehicledevice.InVehicleDeviceActivity;

public class LeakTestCase extends
		ActivityInstrumentationTestCase2<InVehicleDeviceActivity> {

	private Solo solo;
	private static int phase = 0;

	public LeakTestCase() {
		super("com.kogasoftware.odt.invehicledevice",
				InVehicleDeviceActivity.class);
	}

	private void a() {
		phase++;
		if (phase % 3 == 0) {
			solo.drag(10, 10, 200, 200, 5);
		}
		if (phase % 7 == 0) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		}
		if (phase % 4 == 0) {
			solo.sendKey(Solo.ENTER);
		}
		if (phase % 8 == 0) {
			solo.setActivityOrientation(Solo.PORTRAIT);
		}
		if (phase % 5 == 0) {
			solo.goBack();
		}
		if (phase % 11 == 0) {
			for (int i = 0; i < 20; ++i) {
				solo.setActivityOrientation(Solo.PORTRAIT);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				solo.setActivityOrientation(Solo.LANDSCAPE);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	public void tearDown() throws Exception {
		solo.finishOpenedActivities();
		solo = null;
		super.tearDown();
	}

	public void test001() {
		a();
	}

	public void test002() {
		a();
	}

	public void test003() {
		a();
	}

	public void test004() {
		a();
	}

	public void test005() {
		a();
	}

	public void test006() {
		a();
	}

	public void test007() {
		a();
	}

	public void test008() {
		a();
	}

	public void test009() {
		a();
	}

	public void test010() {
		a();
	}

	public void test011() {
		a();
	}

	public void test012() {
		a();
	}

	public void test013() {
		a();
	}

	public void test014() {
		a();
	}

	public void test015() {
		a();
	}

	public void test016() {
		a();
	}

	public void test017() {
		a();
	}

	public void test018() {
		a();
	}

	public void test019() {
		a();
	}

	public void test020() {
		a();
	}

	public void test021() {
		a();
	}

	public void test022() {
		a();
	}

	public void test023() {
		a();
	}

	public void test024() {
		a();
	}

	public void test025() {
		a();
	}

	public void test026() {
		a();
	}

	public void test027() {
		a();
	}

	public void test028() {
		a();
	}

	public void test029() {
		a();
	}

	public void test030() {
		a();
	}

	public void test031() {
		a();
	}

	public void test032() {
		a();
	}

	public void test033() {
		a();
	}

	public void test034() {
		a();
	}

	public void test035() {
		a();
	}

	public void test036() {
		a();
	}

	public void test037() {
		a();
	}

	public void test038() {
		a();
	}

	public void test039() {
		a();
	}

	public void test040() {
		a();
	}

	public void test041() {
		a();
	}

	public void test042() {
		a();
	}

	public void test043() {
		a();
	}

	public void test044() {
		a();
	}

	public void test045() {
		a();
	}

	public void test046() {
		a();
	}

	public void test047() {
		a();
	}

	public void test048() {
		a();
	}

	public void test049() {
		a();
	}

	public void test050() {
		a();
	}

	public void test051() {
		a();
	}

	public void test052() {
		a();
	}

	public void test053() {
		a();
	}

	public void test054() {
		a();
	}

	public void test055() {
		a();
	}

	public void test056() {
		a();
	}

	public void test057() {
		a();
	}

	public void test058() {
		a();
	}

	public void test059() {
		a();
	}

	public void test060() {
		a();
	}

	public void test061() {
		a();
	}

	public void test062() {
		a();
	}

	public void test063() {
		a();
	}

	public void test064() {
		a();
	}

	public void test065() {
		a();
	}

	public void test066() {
		a();
	}

	public void test067() {
		a();
	}

	public void test068() {
		a();
	}

	public void test069() {
		a();
	}

	public void test070() {
		a();
	}

	public void test071() {
		a();
	}

	public void test072() {
		a();
	}

	public void test073() {
		a();
	}

	public void test074() {
		a();
	}

	public void test075() {
		a();
	}

	public void test076() {
		a();
	}

	public void test077() {
		a();
	}

	public void test078() {
		a();
	}

	public void test079() {
		a();
	}

	public void test080() {
		a();
	}

	public void test081() {
		a();
	}

	public void test082() {
		a();
	}

	public void test083() {
		a();
	}

	public void test084() {
		a();
	}

	public void test085() {
		a();
	}

	public void test086() {
		a();
	}

	public void test087() {
		a();
	}

	public void test088() {
		a();
	}

	public void test089() {
		a();
	}

	public void test090() {
		a();
	}

	public void test091() {
		a();
	}

	public void test092() {
		a();
	}

	public void test093() {
		a();
	}

	public void test094() {
		a();
	}

	public void test095() {
		a();
	}

	public void test096() {
		a();
	}

	public void test097() {
		a();
	}

	public void test098() {
		a();
	}

	public void test099() {
		a();
	}

	public void test100() {
		a();
	}

	public void test101() {
		a();
	}

	public void test102() {
		a();
	}

	public void test103() {
		a();
	}

	public void test104() {
		a();
	}

	public void test105() {
		a();
	}

	public void test106() {
		a();
	}

	public void test107() {
		a();
	}

	public void test108() {
		a();
	}

	public void test109() {
		a();
	}

	public void test110() {
		a();
	}

	public void test111() {
		a();
	}

	public void test112() {
		a();
	}

	public void test113() {
		a();
	}

	public void test114() {
		a();
	}

	public void test115() {
		a();
	}

	public void test116() {
		a();
	}

	public void test117() {
		a();
	}

	public void test118() {
		a();
	}

	public void test119() {
		a();
	}

	public void test120() {
		a();
	}

	public void test121() {
		a();
	}

	public void test122() {
		a();
	}

	public void test123() {
		a();
	}

	public void test124() {
		a();
	}

	public void test125() {
		a();
	}

	public void test126() {
		a();
	}

	public void test127() {
		a();
	}

	public void test128() {
		a();
	}

	public void test129() {
		a();
	}

	public void test130() {
		a();
	}

	public void test131() {
		a();
	}

	public void test132() {
		a();
	}

	public void test133() {
		a();
	}

	public void test134() {
		a();
	}

	public void test135() {
		a();
	}

	public void test136() {
		a();
	}

	public void test137() {
		a();
	}

	public void test138() {
		a();
	}

	public void test139() {
		a();
	}

	public void test140() {
		a();
	}

	public void test141() {
		a();
	}

	public void test142() {
		a();
	}

	public void test143() {
		a();
	}

	public void test144() {
		a();
	}

	public void test145() {
		a();
	}

	public void test146() {
		a();
	}

	public void test147() {
		a();
	}

	public void test148() {
		a();
	}

	public void test149() {
		a();
	}

	public void test150() {
		a();
	}

	public void test151() {
		a();
	}

	public void test152() {
		a();
	}

	public void test153() {
		a();
	}

	public void test154() {
		a();
	}

	public void test155() {
		a();
	}

	public void test156() {
		a();
	}

	public void test157() {
		a();
	}

	public void test158() {
		a();
	}

	public void test159() {
		a();
	}

	public void test160() {
		a();
	}

	public void test161() {
		a();
	}

	public void test162() {
		a();
	}

	public void test163() {
		a();
	}

	public void test164() {
		a();
	}

	public void test165() {
		a();
	}

	public void test166() {
		a();
	}

	public void test167() {
		a();
	}

	public void test168() {
		a();
	}

	public void test169() {
		a();
	}

	public void test170() {
		a();
	}

	public void test171() {
		a();
	}

	public void test172() {
		a();
	}

	public void test173() {
		a();
	}

	public void test174() {
		a();
	}

	public void test175() {
		a();
	}

	public void test176() {
		a();
	}

	public void test177() {
		a();
	}

	public void test178() {
		a();
	}

	public void test179() {
		a();
	}

	public void test180() {
		a();
	}

	public void test181() {
		a();
	}

	public void test182() {
		a();
	}

	public void test183() {
		a();
	}

	public void test184() {
		a();
	}

	public void test185() {
		a();
	}

	public void test186() {
		a();
	}

	public void test187() {
		a();
	}

	public void test188() {
		a();
	}

	public void test189() {
		a();
	}

	public void test190() {
		a();
	}

	public void test191() {
		a();
	}

	public void test192() {
		a();
	}

	public void test193() {
		a();
	}

	public void test194() {
		a();
	}

	public void test195() {
		a();
	}

	public void test196() {
		a();
	}

	public void test197() {
		a();
	}

	public void test198() {
		a();
	}

	public void test199() {
		a();
	}

	public void test200() {
		a();
	}
}