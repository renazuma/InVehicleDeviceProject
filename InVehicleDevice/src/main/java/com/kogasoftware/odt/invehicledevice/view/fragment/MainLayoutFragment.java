package com.kogasoftware.odt.invehicledevice.view.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.OperationSchedule.Phase;
import com.kogasoftware.odt.invehicledevice.infra.contentprovider.table.PassengerRecord;
import com.kogasoftware.odt.invehicledevice.view.activity.InVehicleDeviceActivity;
import com.kogasoftware.odt.invehicledevice.view.fragment.controlbar.ControlBarFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.phasecontent.DrivePhaseFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.phasecontent.FinishPhaseFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.informationbar.InformationBarFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.phasecontent.PlatformPhaseFragment;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationPhase;
import com.kogasoftware.odt.invehicledevice.view.fragment.utils.OperationSchedulesSyncFragmentAbstract;

import java.util.LinkedList;

/**
 * 順番に運行を進める画面。上部に「InformationBarFragment」右部に「ControlBarFragment」中心に「**PhaseFragment」を配置する
 */
public class MainLayoutFragment extends OperationSchedulesSyncFragmentAbstract {

	private static final String LOGGING_TAG = MainLayoutFragment.class.getSimpleName();

	// TODO: Activityは一つしかないので、InVehicleDeviceActivityの指定は不要では？
	private static final String FRAGMENT_TAG = InVehicleDeviceActivity.class + "/" + MainLayoutFragment.class;

	public static MainLayoutFragment newInstance() {
		return new MainLayoutFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// トップ画面の空白のコンテナフラグメントに、実運用画面のフレームフラグメントを渡す（中身は別途設定される）
		return inflater.inflate(R.layout.main_layout_fragment, container,	false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// トップ画面の右部分のボタンのコンテナフラグメント、ヘッダフラグメントを設定
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.add(R.id.control_bar_fragment_container, ControlBarFragment.newInstance());
		fragmentTransaction.add(R.id.information_bar_fragment_container, InformationBarFragment.newInstance());
		fragmentTransaction.commitAllowingStateLoss();
	}


	// 運行スケジュールが新しく同期される度に、運行メイン画面を最新化する
	// TODO: control_fragmentやinformation_fragmentに合わせるのであれば、phase表示をコントロールする別クラスで用意し、ここはコンテナに徹するべきでは？
    // TODO: もしくは、fragmentTransactionのメソッドは、コンテナを管理するこのクラスで実行するという方針？
	// TODO: phaseコンテナがメインコンテンツ部分だという事が分かりにくいので、名前を変えたい。
	@Override
	protected void onOperationSchedulesAndPassengerRecordsLoadFinished(
			LinkedList<OperationSchedule> operationSchedules,
			LinkedList<PassengerRecord> passengerRecords, Boolean phaseChanged) {
		Phase phase = OperationSchedule.getPhase(operationSchedules, passengerRecords);
		Log.i(LOGGING_TAG, "phase=" + phase + " phaseChanged=" + phaseChanged);

		if (!phaseChanged) { return; }

		// スケジュール同期後に、phase（運行中、運行終了等）フラグメントを設定
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

		switch (phase) {
			case FINISH:
				fragmentTransaction.replace(R.id.phase_content_fragment_container, FinishPhaseFragment.newInstance());
				break;
			case DRIVE:
				fragmentTransaction.replace(R.id.phase_content_fragment_container, DrivePhaseFragment.newInstance(new OperationPhase(operationSchedules, passengerRecords)));
				break;
			case PLATFORM_GET_ON:
				fragmentTransaction.replace(R.id.phase_content_fragment_container, PlatformPhaseFragment.newInstance());
				break;
			case PLATFORM_GET_OFF:
				fragmentTransaction.replace(R.id.phase_content_fragment_container, PlatformPhaseFragment.newInstance());
				break;
		}

		fragmentTransaction.commitAllowingStateLoss();
	}

	// 実運用画面の表示
	// TODO: 共通処理のshowModalFragmentと、customAnimation以外は変わらない。共通処理を使っていないのはそこが理由なのかを確認。
	// TODO: 既存に合わせるためにstaticにしている。出来れば変えたい。
	public static void showModal(InVehicleDeviceActivity inVehicleDeviceActivity) {
		if (inVehicleDeviceActivity.destroyed) { return; }

		FragmentManager fragmentManager = inVehicleDeviceActivity.getFragmentManager();

		if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) != null) { return; }

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.add(R.id.modal_fragment_container, MainLayoutFragment.newInstance(), FRAGMENT_TAG);
		fragmentTransaction.commitAllowingStateLoss();
	}

	// TODO: 共通処理のhideは使えない？確認する。
	// TODO: 既存に合わせるためにstaticにしている。出来れば変えたい。
	public static void hideModal(InVehicleDeviceActivity inVehicleDeviceActivity) {
		if (inVehicleDeviceActivity.destroyed) { return; }

		FragmentManager fragmentManager = inVehicleDeviceActivity.getFragmentManager();

		if (fragmentManager.findFragmentByTag(FRAGMENT_TAG) == null) { return; }

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.remove(fragmentManager.findFragmentByTag(FRAGMENT_TAG));
		fragmentTransaction.commitAllowingStateLoss();
	}
}
