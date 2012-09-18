package com.kogasoftware.odt.invehicledevice.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.test.mock.MockContext;

public class DelegateMockContext extends MockContext {
	private final Context context;

	public DelegateMockContext(Context context) {
		this.context = context;
	}

	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		return context.bindService(service, conn, flags);
	}

	@Override
	public int checkCallingOrSelfPermission(String permission) {
		return context.checkCallingOrSelfPermission(permission);
	}

	@Override
	public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
		return context.checkCallingOrSelfUriPermission(uri, modeFlags);
	}

	@Override
	public int checkCallingPermission(String permission) {
		return context.checkCallingPermission(permission);
	}

	@Override
	public int checkCallingUriPermission(Uri uri, int modeFlags) {
		return context.checkCallingUriPermission(uri, modeFlags);
	}

	@Override
	public int checkPermission(String permission, int pid, int uid) {
		return context.checkPermission(permission, pid, uid);
	}

	@Override
	public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
		return context.checkUriPermission(uri, pid, uid, modeFlags);
	}

	@Override
	public int checkUriPermission(Uri uri, String readPermission,
			String writePermission, int pid, int uid, int modeFlags) {
		return context.checkUriPermission(uri, readPermission, writePermission,
				pid, uid, modeFlags);
	}

	@Override
	public void clearWallpaper() {
		try {
			context.clearWallpaper();
		} catch (IOException e) {
			throw new UnsupportedOperationException(e);
		}
	}

	@Override
	public Context createPackageContext(String packageName, int flags)
			throws NameNotFoundException {
		return context.createPackageContext(packageName, flags);
	}

	@Override
	public String[] databaseList() {
		return context.databaseList();
	}

	@Override
	public boolean deleteDatabase(String name) {
		return context.deleteDatabase(name);
	}

	@Override
	public boolean deleteFile(String name) {
		return context.deleteFile(name);
	}

	@Override
	public void enforceCallingOrSelfPermission(String permission, String message) {
		context.enforceCallingOrSelfPermission(permission, message);
	}

	@Override
	public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags,
			String message) {
		context.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
	}

	@Override
	public void enforceCallingPermission(String permission, String message) {
		context.enforceCallingPermission(permission, message);
	}

	@Override
	public void enforceCallingUriPermission(Uri uri, int modeFlags,
			String message) {
		context.enforceCallingUriPermission(uri, modeFlags, message);
	}

	@Override
	public void enforcePermission(String permission, int pid, int uid,
			String message) {
		context.enforcePermission(permission, pid, uid, message);
	}

	@Override
	public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags,
			String message) {
		context.enforceUriPermission(uri, pid, uid, modeFlags, message);
	}

	@Override
	public void enforceUriPermission(Uri uri, String readPermission,
			String writePermission, int pid, int uid, int modeFlags,
			String message) {
		context.enforceUriPermission(uri, readPermission, writePermission, pid,
				uid, modeFlags, message);
	}

	@Override
	public String[] fileList() {
		return context.fileList();
	}

	@Override
	public Context getApplicationContext() {
		return context.getApplicationContext();
	}

	@Override
	public ApplicationInfo getApplicationInfo() {
		return context.getApplicationInfo();
	}

	@Override
	public AssetManager getAssets() {
		return context.getAssets();
	}

	@Override
	public File getCacheDir() {
		return context.getCacheDir();
	}

	@Override
	public ClassLoader getClassLoader() {
		return context.getClassLoader();
	}

	@Override
	public ContentResolver getContentResolver() {
		return context.getContentResolver();
	}

	@Override
	public File getDatabasePath(String name) {
		return context.getDatabasePath(name);
	}

	@Override
	public File getDir(String name, int mode) {
		return context.getDir(name, mode);
	}

	@Override
	public File getExternalCacheDir() {
		return context.getExternalCacheDir();
	}

	@Override
	public File getExternalFilesDir(String type) {
		return context.getExternalFilesDir(type);
	}

	@Override
	public File getFileStreamPath(String name) {
		return context.getFileStreamPath(name);
	}

	@Override
	public File getFilesDir() {
		return context.getFilesDir();
	}

	@Override
	public Looper getMainLooper() {
		return context.getMainLooper();
	}

	@Override
	public String getPackageCodePath() {
		return context.getPackageCodePath();
	}

	@Override
	public PackageManager getPackageManager() {
		return context.getPackageManager();
	}

	@Override
	public String getPackageName() {
		return context.getPackageName();
	}

	@Override
	public String getPackageResourcePath() {
		return context.getPackageResourcePath();
	}

	@Override
	public Resources getResources() {
		return context.getResources();
	}

	@Override
	public SharedPreferences getSharedPreferences(String name, int mode) {
		return context.getSharedPreferences(name, mode);
	}

	@Override
	public Object getSystemService(String name) {
		return context.getSystemService(name);
	}

	@Override
	public Theme getTheme() {
		return context.getTheme();
	}

	@Override
	public Drawable getWallpaper() {
		return context.getWallpaper();
	}

	@Override
	public int getWallpaperDesiredMinimumHeight() {
		return context.getWallpaperDesiredMinimumHeight();
	}

	@Override
	public int getWallpaperDesiredMinimumWidth() {
		return context.getWallpaperDesiredMinimumWidth();
	}

	@Override
	public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
		context.grantUriPermission(toPackage, uri, modeFlags);
	}

	@Override
	public boolean isRestricted() {
		return context.isRestricted();
	}

	@Override
	public FileInputStream openFileInput(String name)
			throws FileNotFoundException {
		return context.openFileInput(name);
	}

	@Override
	public FileOutputStream openFileOutput(String name, int mode)
			throws FileNotFoundException {
		return context.openFileOutput(name, mode);
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String file, int mode,
			CursorFactory factory) {
		return context.openOrCreateDatabase(file, mode, factory);
	}

	@Override
	public Drawable peekWallpaper() {
		return context.peekWallpaper();
	}

	@Override
	public Intent registerReceiver(BroadcastReceiver receiver,
			IntentFilter filter, String broadcastPermission, Handler scheduler) {
		return context.registerReceiver(receiver, filter, broadcastPermission,
				scheduler);
	}

	@Override
	public Intent registerReceiver(BroadcastReceiver receiver,
			IntentFilter filter) {
		return context.registerReceiver(receiver, filter);
	}

	@Override
	public void removeStickyBroadcast(Intent intent) {
		context.removeStickyBroadcast(intent);
	}

	@Override
	public void revokeUriPermission(Uri uri, int modeFlags) {
		context.revokeUriPermission(uri, modeFlags);
	}

	@Override
	public void sendBroadcast(Intent intent, String receiverPermission) {
		context.sendBroadcast(intent, receiverPermission);
	}

	@Override
	public void sendBroadcast(Intent intent) {
		context.sendBroadcast(intent);
	}

	@Override
	public void sendOrderedBroadcast(Intent intent, String receiverPermission,
			BroadcastReceiver resultReceiver, Handler scheduler,
			int initialCode, String initialData, Bundle initialExtras) {
		context.sendOrderedBroadcast(intent, receiverPermission,
				resultReceiver, scheduler, initialCode, initialData,
				initialExtras);
	}

	@Override
	public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
		context.sendOrderedBroadcast(intent, receiverPermission);
	}

	@Override
	public void sendStickyBroadcast(Intent intent) {
		context.sendStickyBroadcast(intent);
	}

	@Override
	public void sendStickyOrderedBroadcast(Intent intent,
			BroadcastReceiver resultReceiver, Handler scheduler,
			int initialCode, String initialData, Bundle initialExtras) {
		context.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler,
				initialCode, initialData, initialExtras);
	}

	@Override
	public void setTheme(int resid) {
		context.setTheme(resid);
	}

	@Override
	public void setWallpaper(Bitmap bitmap) throws IOException {
		context.setWallpaper(bitmap);
	}

	@Override
	public void setWallpaper(InputStream data) throws IOException {
		context.setWallpaper(data);
	}

	@Override
	public void startActivity(Intent intent) {
		context.startActivity(intent);
	}

	@Override
	public boolean startInstrumentation(ComponentName className,
			String profileFile, Bundle arguments) {
		return context.startInstrumentation(className, profileFile, arguments);
	}

	@Override
	public void startIntentSender(IntentSender intent, Intent fillInIntent,
			int flagsMask, int flagsValues, int extraFlags)
			throws SendIntentException {
		context.startIntentSender(intent, fillInIntent, flagsMask, flagsValues,
				extraFlags);
	}

	@Override
	public ComponentName startService(Intent service) {
		return context.startService(service);
	}

	@Override
	public boolean stopService(Intent service) {
		return context.stopService(service);
	}

	@Override
	public void unbindService(ServiceConnection conn) {
		context.unbindService(conn);
	}

	@Override
	public void unregisterReceiver(BroadcastReceiver receiver) {
		context.unregisterReceiver(receiver);
	}
}
