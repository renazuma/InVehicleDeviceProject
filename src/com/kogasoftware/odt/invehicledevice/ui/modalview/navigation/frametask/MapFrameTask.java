package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.frametask;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.FrameState;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map.Tile;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map.TileFrameTask;
import com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.map.TilePipeline;

public class MapFrameTask extends FrameTask {
	private final Map<Tile, TileFrameTask> activeTileFrameTasks = new ConcurrentHashMap<Tile, TileFrameTask>();
	private final TilePipeline tilePipeline;

	public MapFrameTask(Context context, TilePipeline tilePipeline) {
		this.tilePipeline = tilePipeline;
	}

	@Override
	public void onChangeZoom(FrameState frameState) {
		tilePipeline.changeZoomLevel(frameState.getZoom());
	}

	@Override
	public void onRemove(FrameState frameState) {
	}

	private void addTile(FrameState frameState, final Tile tile) {
		if (activeTileFrameTasks.containsKey(tile)) {
			return;
		}

		// テクスチャが表示されていない場合
		for (TileFrameTask tileFrameTask : tilePipeline.pollOrStartLoad(tile)
				.asSet()) {
			// キャッシュにあった場合、追加
			activeTileFrameTasks.put(tile, tileFrameTask);
			frameState.addFrameTask(tileFrameTask);
		}
	}

	public void onDraw2(FrameState frameState) {
		// 上下左右で追加で表示に必要なタイルを準備
		float cameraZoom = frameState.getCameraZoom();
		int extraTiles = (int) Math.floor(Math.max(frameState.getHeight(),
				frameState.getWdith()) / cameraZoom / Tile.TILE_LENGTH / 2 + 1);
		Tile centerTile = new Tile(frameState.getLatLng(), frameState.getZoom());
		Map<Tile, TileFrameTask> inactiveTileFrameTasks = new HashMap<Tile, TileFrameTask>(
				activeTileFrameTasks);
		Multimap<Double, Tile> tilesByDistance = LinkedListMultimap
				.<Double, Tile> create();
		for (int x = -extraTiles; x <= extraTiles; ++x) {
			for (int y = -extraTiles; y <= extraTiles; ++y) {
				for (Tile tile : centerTile.getRelativeTile(x, y).asSet()) {
					tilesByDistance.put((double) (x * x + y * y), tile);
				}
			}
		}
		// 必要なタイルを中心に近い順にソートして追加
		for (Double distance : new TreeSet<Double>(tilesByDistance.keys())) {
			for (Tile tile : tilesByDistance.get(distance)) {
				addTile(frameState, tile);
				inactiveTileFrameTasks.remove(tile);
			}
		}
		// 不要なタイルを削除予約
		for (Tile inactiveTile : inactiveTileFrameTasks.keySet()) {
			activeTileFrameTasks.remove(inactiveTile);
		}
		for (FrameTask frameTask : inactiveTileFrameTasks.values()) {
			frameState.removeFrameTask(frameTask);
		}
	}

	@Override
	public void onAdd(FrameState frameState) {
	}
}
