package jp.tomorrowkey.android.vtextviewer;

import java.util.concurrent.Semaphore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyThread;

/**
 * 縦に文字を表示するためのクラス
 * 
 * @see http://code.google.com/p/tomorrowkey
 */
public class VTextView extends View {
	/**
	 * onLayoutをトリガーにしてbitmapを作成したいが、
	 * 同スレッドでnewなどのメモリ確保をするとAndroidLintのwarningがおきる。
	 * そのため、別のスレッドでbitmap作成を行うためのクラス。
	 * AsyncTaskはimmutableクラスなので結局newをする必要があるため自前で作ることにした
	 */
	class UpdateBitmapThread extends Thread {
		@Override
		public void run() {
			try {
				while (true) {
					updateBitmapStartSemaphore.acquire();
					updateBitmapStartSemaphore.drainPermits();
					int localHeight = height; // widthとheightは途中で変更される可能性があるため、読み出しておく
					int localWidth = width;
					if (localHeight <= 0 || localWidth <= 0) {
						continue;
					}
					final Bitmap newBitmap = Bitmap.createBitmap(localWidth,
							localHeight, Bitmap.Config.ARGB_8888);
					updateBitmapHandler.post(new Runnable() {
						@Override
						public void run() {
							bitmap.recycle();
							bitmap = newBitmap;
							canvas.setBitmap(bitmap);
							invalidate();
						}
					});
				}
			} catch (InterruptedException e) {
			}
		}
	}

	private static final int BOTTOM_SPACE = 18;
	private static final int FONT_SIZE = 60;
	private static final float FONT_SPACING_RATE = 0.8f;
	private static final String TAG = VTextView.class.getSimpleName();
	private static final int TOP_SPACE = 0;
	private Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
	private final Canvas canvas = new Canvas(bitmap);
	private volatile int height = 0; // 別スレッドから読み出すためvolatileをつける
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private String text = "";
	private final Typeface typeFace = Typeface
			.defaultFromStyle(Typeface.NORMAL);
	private final Handler updateBitmapHandler = new Handler();
	private final Semaphore updateBitmapStartSemaphore = new Semaphore(0);
	private Thread updateBitmapThread = new EmptyThread();
	private volatile int width = 0; // 別スレッドから読み出すためvolatileをつける

	public VTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setTextSize(FONT_SIZE);
		paint.setColor(Color.BLACK);
		paint.setTypeface(typeFace);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		updateBitmapThread = new UpdateBitmapThread();
		updateBitmapThread.start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		updateBitmapThread.interrupt();
	}

	@Override
	public void onDraw(Canvas targetCanvas) {
		if (bitmap.getWidth() != width || bitmap.getHeight() != height) {
			targetCanvas.drawColor(Color.WHITE);
			return;
		}
		bitmap.eraseColor(Color.WHITE);
		float fontSpacing = paint.getFontSpacing() * FONT_SPACING_RATE;
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

			boolean cond = false;
			try {
				cond = y + fontSpacing > height - BOTTOM_SPACE;
				// TODO:
				// 上行でなぜかArrayIndexOutOfBoundsException発生報告がlogcatに出力されることがある。
				// 再現しないようなら削除する
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.e(TAG, e.toString(), e);
				return;
			}

			if (cond) {
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
