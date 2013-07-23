package com.kogasoftware.odt.invehicledevice.empty;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;

public class EmptyFile extends File {
	private static final long serialVersionUID = -2496664019244109325L;

	private static String getNullFilePath() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return "nul";
		} else {
			return "/dev/null";
		}
	}

	public EmptyFile() {
		super(getNullFilePath());
	}

	/**
	 * 何もしない
	 */
	@Override
	public boolean delete() {
		return false;
	}

	/**
	 * 何もしない
	 */
	@Override
	public void deleteOnExit() {
	}

	/**
	 * 何もしない
	 */
	@Override
	public boolean renameTo(File newPath) {
		return false;
	}
}