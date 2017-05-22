package com.comandulli.data.file;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class WriteableData extends Data {

	public WriteableData(String dataPath, List<WriteableFile> files) throws IOException, DataFileException {
		File dataFile = new File(dataPath);
		if (dataFile.exists()) {
			if (!dataFile.delete()) {
				throw new DataFileException("Error cant access path: " + dataPath);
			}
		}
		accessDataFile = new RandomAccessFile(dataFile, "rw");
		int numFiles = files.size();
		long dataStartPtr = indexPositionToKeyFilePosition(numFiles);
		accessDataFile.setLength(dataStartPtr);
		writeNumFilesHeader(numFiles);

		for (int i = 0; i < numFiles; i++) {
			insertFile(files.get(i), i);
		}
		close();
	}

	private synchronized void insertFile(WriteableFile fileEntry, int index) throws DataFileException, IOException {
		String key = fileEntry.getKey();
		FileHeader newEntry = allocateFile(key, fileEntry.getDataLength());
		writeFileData(newEntry, fileEntry);
		addEntryToIndex(key, newEntry, index);
	}

	private synchronized void close() throws IOException {
		try {
			accessDataFile.close();
		} finally {
			accessDataFile = null;
		}
	}

	private void writeNumFilesHeader(int numRecords) throws IOException {
		accessDataFile.seek(HEADER_LOCATION);
		accessDataFile.writeInt(numRecords);
	}

	private FileHeader allocateFile(String key, int dataLength) throws IOException {
		long fp = accessDataFile.length();
		accessDataFile.setLength(fp + dataLength);
		return new FileHeader(fp, dataLength);
	}

	private void writeFileData(FileHeader header, WriteableFile rw) throws IOException, DataFileException {
		if (rw.getDataLength() > header.dataCapacity) {
			throw new DataFileException("File data does not fit");
		}
		header.dataCount = rw.getDataLength();
		accessDataFile.seek(header.dataPointer);
		rw.writeTo((DataOutput) accessDataFile);
	}

	private void addEntryToIndex(String key, FileHeader newRecord, int currentIndex) throws IOException, DataFileException {
		ByteArrayOutputStream temp = new ByteArrayOutputStream(MAX_KEY_LENGTH);
		DataOutputStream dos = new DataOutputStream(temp);
		dos.writeUTF(key);
		dos.close();	
		if (temp.size() > MAX_KEY_LENGTH) {
			throw new DataFileException("Key is larger than permitted size of " + MAX_KEY_LENGTH + " bytes");
		}
		accessDataFile.seek(indexPositionToKeyFilePosition(currentIndex));
		byte[] data = temp.toByteArray();
		accessDataFile.write(data);

		accessDataFile.seek(indexPositionToRecordHeaderFilePosition(currentIndex));
		newRecord.write(accessDataFile);
		newRecord.setIndexPosition(currentIndex);
	}
	
	public static void GetDirectoryFiles(List<WriteableFile> writers, File directory, String parentKey) throws IOException {
		if (directory.isDirectory() && directory.exists()) {
			File[] children = directory.listFiles();
			for (File child : children) {
				if (child.isDirectory()) {
					GetDirectoryFiles(writers, child, parentKey + "\\" + child.getName());
				} else if (child.exists()) {
					WriteableFile rw = new WriteableFile(parentKey + "\\" + child.getName());
					rw.writeFile(child);
					writers.add(rw);
				}
			}
		}
	}

}
