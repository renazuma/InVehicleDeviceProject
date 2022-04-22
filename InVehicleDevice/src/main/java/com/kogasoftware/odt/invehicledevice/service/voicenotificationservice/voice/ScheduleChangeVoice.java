package com.kogasoftware.odt.invehicledevice.service.voicenotificationservice.voice;

import com.kogasoftware.odt.invehicledevice.R;

/**
 * 運行予定変更音声データクラス
 * Created by tnoda on 2017/06/12.
 */

public class ScheduleChangeVoice implements Voice {
    @Override
    public int getVoiceFileResId() {
        return R.raw.schedule_change;
    }

    @Override
    public float getVolume() {
        return 1.0f;
    }
}
