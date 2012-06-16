package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;

public class FrameTask {
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Background {
	}

	public void onChangeZoom(FrameState frameState) {
	}

	public void onRemove(FrameState frameState) {
	}

	public void onDraw(FrameState frameState) {
	}

	public void onAdd(FrameState frameState) {
	}
}
