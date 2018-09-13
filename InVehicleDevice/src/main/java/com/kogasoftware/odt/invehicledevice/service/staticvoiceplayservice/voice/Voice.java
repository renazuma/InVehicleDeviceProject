package com.kogasoftware.odt.invehicledevice.service.staticvoiceplayservice.voice;

import java.io.Serializable;

/**
 * 音声データを抽象化したインターフェース
 * Created by tnoda on 2017/06/12.
 */

public interface Voice extends Serializable {
    public int getVoiceFileResId();
    public float getVolume();
}
