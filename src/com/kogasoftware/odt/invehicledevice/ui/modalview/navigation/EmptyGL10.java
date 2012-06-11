package com.kogasoftware.odt.invehicledevice.ui.modalview.navigation;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.util.Log;

public class EmptyGL10 implements GL10 {
	private final String TAG = EmptyGL10.class.getSimpleName();

	@Override
	public void glActiveTexture(int texture) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glAlphaFunc(int func, float ref) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glAlphaFuncx(int func, int ref) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glBindTexture(int target, int texture) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glBlendFunc(int sfactor, int dfactor) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glClear(int mask) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glClearColor(float red, float green, float blue, float alpha) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glClearColorx(int red, int green, int blue, int alpha) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glClearDepthf(float depth) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glClearDepthx(int depth) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glClearStencil(int s) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glClientActiveTexture(int texture) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glColor4f(float red, float green, float blue, float alpha) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glColor4x(int red, int green, int blue, int alpha) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glColorMask(boolean red, boolean green, boolean blue,
			boolean alpha) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glColorPointer(int size, int type, int stride, Buffer pointer) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glCompressedTexImage2D(int target, int level,
			int internalformat, int width, int height, int border,
			int imageSize, Buffer data) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glCompressedTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int imageSize,
			Buffer data) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glCopyTexImage2D(int target, int level, int internalformat,
			int x, int y, int width, int height, int border) {
		Log.w(TAG, "not implemented");
	}

	@Override
	public void glCopyTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int x, int y, int width, int height) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glCullFace(int mode) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDeleteTextures(int n, IntBuffer textures) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDeleteTextures(int n, int[] textures, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDepthFunc(int func) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDepthMask(boolean flag) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDepthRangef(float zNear, float zFar) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDepthRangex(int zNear, int zFar) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDisable(int cap) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDisableClientState(int array) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDrawArrays(int mode, int first, int count) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glDrawElements(int mode, int count, int type, Buffer indices) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glEnable(int cap) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glEnableClientState(int array) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFinish() {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFlush() {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFogf(int pname, float param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFogfv(int pname, FloatBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFogfv(int pname, float[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFogx(int pname, int param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFogxv(int pname, IntBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFogxv(int pname, int[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFrontFace(int mode) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFrustumf(float left, float right, float bottom, float top,
			float zNear, float zFar) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glFrustumx(int left, int right, int bottom, int top, int zNear,
			int zFar) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glGenTextures(int n, IntBuffer textures) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glGenTextures(int n, int[] textures, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public int glGetError() {
		Log.w(TAG, "not implemented");
		return 0;
	}

	@Override
	public void glGetIntegerv(int pname, IntBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glGetIntegerv(int pname, int[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public String glGetString(int name) {
		Log.w(TAG, "not implemented");
		return null;
	}

	@Override
	public void glHint(int target, int mode) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightModelf(int pname, float param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightModelfv(int pname, FloatBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightModelfv(int pname, float[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightModelx(int pname, int param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightModelxv(int pname, IntBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightModelxv(int pname, int[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightf(int light, int pname, float param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightfv(int light, int pname, FloatBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightfv(int light, int pname, float[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightx(int light, int pname, int param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightxv(int light, int pname, IntBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLightxv(int light, int pname, int[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLineWidth(float width) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLineWidthx(int width) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLoadIdentity() {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLoadMatrixf(FloatBuffer m) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLoadMatrixf(float[] m, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLoadMatrixx(IntBuffer m) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLoadMatrixx(int[] m, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glLogicOp(int opcode) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMaterialf(int face, int pname, float param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMaterialfv(int face, int pname, FloatBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMaterialfv(int face, int pname, float[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMaterialx(int face, int pname, int param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMaterialxv(int face, int pname, IntBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMaterialxv(int face, int pname, int[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMatrixMode(int mode) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMultMatrixf(FloatBuffer m) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMultMatrixf(float[] m, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMultMatrixx(IntBuffer m) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMultMatrixx(int[] m, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glMultiTexCoord4x(int target, int s, int t, int r, int q) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glNormal3f(float nx, float ny, float nz) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glNormal3x(int nx, int ny, int nz) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glNormalPointer(int type, int stride, Buffer pointer) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glOrthof(float left, float right, float bottom, float top,
			float zNear, float zFar) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glOrthox(int left, int right, int bottom, int top, int zNear,
			int zFar) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glPixelStorei(int pname, int param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glPointSize(float size) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glPointSizex(int size) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glPolygonOffset(float factor, float units) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glPolygonOffsetx(int factor, int units) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glPopMatrix() {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glPushMatrix() {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glReadPixels(int x, int y, int width, int height, int format,
			int type, Buffer pixels) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glRotatef(float angle, float x, float y, float z) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glRotatex(int angle, int x, int y, int z) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glSampleCoverage(float value, boolean invert) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glSampleCoveragex(int value, boolean invert) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glScalef(float x, float y, float z) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glScalex(int x, int y, int z) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glScissor(int x, int y, int width, int height) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glShadeModel(int mode) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glStencilFunc(int func, int ref, int mask) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glStencilMask(int mask) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glStencilOp(int fail, int zfail, int zpass) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexEnvf(int target, int pname, float param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexEnvfv(int target, int pname, FloatBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexEnvfv(int target, int pname, float[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexEnvx(int target, int pname, int param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexEnvxv(int target, int pname, IntBuffer params) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexEnvxv(int target, int pname, int[] params, int offset) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexImage2D(int target, int level, int internalformat,
			int width, int height, int border, int format, int type,
			Buffer pixels) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexParameterf(int target, int pname, float param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexParameterx(int target, int pname, int param) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			Buffer pixels) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTranslatef(float x, float y, float z) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glTranslatex(int x, int y, int z) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
		Log.w(TAG, "not implemented");

	}

	@Override
	public void glViewport(int x, int y, int width, int height) {
		Log.w(TAG, "not implemented");

	}

}
