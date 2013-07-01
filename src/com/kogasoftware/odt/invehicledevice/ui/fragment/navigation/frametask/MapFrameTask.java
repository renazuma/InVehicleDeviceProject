package com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.frametask;

import java.util.Set;
import java.util.TreeSet;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.NavigationRenderer;
import com.kogasoftware.odt.invehicledevice.ui.fragment.navigation.Textures;
import com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline.Tile;
import com.kogasoftware.odt.invehicledevice.ui.frametask.navigation.tilepipeline.TilePipeline;

@FrameTask.Background
public class MapFrameTask extends FrameTask {

	private final TilePipeline tilePipeline;
	private final int defaultTextureId;
	private Tile lastCenterTile = new Tile(0, 0, 0);

	public MapFrameTask(TilePipeline tilePipeline, int defaultTextureId) {
		this.tilePipeline = tilePipeline;
		this.defaultTextureId = defaultTextureId;
	}

	@Override
	public void onChangeZoom(FrameState frameState) {
		tilePipeline.changeZoomLevel(frameState.getZoom());
	}

	@Override
	public void onRemove(FrameState frameState) {
		Textures.delete(frameState.getGL(), defaultTextureId);
	}

	@Override
	public void draw(FrameState frameState) {
		GL10 gl = frameState.getGL();
		tilePipeline.transferGL(gl);

		// 上下左右で追加で表示に必要なタイルを準備
		float cameraZoom = frameState.getCameraZoom();
		Tile centerTile = new Tile(frameState.getLatLng(), frameState.getZoom());

		int lineTiles = (int) Math.ceil(Math.max(frameState.getWidth()
				/ (double) Tile.WIDTH, frameState.getHeight()
				/ (double) Tile.HEIGHT)
				/ cameraZoom);
		if (lineTiles % 2 == 0) {
			lineTiles += 1;
		}
		int extraTiles = (lineTiles - 1) / 2 + 1;

		Set<Tile> inactiveTiles = tilePipeline.getPresentTiles();

		// 必要なタイルを中心に近い順にソートして追加
		Multimap<Double, Tile> tilesByDistance = LinkedListMultimap
				.<Double, Tile> create();
		for (int x = -extraTiles; x <= extraTiles; ++x) {
			for (int y = -extraTiles; y <= extraTiles; ++y) {
				for (Tile tile : centerTile.getRelativeTile(x, y).asSet()) {
					tilesByDistance.put((double) (x * x + y * y), tile);
				}
			}
		}
		for (Double distance : new TreeSet<Double>(tilesByDistance.keys())) {
			for (Tile tile : tilesByDistance.get(distance)) {
				int textureId = tilePipeline.pollOrStartLoad(tile).or(
						defaultTextureId);
				drawTile(frameState, tile, textureId);
				inactiveTiles.remove(tile);
			}
		}

		if (!lastCenterTile.equals(centerTile)) {
			lastCenterTile = centerTile;
			// 次のズームレベルのデータを先読みする
			// TODO:上のコードと共通化
			if (frameState.getZoom() < NavigationRenderer.MAX_ZOOM_LEVEL) {
				Tile nextCenterTile = new Tile(frameState.getLatLng(),
						frameState.getZoom() + 1);
				for (int x = -extraTiles; x <= extraTiles; ++x) {
					for (int y = -extraTiles; y <= extraTiles; ++y) {
						for (Tile tile : nextCenterTile.getRelativeTile(x, y)
								.asSet()) {
							tilePipeline.start(tile);
						}
					}
				}
			}
		}

		// 不要なタイルを削除
		for (Tile tile : inactiveTiles) {
			tilePipeline.remove(gl, tile);
		}
	}

	private void drawTile(FrameState frameState, Tile tile, int textureId) {
		float alpha = 1f;
		PointF point = tile.getCenterPixel();
		float scale = 1f / (1 << tile.getZoom());
		Textures.drawf(frameState.getGL(), textureId, point.x, point.y,
				Tile.WIDTH + 1, Tile.HEIGHT + 1, 0, scale, scale, alpha);
	}

	@Override
	public void onAdd(FrameState frameState) {
	}
}
