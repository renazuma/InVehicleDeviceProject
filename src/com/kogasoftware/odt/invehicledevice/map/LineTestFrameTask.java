package com.kogasoftware.odt.invehicledevice.map;

import javax.microedition.khronos.opengles.GL10;


public class LineTestFrameTask extends FrameTask {

	@Override
	void onDraw(FrameState frameState) {
		GL10 gl = frameState.getGL();
		int x = 0;
		int y = 0;
		int width = 300;
		int height = 4;
		int red = 0;
		int green = 0;
		int blue = 255;
		int alpha = 255;

		// 固定小数点値で1.0
		int one = 0x10000;

		// 頂点座標
		// int vertices[] = { -width * one / 2, -height * one / 2, 0,
		// width * one / 2, -height * one / 2, 0, -width * one / 2,
		// height * one / 2, 0, width * one / 2, height * one / 2, 0, };

		int x1 = 100 * one;
		int y1 = 1000 * one;
		int x2 = 300 * one;
		int y2 = 200 * one;
		// 頂点座標
		int vertices[] = { x1 + one * 10, y1 + one * 10, 0, //
				x1 - one * 10, y1 - one * 10, 0, //
				x2 + one * 10, y2 + one * 10, 0, //
				x2 - one * 10, y2 - one * 10, 0, };

		// 頂点カラー
		int colors[] = { one * red / 255, one * green / 255, one * blue / 255,
				one * alpha / 255, one * red / 255, one * green / 255,
				one * blue / 255, one * alpha / 255, one * red / 255,
				one * green / 255, one * blue / 255, one * alpha / 255,
				one * red / 255, one * green / 255, one * blue / 255,
				one * alpha / 255, };

		// 頂点配列を使うことを宣言
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// 色情報配列を使うことを宣言
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		// 2Dテクスチャを無効に
		gl.glDisable(GL10.GL_TEXTURE_2D);

		// モデルビュー行列を選択
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// 現在選択されている行列(モデルビュー行列)に、単位行列をセット
		gl.glLoadIdentity();

		// 行列スタックに現在の行列をプッシュ
		gl.glPushMatrix();

		// モデルを平行移動する行列を掛け合わせる
		gl.glTranslatef(x, y, 0);

		// 頂点座標配列をセット
		gl.glVertexPointer(3, GL10.GL_FIXED, 0,
				Texture.wrapNativeIntBuffer(vertices));

		// 色情報配列をセット
		gl.glColorPointer(4, GL10.GL_FIXED, 0,
				Texture.wrapNativeIntBuffer(colors));

		// セットした配列を元に描画
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		// さきほどプッシュした状態に行列スタックを戻す
		gl.glPopMatrix();

		// 有効にしたものを無効化
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

	}
}
