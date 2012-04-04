package jp.tomorrowkey.android.vtextviewer;

import java.lang.ref.WeakReference;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.empty.EmptyThread;

/**
 * 縦に文字を表示するためのクラス
 * 
 * @see http://code.google.com/p/tomorrowkey
 */
public class VTextView extends View {
	/**
	 * onLayoutをトリガーにしてbitmapを作成したいが、同スレッドで行うと重いメモリ確保が起きる(AndroidLintに指摘される)
	 * ため別のスレッドで行うためのクラス. AsyncTaskはimmutableクラスなので結局newをする必要があるため自前で行うことにした
	 * 
	 * 親Viewの終了時onDetachedFromWindow()が呼ばれないことがあり、スレッドを終了できないことがあるため
	 * 親Viewへの参照はWeakReferenceを使う
	 */
	private static class UpdateBitmapThread extends Thread {
		private static final Integer TRY_ACQUIRE_TIMEOUT_MILLIS = 10000;
		private final WeakReference<VTextView> parentWeakReference;
		private final Semaphore updateBitmapStartSemaphore;

		public UpdateBitmapThread(VTextView parent) {
			this.updateBitmapStartSemaphore = parent.updateBitmapStartSemaphore;
			this.parentWeakReference = new WeakReference<VTextView>(parent);
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (!updateBitmapStartSemaphore.tryAcquire(
							TRY_ACQUIRE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
						// 定期的に親クラスが生きているかチェック
						if (parentWeakReference.get() == null) {
							return;
						}
						continue;
					}
					updateBitmapStartSemaphore.drainPermits();
					if (!updateParentBitmap()) {
						return;
					}
				}
			} catch (InterruptedException e) {
			}
		}

		private Boolean updateParentBitmap() {
			final VTextView parent = parentWeakReference.get();
			if (parent == null) {
				return false;
			}
			int local_height = parent.height; // widthとheightは途中で変更される可能性があるため、読み出しておく
			int local_width = parent.width;
			if (local_height <= 0 || local_width <= 0) {
				return true;
			}
			final Bitmap newBitmap = Bitmap.createBitmap(local_width,
					local_height, Bitmap.Config.ARGB_8888);
			parent.updateBitmapHandler.post(new Runnable() {
				@Override
				public void run() {
					parent.bitmap = newBitmap;
					parent.canvas.setBitmap(parent.bitmap);
					parent.invalidate();
				}
			});
			return true;
		}
	}

	// private static final String TAG = VTextView.class.getSimpleName();
	private static final int TOP_SPACE = 0;
	private static final int BOTTOM_SPACE = 18;
	private static final int FONT_SIZE = 60;
	private Typeface typeFace = Typeface.defaultFromStyle(Typeface.NORMAL);
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private String text = "";
	private Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
	private volatile int width = 0; // 別スレッドから読み出すためvolatileをつける
	private volatile int height = 0; // 別スレッドから読み出すためvolatileをつける
	private Canvas canvas = new Canvas(bitmap);
	private Semaphore updateBitmapStartSemaphore = new Semaphore(0);
	private Thread updateBitmapThread = new EmptyThread();
	private Handler updateBitmapHandler = new Handler();

	public VTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setTextSize(FONT_SIZE);
		paint.setColor(Color.BLACK);
		paint.setTypeface(typeFace);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		updateBitmapThread.interrupt();
		updateBitmapThread = new UpdateBitmapThread(this);
		updateBitmapThread.start();
	}

	@Override
	protected void onDetachedFromWindow() {
		updateBitmapThread.interrupt();
		try {
			updateBitmapThread.join();
		} catch (InterruptedException e) {
		}
		super.onDetachedFromWindow();
	}

	@Override
	public void onDraw(Canvas targetCanvas) {
		if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
			targetCanvas.drawColor(Color.WHITE);
			return;
		}
		bitmap.eraseColor(Color.WHITE);
		float fontSpacing = paint.getFontSpacing() * 0.8f;
		float lineSpacing = fontSpacing;
		float x = width - lineSpacing;
		float beginX = x;
		float y = TOP_SPACE + fontSpacing;
		String[] s = text.split("");

		boolean newLine = false;
		for (int i = 1; i < s.length; i++) {
			if (newLine) {
				// 改行処理
				x -= lineSpacing;
				y = TOP_SPACE + fontSpacing;
			}

			CharSetting setting = CharSetting.getSetting(s[i]);
			if (setting == null) {
				// 文字設定がない場合、そのまま描画
				canvas.drawText(s[i], x, y, paint);
			} else {
				// 文字設定が見つかったので、設定に従い描画
				canvas.save();
				canvas.rotate(setting.angle, x, y);
				canvas.drawText(s[i], x + fontSpacing * setting.x, y
						+ fontSpacing * setting.y, paint);
				canvas.restore();
			}

			if (y + fontSpacing > height - BOTTOM_SPACE) {
				// もう文字が入らない場合
				newLine = true;
			} else {
				newLine = false;
				// 文字を送る
				y += fontSpacing;
			}
		}

		// 中央揃え
		float x2 = -(width - beginX + x - fontSpacing) / 2;
		targetCanvas.drawBitmap(bitmap, x2, 0, paint);
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (width == getWidth() && height == getHeight()) {
			return;
		}
		width = getWidth();
		height = getHeight();
		updateBitmapStartSemaphore.release(); // 別スレッドでビットマップを再作成
	}

	public void setText(String text) {
		this.text = text;
	}
}
