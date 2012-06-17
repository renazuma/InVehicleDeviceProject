package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation.tilepipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.common.io.Closeables;
import com.google.common.math.DoubleMath;

public class TileBitmapFile {
	private static final Object BITMAP_FILE_ACCESS_LOCK = new Object();

	protected final Tile tile;
	protected final File directory;

	public TileBitmapFile(Tile tile, File directory) {
		this.tile = tile;
		this.directory = directory;
	}

	public boolean exists() {
		synchronized (BITMAP_FILE_ACCESS_LOCK) {
			return getFile().exists();
		}
	}

	protected File getFile() {
		return new File(directory, tile.getZoom() + "_" + tile.getX() + "_"
				+ tile.getY() + ".png");
	}

	public Bitmap getBitmap() throws IOException {
		synchronized (BITMAP_FILE_ACCESS_LOCK) {
			File file = getFile();
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			if (bitmap == null) {
				throw new IOException("BitmapFactory.decodeFile(" + file
						+ ") failed");
			}
			return alignAndSaveBitmap(bitmap, false);
		}
	}

	public Bitmap alignAndSaveBitmap(Bitmap bitmap, boolean alwaysSave)
			throws FileNotFoundException {
		File file = getFile();
		int alignedLength = (int) Math.pow(
				2,
				Math.floor(DoubleMath.log2(Math.max(bitmap.getWidth(),
						bitmap.getHeight()))));
		if (bitmap.getWidth() == alignedLength
				&& bitmap.getHeight() == alignedLength) {
			if (!alwaysSave) {
				return bitmap;
			}
		} else {
			Bitmap alignedBitmap = Bitmap.createBitmap(alignedLength,
					alignedLength, Bitmap.Config.RGB_565);
			Float left = (float) (alignedLength - bitmap.getWidth()) / 2;
			Float top = (float) (alignedLength - bitmap.getHeight()) / 2;
			new Canvas(alignedBitmap)
					.drawBitmap(bitmap, left, top, new Paint());
			bitmap.recycle();
			bitmap = alignedBitmap;
		}

		synchronized (BITMAP_FILE_ACCESS_LOCK) {
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(file);
				bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
			} finally {
				Closeables.closeQuietly(fileOutputStream);
				bitmap.recycle();
			}
		}
		return bitmap;
	}
}
