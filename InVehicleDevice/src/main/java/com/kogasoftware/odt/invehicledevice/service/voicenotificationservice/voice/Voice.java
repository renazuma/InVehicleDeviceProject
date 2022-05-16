package com.kogasoftware.odt.invehicledevice.service.voicenotificationservice.voice;

import java.io.Serializable;

/**
 * 音声データを抽象化したインターフェース
 * Created by tnoda on 2017/06/12.
 */

public interface Voice extends Serializable {
    int getVoiceFileResId();

    float getVolume();
}
