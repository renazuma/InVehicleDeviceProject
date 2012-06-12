package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class FrameTask {
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Front {
	}

	public void onRemove(FrameState frameState) {
	}

	void onDraw(FrameState frameState) {
	}

	public void onAdd(FrameState frameState) {
	}
}
