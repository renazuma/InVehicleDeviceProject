package com.kogasoftware.odt.invehicledevice.test.unit.backgroundtask;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;

import com.kogasoftware.odt.invehicledevice.service.invehicledeviceservice.SharedPreferencesKey;
import com.kogasoftware.odt.invehicledevice.service.voiceservice.VoiceCache;
import com.kogasoftware.odt.invehicledevice.test.util.EmptyActivityInstrumentationTestCase2;

public class VoiceCacheTestCase extends EmptyActivityInstrumentationTestCase2 {
	AtomicBoolean once = new AtomicBoolean(true);
	int bytes = -1;

	protected void assertMediaFile(File wavFile) throws Exception,
			IllegalStateException, IOException {
		assertTrue(wavFile.length() > 0);
		MediaPlayer mp = new MediaPlayer();
		mp.setDataSource(wavFile.getAbsolutePath());
		mp.prepare();
		mp.start();
		Thread.sleep(500);
		mp.stop();
	}

	public void callTestGet_キャッシュ境界x2(double b, int win, String s1, String s2)
			throws Exception {
		VoiceCache vc = new VoiceCache(getInstrumentation().getTargetContext(),
				(int) b);

		File f1 = vc.get(s1);
		assertTrue(f1.exists());
		assertMediaFile(f1);

		File f2 = vc.get(s2);

		if (win == 0) {
			assertTrue(f1.exists() ^ f2.exists()); // どちらか一方が存在
			if (f1.exists()) {
				win = 1;
			} else {
				win = 2;
			}
		}

		switch (win) {
		case 1:
			assertMediaFile(f1);
			File f1b = vc.get(s1);
			assertEquals(f1, f1b);
			break;
		case 2:
			assertMediaFile(f2);
			File f2b = vc.get(s2);
			assertEquals(f2, f2b);
			break;
		}
	}

	public void callTestGet_キャッシュ境界x3(double b, int win, String s1, String s2,
			String s3) {
	}

	protected void callTestGet_キャッシュ不可能(int bytes) throws Exception {
		VoiceCache vc = new VoiceCache(getInstrumentation().getTargetContext(),
				bytes);
		File f1 = vc.get("こんにちは");
		assertFalse(f1.exists());

		File f2 = vc.get("こんばんは");
		assertFalse(f2.exists());
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		if (once.getAndSet(false)) {
			Context c = getInstrumentation().getTargetContext();
			VoiceCache vc = new VoiceCache(c, Integer.MAX_VALUE);
			File f = vc.get("こんにちは");
			bytes = (int) f.length();
			PreferenceManager
					.getDefaultSharedPreferences(c)
					.edit()
					.putBoolean(
							SharedPreferencesKey.CLEAR_VOICE_CACHE,
							true).commit();
			new VoiceCache(c, Integer.MAX_VALUE);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGet_キャッシュ不可能() throws Exception {
		callTestGet_キャッシュ不可能(0);
		callTestGet_キャッシュ不可能(1);
		callTestGet_キャッシュ不可能((int) (bytes * 0.7));
	}

	public void xtestGet_キャッシュ境界x2() throws Exception {
		callTestGet_キャッシュ境界x2(bytes * 1.5, 0, "あいうえお", "かきくけこ");
		callTestGet_キャッシュ境界x2(bytes, 0, "あい", "くけこ");
		callTestGet_キャッシュ境界x2(bytes * 2.0, 0, "にんじんたまねぎ", "だいこんじゃがいも");

		callTestGet_キャッシュ境界x2(bytes * 1.5, 1, "あいうえお", "かきくけこさしすせそ");
		callTestGet_キャッシュ境界x2(bytes, 1, "イフ", "ステイトメント");

		callTestGet_キャッシュ境界x2(bytes, 2, "ねぎ", "だいこんじゃがいも");
		callTestGet_キャッシュ境界x2(bytes * 1.2, 2, "にんじん", "ハンバーガーデミグラスソース");
	}
}
