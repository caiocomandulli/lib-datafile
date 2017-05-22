package com.comandulli.data.file;

import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WriteableFile {
	private String key;
	private File input;

	public WriteableFile(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void writeFile(File file) throws IOException {
		input = file;
	}

	public int getDataLength() {
		return (int) input.length();
	}

	public void writeTo(DataOutput str) throws IOException {
		FileInputStream fin = new FileInputStream(input);
		byte[] data = new byte[getDataLength()];
		fin.read(data);
		str.write(data);
		fin.close();
	}
}
