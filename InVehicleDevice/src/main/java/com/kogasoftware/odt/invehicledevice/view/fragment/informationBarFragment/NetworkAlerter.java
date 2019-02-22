package com.kogasoftware.odt.invehicledevice.view.fragment.informationBarFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;

import com.google.common.base.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * ネットワーク状態を監視し、定期的にダイアログメッセージを表示します。
 *
 * Created by tnoda on 2017/06/07.
 */

public class NetworkAlerter implements Runnable {
    private static final String NETWORK_ALERT_DIALOG_FRAGMENT_TAG = "NetworkAlertDialogFragmentTag";
    private static final int RUN_INTERVAL = 500; // 0.5 秒ごとに状態を確認
    private static final int NETWORK_DISCONNECT_LIMIT_MILLS = 3 * 60 * 1000;

    private Context context;
    private Handler handler;
    private ImageView networkIconView;
    private FragmentManager fragmentManager;
    private final Stopwatch dialogStopwatch = new Stopwatch();

    /**
     * ネットワーク警告ダイアログ
     */
    public static class NetworkAlertDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setMessage(Html
                    .fromHtml("<big><big>ネットワークに接続できません。<br />このメッセージが繰り返し表示される場合は、端末を再起動してください。<br />改善しない場合は、オペレータに連絡をお願いします。</big></big>"));
            builder.setPositiveButton(
                    Html.fromHtml("<big><big>確認</big></big>"), null);
            return builder.create();
        }
    }

    public NetworkAlerter(Context context, Handler handler,
                          ImageView networkIconView, FragmentManager fragmentManager) {
        this.context = context;
        this.handler = handler;
        this.networkIconView = networkIconView;
        this.fragmentManager = fragmentManager;
    }

    /**
     * ネットワーク状態を確認し状態が不通の場合、警告を表示します。
     */
    @Override
    public void run() {
        this.handler.postDelayed(this, RUN_INTERVAL);

        // ネットワーク状態を確認
        if (checkNetworkStatus()) {
            // ネットワークOK
            resetDialogStopWatch();
            unblinkNetworkIcon();
        } else {
            // ネットワークNG
            if (checkDialogShowTiming()) {
                restartDialogStopWatch();
                Fragment networkFragmentTag = getNetworkFragmentTag();

                if (networkFragmentTag == null) {
                    showDialog();
                }
            }
            blinkNetworkIcon();
        }
    }

    // ネットワークアイコンが点滅するように操作する
    private void blinkNetworkIcon() {
        if (networkIconView.isShown()) {
            networkIconView.setVisibility(View.INVISIBLE);
        } else {
            networkIconView.setVisibility(View.VISIBLE);
        }
    }

    // ネットワークアイコンの点滅を止める
    private void unblinkNetworkIcon() {
        if (networkIconView.isShown()) {
            return;
        }

        networkIconView.setVisibility(View.VISIBLE);
    }

    // ダイアログを表示する
    private void showDialog() {
        NetworkAlertDialogFragment fragment = new NetworkAlertDialogFragment();
        fragment.show(fragmentManager, NETWORK_ALERT_DIALOG_FRAGMENT_TAG);
    }

    // フラグメントタグを取得する
    private Fragment getNetworkFragmentTag() {
        return this.fragmentManager.findFragmentByTag(NETWORK_ALERT_DIALOG_FRAGMENT_TAG);
    }

    // ダイアログ表示タイミングストップウォッチをリスタート
    private void restartDialogStopWatch() {
        this.dialogStopwatch.reset().start();
    }

    // ダイアログ表示タイミングストップウォッチをリセット
    private void resetDialogStopWatch() {
        if (this.dialogStopwatch.isRunning()) {
            this.dialogStopwatch.stop().reset();
        }
    }

    // ダイアログ表示時間が来たどうかを判定する
    private boolean checkDialogShowTiming() {
        return !this.dialogStopwatch.isRunning()
                || this.dialogStopwatch.elapsed(TimeUnit.MILLISECONDS) > NETWORK_DISCONNECT_LIMIT_MILLS;
    }

    // ネットワーク状態を判定する
    private boolean checkNetworkStatus() {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // ネットワーク情報を取得する
    private NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager manager =
                (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return manager.getActiveNetworkInfo();
    }
}