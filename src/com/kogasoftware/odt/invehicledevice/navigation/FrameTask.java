package com.kogasoftware.odt.invehicledevice.navigation;

import java.util.Queue;

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

	public void onAdd(Queue<FrameTask> frameTaskQueue) {
	}
}
