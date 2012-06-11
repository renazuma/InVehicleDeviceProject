package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLUtils;

import com.google.common.math.DoubleMath;

public class Texture {
	// 固定小数点値で1.0
	private static final int ONE = 0x10000;
	// テクスチャ座標配列
	private static final IntBuffer DEFAULT_TEX_COORDS = wrapNativeIntBuffer(new int[] {
			0, ONE, ONE, ONE, 0, 0, ONE, 0 });

	/**
	 * 2Dテクスチャを描画する
	 * 
	 * @param gl
	 * @param textureId
	 *            テクスチャID
	 * @param x
	 *            , y 描画する座標
	 * @param width
	 *            , height 四角形の幅・高さ
	 * @param angle
	 *            回転角度
	 * @param scaleX
	 *            , scale_y 拡大率
	 */
	public static void draw(GL10 gl, int textureId, float x, float y,
			int width, int height, float angle, float scaleX, float scaleY,
			float alpha) {

		// 頂点座標
		int vertices[] = { -width * ONE / 2, //
				-height * ONE / 2, //
				0, //
				width * ONE / 2, //
				-height * ONE / 2, //
				0, //
				-width * ONE / 2, //
				height * ONE / 2, //
				0, //
				width * ONE / 2, //
				height * ONE / 2, //
				0, //
		};

		// 頂点配列を使うことを宣言
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		// テクスチャ座標配列を使うことを宣言
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		// ブレンディングを有効化
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// テクスチャの透明度の合成を有効にする
		gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_MODULATE);

		// 2Dテクスチャを有効に
		gl.glEnable(GL10.GL_TEXTURE_2D);
		// テクスチャユニット0番をアクティブに
		gl.glActiveTexture(GL10.GL_TEXTURE0);
		// テクスチャIDに対応するテクスチャをバインド
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		// モデルビュー行列を選択
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		// 行列スタックに現在の行列をプッシュ
		gl.glPushMatrix();
		// 現在選択されている行列(モデルビュー行列)に、単位行列をセット
		gl.glLoadIdentity();
		// モデルを平行移動する行列を掛け合わせる
		gl.glTranslatef(x, y, 0);
		// モデルをX軸中心に回転する行列を掛け合わせる
		// gl.glRotatef(60.0f, 1.0f, 0.0f, 0.0f);
		// モデルをZ軸中心に回転する行列を掛け合わせる
		gl.glRotatef((float) Math.toDegrees(angle), 0.0f, 0.0f, 1.0f);
		// モデルを拡大縮小する行列を掛け合わせる
		gl.glScalef(scaleX, scaleY, 1.0f);
		// 色をセット
		gl.glColor4x(0x10000, 0x10000, 0x10000, (int) (0x10000 * alpha));
		// 頂点座標配列をセット
		gl.glVertexPointer(3, GL10.GL_FIXED, 0, wrapNativeIntBuffer(vertices));
		// テクスチャ情報をセット
		gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, DEFAULT_TEX_COORDS);
		// セットした配列を元に描画
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		// さきほどプッシュした状態に行列スタックを戻す
		gl.glPopMatrix();

		// 有効にしたものを無効化
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

	/**
	 * テクスチャを読み込む
	 * 
	 * @param gl
	 * @return 生成したテクスチャのIDを返す
	 */
	public static int generate(GL10 gl) {
		int[] textures = new int[1];
		// テクスチャを作成するための固有IDを1つ作成
		gl.glGenTextures(1, textures, 0);
		return textures[0];
	}

	/**
	 * テクスチャを削除する
	 */
	public static void delete(GL10 gl, int textureId) {
		gl.glDeleteTextures(1, new int[] { textureId }, 0);
	}

	/**
	 * テクスチャを張り替える
	 */
	public static void update(GL10 gl, int textureId, Bitmap bitmap) {
		Bitmap targetBitmap = bitmap;
		boolean recycle = false;

		int alignedLength = (int) Math.pow(
				2,
				Math.ceil(DoubleMath.log2(Math.max(bitmap.getWidth(),
						bitmap.getHeight()))));
		if (bitmap.getWidth() != alignedLength
				|| bitmap.getHeight() != alignedLength) {
			Bitmap alignedBitmap = Bitmap.createBitmap(alignedLength,
					alignedLength, Bitmap.Config.ARGB_8888);
			Float left = (float) (alignedLength - bitmap.getWidth()) / 2;
			Float top = (float) (alignedLength - bitmap.getHeight()) / 2;
			new Canvas(alignedBitmap)
					.drawBitmap(bitmap, left, top, new Paint());
			targetBitmap = alignedBitmap;
			recycle = true;
		}

		// 指定した固有名を持つテクスチャを作成
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		// バインドされているテクスチャに、テクスチャの拡大・縮小方法を指定。
		// 縮小処理には高速化のためにニアレストネイバー法を用いる
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_NEAREST);
		// 拡大は線形補完とする。重いようならニアレストネイバー法(GL_NEAREST)へ変更すること
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		// バインドされているテクスチャに、繰り返し方法を指定
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_CLAMP_TO_EDGE);

		// バインドされているテクスチャに、テクスチャの色が下地の色を置き換えるよう指定(GL_REPLACE)
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
				GL10.GL_REPLACE);

		// ビットマップからテクスチャを作成する
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, targetBitmap, 0);

		if (recycle) {
			targetBitmap.recycle();
		}
	}

	private static IntBuffer wrapNativeIntBuffer(int vertices[]) {
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(vertices);
		intBuffer.position(0);
		return intBuffer;
	}
}
