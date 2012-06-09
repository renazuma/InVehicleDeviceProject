package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;

import com.javadocmd.simplelatlng.LatLng;

public class FrameState {
	private final GL10 gl;
	private final long milliSeconds;
	private final double radian;
	private final LatLng latLng;
	private final LinkedList<FrameTask> newFrameTasks = new LinkedList<FrameTask>();

	public FrameState(GL10 gl, long milliSeconds, double radian, LatLng latLng) {
		this.gl = gl;
		this.milliSeconds = milliSeconds;
		this.radian = radian;
		this.latLng = latLng;
	}

	public void addFrameTask(FrameTask frameTask) {
		frameTask.onAdd(this);
		newFrameTasks.add(frameTask);
	}

	public LinkedList<FrameTask> getNewFrameTasks() {
		return newFrameTasks;
	}

	/**
	 * LatLngを描画先の座標へ変換
	 * 
	 * @param frameState
	 * @param geoPoint
	 * @return
	 */
	PointF convertLatLngToPointF(LatLng latLng) {
		return new PointF(0, 0);
	}

	public GL10 getGL() {
		return gl;
	}

	public Long getMilliSeconds() {
		return milliSeconds;
	}
}
