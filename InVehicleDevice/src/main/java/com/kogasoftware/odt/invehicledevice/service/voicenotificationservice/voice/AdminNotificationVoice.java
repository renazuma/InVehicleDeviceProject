package com.kogasoftware.odt.invehicledevice.service.voicenotificationservice.voice;

import com.kogasoftware.odt.invehicledevice.R;

/**
 * 管理者からの連絡通知音声データクラス
 * Created by tnoda on 2017/06/12.
 */

public class AdminNotificationVoice implements Voice {
    @Override
    public int getVoiceFileResId() {
        return R.raw.admin_notification;
    }

    @Override
    public float getVolume() {
        return 1.0f;
    }
}
