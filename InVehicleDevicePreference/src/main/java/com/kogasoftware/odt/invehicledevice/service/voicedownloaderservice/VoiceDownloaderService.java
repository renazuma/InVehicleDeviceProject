package com.kogasoftware.odt.invehicledevice.service.voicedownloaderservice;

import com.google.android.vending.expansion.downloader.impl.DownloaderService;

public class VoiceDownloaderService extends DownloaderService {
	private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQE"
			+ "AiMMUe8MimZLACpdJ4SCFNHj29PUBMIut2PJv+6jOY968IRPSTy0gjmiyoT4w41VF+OF+6OtEGf6KMVI"
			+ "xj/WkQ+VyVSrc/NHM9Mo2y5tqv0ZeOK3km2Mzck6JJFS+uFOCbdseTx8cYu3l8Wx6+axR8AU9zBDdmI4"
			+ "35E0bfKDqRZ1DDJWTfwBjxg91fuTpuHW8xeDoytJ7dWjBsCzeq+e7QX7B2KRb3Qsq5uikjAKWijwP9D2"
			+ "qIXxwN5Bs2rD7dcypY00RGiHtdJky1F3xulUFaRLzkgLry3mzQEpFHOOi3DvVCXQu6oNT5Ifp9wbDcz8"
			+ "fbUQ3nj2z1PtsnESNbinn2wIDAQAB";

	private static final byte[] SALT = new byte[] { -112, 127, -49, -50, 4, 13,
			44, 19, 24, -20, 36, -99, -94, -86, -14, -56, -65, 43, 1, 5, -94,
			12, -61, -55, -105, 104, 99, -84, -81, -78, 39, -69, 93, -110, -74,
			-29, 39, 12, 102, -27, -51, 15, 64, 18, -13, 86, 28, -48, -1, -72,
			52, 127, -62, -119, -90, 122, -47, 123, 117, 17, -75, -95, -119,
			-82, -50, 83, -115, -61, 101, -95, -33, -60, 18, -10, 69, 81, -89,
			-122, -20 - 63, -54, -11, -16, -10, 107, -63, 15, -66, 6, -99, -7,
			80, -128, 100, -62, -91, -108, 55, -128, -4, 39, 119, 93, 9, };

	@Override
	public String getPublicKey() {
		return BASE64_PUBLIC_KEY;
	}

	@Override
	public byte[] getSALT() {
		return SALT;
	}

	@Override
	public String getAlarmReceiverClassName() {
		return VoiceDownloadAlarmReceiver.class.getName();
	}
}
