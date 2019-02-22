package com.kogasoftware.odt.invehicledevice.presenter.service.staticvoiceplayservice.voice;

import com.kogasoftware.odt.invehicledevice.R;

/**
 * チャイム音の音声データクラス
 * Created by tnoda on 2017/06/12.
 */

public class ChimeVoice implements Voice {
    @Override
    public int getVoiceFileResId() {
        return R.raw.chime;
    }

    @Override
    public float getVolume() {
        return 0.5f;
    }
}
