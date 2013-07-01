package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.frametask;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.FrameState;

public class FrameTask {
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Background {
	}

	public void onChangeZoom(FrameState frameState) {
	}

	public void onRemove(FrameState frameState) {
	}

	public void draw(FrameState frameState) {
	}

	public void onAdd(FrameState frameState) {
	}
}
