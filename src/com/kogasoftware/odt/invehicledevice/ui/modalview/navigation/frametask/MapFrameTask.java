package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask;

import java.util.Set;
import java.util.TreeSet;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.kogasoftware.odt.invehicledevice.R;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.Textures;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.Tile;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline.TilePipeline;

public class MapFrameTask extends FrameTask {
	private final TilePipeline tilePipeline;
	private final Bitmap defaultBitmap;
	private int defaultTextureId = -1;

	public MapFrameTask(Context context, TilePipeline tilePipeline) {
		this.tilePipeline = tilePipeline;
		defaultBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.default_texture);
	}

	@Override
	public void onChangeZoom(FrameState frameState) {
		tilePipeline.changeZoomLevel(frameState.getZoom());
	}

	@Override
	public void onRemove(FrameState frameState) {
		if (!defaultBitmap.isRecycled()) {
			defaultBitmap.recycle();
		}
	}

	public void onDraw2(FrameState frameState) {
		Tile centerTile = new Tile(frameState.getLatLng(), frameState.getZoom());
		float alpha = 1f;
		Point point = centerTile.getCenterPixel();
		int x = point.x;
		int y = point.y;
		float scaleX = 1f;
		float scaleY = 1f;
		float angle = frameState.getAngle();
		Textures.draw(frameState.getGL(), defaultTextureId, point.x, point.y,
				Tile.TILE_LENGTH, Tile.TILE_LENGTH, angle, scaleX, scaleY,
				alpha);
	}

	@Override
	public void onDraw(FrameState frameState) {
		GL10 gl = frameState.getGL();
		tilePipeline.transferGL(gl);

		// 上下左右で追加で表示に必要なタイルを準備
		float cameraZoom = frameState.getCameraZoom();
		int extraTiles = (int) Math.floor(Math.max(frameState.getHeight(),
				frameState.getWdith()) / cameraZoom / Tile.TILE_LENGTH / 2 + 1);
		Tile centerTile = new Tile(frameState.getLatLng(), frameState.getZoom());

		Set<Tile> inactiveTiles = tilePipeline.getPresentTiles();
		// 必要なタイルを列挙し、中心に近い順にソート
		Multimap<Double, Tile> tilesByDistance = LinkedListMultimap
				.<Double, Tile> create();
		for (int x = -extraTiles; x <= extraTiles; ++x) {
			for (int y = -extraTiles; y <= extraTiles; ++y) {
				for (Tile tile : centerTile.getRelativeTile(x, y).asSet()) {
					tilesByDistance.put((double) (x * x + y * y), tile);
				}
			}
		}
		// 必要なタイルを表示、予約
		for (Double distance : new TreeSet<Double>(tilesByDistance.keys())) {
			for (Tile tile : tilesByDistance.get(distance)) {
				drawTile(frameState, tile, tilePipeline.pollOrStartLoad(tile)
						.or(defaultTextureId));
				inactiveTiles.remove(tile);
			}
		}
		// 不要なタイルを削除
		// for (Tile tile : inactiveTiles) {
		// tilePipeline.remove(gl, tile);
		// }
	}

	private void drawTile(FrameState frameState, Tile tile, int textureId) {
		float alpha = 1f;
		Point point = tile.getCenterPixel();
		float scale = 1f;
		Textures.draw(frameState.getGL(), textureId, point.x, point.y,
				Tile.TILE_LENGTH + 1, Tile.TILE_LENGTH + 1, 0, scale, scale,
				alpha);
	}

	@Override
	public void onAdd(FrameState frameState) {
		defaultTextureId = Textures.generate(frameState.getGL());
		Textures.update(frameState.getGL(), defaultTextureId, defaultBitmap);
	}
}
