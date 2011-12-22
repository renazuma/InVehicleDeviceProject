package com.kogasoftware.viridian;

import java.util.Queue;

import android.content.Context;

public class HazardFrameTask extends FrameTask {
	private final HazardApproachingSprite hazardApproachingSprite;
	private final HazardPointSprite hazardPointSprite;
	private final Long startTime = System.currentTimeMillis();
	private final Integer id = 100;

	public HazardFrameTask(Context context, Hazard hazard) {
		hazardApproachingSprite = new HazardApproachingSprite(context);
		hazardPointSprite = new HazardPointSprite(context);
		setHazard(hazard);
	}

	@Override
	public void onAdd(Queue<FrameTask> frameTaskQueue) {
		frameTaskQueue.add(hazardApproachingSprite);
		frameTaskQueue.add(hazardPointSprite);
	}

	public void setHazard(Hazard hazard) {
		hazardPointSprite.setHazard(hazard);
		hazardApproachingSprite.setHazard(hazard);
	}

	public Integer getId() {
		return id;
	}

	@Override
	void onDraw(FrameState frameState) {
		if (startTime + 20000 < frameState.getMilliSeconds()) {
			remove();
			hazardApproachingSprite.remove();
			hazardPointSprite.remove();
		}
	}
}
