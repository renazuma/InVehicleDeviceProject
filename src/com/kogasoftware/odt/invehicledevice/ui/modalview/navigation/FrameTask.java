package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

public class FrameTask {
	protected Boolean removed = false;

	public void remove() {
		removed = true;
	}

	public Boolean isRemoved() {
		return removed;
	}

	public void onDispose(FrameState frameState) {
	}

	void onDraw(FrameState frameState) {
	}

	public void onAdd(FrameState frameState) {
	}
}
