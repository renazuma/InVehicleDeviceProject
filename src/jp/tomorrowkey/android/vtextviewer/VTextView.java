package jp.tomorrowkey.android.vtextviewer;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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

import com.google.common.base.Strings;
import com.kogasoftware.odt.invehicledevice.logic.empty.EmptyThread;

/**
 * 縦に文字を表示するためのクラス
 * 
 * @see http://code.google.com/p/tomorrowkey
 */
public class VTextView extends View {
	static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	final AtomicInteger width = new AtomicInteger(0);
	final AtomicInteger height = new AtomicInteger(0);
	final Semaphore updateBitmapStartSemaphore = new Semaphore(0);
	final AtomicReference<Bitmap> preparedBitmap = new AtomicReference<Bitmap>(
			Bitmap.createBitmap(1, 1, BITMAP_CONFIG));
	final Handler handler = new Handler();
	
	protected final Paint paint = new Paint();
	protected Bitmap bitmap = Bitmap.createBitmap(1, 1, BITMAP_CONFIG);
	protected String text = "";
	protected Thread updateBitmapThread = new EmptyThread();

	public VTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (updateBitmapThread.isAlive()) {
			updateBitmapThread.interrupt();
		}
		updateBitmapThread = new VTextViewDrawThread(this);
		updateBitmapThread.start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		updateBitmapThread.interrupt();
	}

	@Override
	public void onDraw(Canvas targetCanvas) {
		super.onDraw(targetCanvas);
		
		// 新しいビットマップがある場合交換する
		Bitmap newBitmap = preparedBitmap.getAndSet(null);
		if (newBitmap != null) {
			bitmap.recycle();
			bitmap = newBitmap;
		}

		// ビットマップの大きさがあわないばあい別スレッドでビットマップを作成開始
		if (bitmap.getWidth() != width.get()
				|| bitmap.getHeight() != height.get()) {
			updateBitmapStartSemaphore.release();
		}

		targetCanvas.drawBitmap(bitmap, 0, 0, paint);
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (width.get() == getWidth() && height.get() == getHeight()) {
			return;
		}
		width.set(getWidth());
		height.set(getHeight());
		updateBitmapStartSemaphore.release();
	}

	public void setText(String text) {
		this.text = Strings.nullToEmpty(text);
		updateBitmapStartSemaphore.release();
	}

	public String getText() {
		return text;
	}
}

/**
 * onLayoutをトリガーにしてbitmapを作成したいが、 同スレッドでnewなどのメモリ確保をするとAndroidLintのwarningがおきる。
 * そのため、別のスレッドでbitmap作成を行うためのクラス。
 * AsyncTaskはimmutableクラスなので結局newをする必要があるため自前で作ることにした
 */
class VTextViewDrawThread extends Thread {
	private static final int BOTTOM_SPACE = 18;
	private static final float FONT_SPACING_RATE = 0.8f;
	private static final String TAG = VTextViewDrawThread.class.getSimpleName();
	private static final int FONT_SIZE = 60;
	private static final int TOP_SPACE = 0;
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Typeface typeFace = Typeface
			.defaultFromStyle(Typeface.NORMAL);
	private final VTextView vtextView;

	public VTextViewDrawThread(VTextView vtextView) {
		this.vtextView = vtextView;
		paint.setTextSize(FONT_SIZE);
		paint.setColor(Color.BLACK);
		paint.setTypeface(typeFace);
	}

	@Override
	public void run() {
		try {
			while (true) {
				vtextView.updateBitmapStartSemaphore.acquire();
				vtextView.updateBitmapStartSemaphore.drainPermits();
				int localHeight = vtextView.height.get(); // widthとheightは途中で変更される可能性があるため、読み出しておく
				int localWidth = vtextView.width.get();
				if (localHeight <= 0 || localWidth <= 0) {
					continue;
				}
				Bitmap newBitmap = Bitmap.createBitmap(localWidth, localHeight,
						VTextView.BITMAP_CONFIG);
				draw(newBitmap);
				Bitmap oldBitmap = vtextView.preparedBitmap
						.getAndSet(newBitmap);
				if (oldBitmap != null) {
					oldBitmap.recycle();
				}
				vtextView.handler.post(new Runnable() {
					@Override
					public void run() {
						vtextView.invalidate();
					}
				});
			}
		} catch (InterruptedException e) {
		}
	}

	protected void draw(Bitmap targetBitmap) {
		int height = targetBitmap.getHeight();
		int width = targetBitmap.getWidth();
		Bitmap tempBitmap = targetBitmap.copy(VTextView.BITMAP_CONFIG, true);
		Canvas tempCanvas = new Canvas(tempBitmap);
		tempBitmap.eraseColor(Color.WHITE);
		float fontSpacing = paint.getFontSpacing() * FONT_SPACING_RATE;
		float lineSpacing = fontSpacing;
		float x = width - lineSpacing;
		float beginX = x;
		float y = TOP_SPACE + fontSpacing;
		String[] s = vtextView.text.split("");

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
				tempCanvas.drawText(s[i], x, y, paint);
			} else {
				// 文字設定が見つかったので、設定に従い描画
				tempCanvas.save();
				tempCanvas.rotate(setting.angle, x, y);
				tempCanvas.drawText(s[i], x + fontSpacing * setting.x, y
						+ fontSpacing * setting.y, paint);
				tempCanvas.restore();
			}

			boolean cond = false;
			try {
				cond = y + fontSpacing > height - BOTTOM_SPACE;
				// TODO:
				// 上行でなぜかArrayIndexOutOfBoundsException発生することがある。
				// 再現しないようなら削除する
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.e(TAG, e.toString(), e);
				tempBitmap.recycle();
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
		Canvas targetCanvas = new Canvas(targetBitmap);
		targetCanvas.drawBitmap(tempBitmap, x2, 0, paint);
		tempBitmap.recycle();
	}
}
