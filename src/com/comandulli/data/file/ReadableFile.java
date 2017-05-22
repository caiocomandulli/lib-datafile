package com.comandulli.data.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ReadableFile {
	private String key;
	private byte[] data;
	private ByteArrayInputStream in;

	public ReadableFile(String key, byte[] data) {
		this.key = key;
		this.data = data;
		in = new ByteArrayInputStream(data);
	}

	public String getKey() {
		return key;
	}

	public byte[] getData() {
		return data;
	}

	public InputStream getInputStream() throws IOException {
		return in;
	}
}
