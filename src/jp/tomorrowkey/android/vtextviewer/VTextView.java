package jp.tomorrowkey.android.vtextviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class VTextView extends View {
	private static final int TOP_SPACE = 0;
	private static final int BOTTOM_SPACE = 18;
	private static final int FONT_SIZE = 60;
	private Typeface typeFace = Typeface.defaultFromStyle(Typeface.NORMAL);
	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private String text = "";
	private int width = 0;
	private int height = 0;
	private Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
	private Canvas canvas = new Canvas(bitmap);

	public VTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint.setTextSize(FONT_SIZE);
		paint.setColor(Color.BLACK);
		paint.setTypeface(typeFace);
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		width = getWidth();
		height = getHeight();

		bitmap.recycle();
		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
	}

	@Override
	public void onDraw(Canvas targetCanvas) {
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
}
