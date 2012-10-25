package com.kogasoftware.odt.invehicledevice.apiclient.model.base.externalizable;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;

/**
 * ObjectInput wrapper
 * 
 * @see "http://www.cowtowncoder.com/blog/archives/2012/08/entry_477.html"
 */
public final class ExternalizableInput extends InputStream {
	private final ObjectInput in;

	public ExternalizableInput(ObjectInput in) {
		this.in = in;
	}

	@Override
	public int available() throws IOException {
		return in.available();
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] buffer) throws IOException {
		return in.read(buffer);
	}

	@Override
	public int read(byte[] buffer, int offset, int len) throws IOException {
		return in.read(buffer, offset, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return in.skip(n);
	}
}
