package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.util.Queue;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;

import com.javadocmd.simplelatlng.LatLng;

public class FrameState {
	private final GL10 gl;
	private final long milliSeconds;
	private final float angle;
	private final int zoom;
	private final LatLng latLng;
	private final Queue<FrameTask> addedFrameTasks;
	private final Queue<FrameTask> removedFrameTasks;

	public FrameState(GL10 gl, long milliSeconds, float angle, LatLng latLng,
			int zoom, Queue<FrameTask> addedFrameTasks,
			Queue<FrameTask> removedFrameTasks) {
		this.gl = gl;
		this.milliSeconds = milliSeconds;
		this.angle = angle;
		this.zoom = zoom;
		this.latLng = latLng;
		this.addedFrameTasks = addedFrameTasks;
		this.removedFrameTasks = removedFrameTasks;
	}

	public void addFrameTask(FrameTask frameTask) {
		addedFrameTasks.add(frameTask);
	}

	public void removeFrameTask(FrameTask frameTask) {
		removedFrameTasks.add(frameTask);
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

	public long getMilliSeconds() {
		return milliSeconds;
	}

	public float getAngle() {
		return angle;
	}

	public LatLng getLatLng() {
		return latLng;
	}

	public int getZoom() {
		return zoom;
	}
}
