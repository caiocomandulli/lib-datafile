# Data File for Java - File System in a File.

Data File integrates a File System, containing multiple files and folder structure inside a single file.
This is particularly useful for generating .obb files for Android or bundling all assets in a single easily updateable file.

## Usage

### Writing Data

You create a Data File by submitting a List of `WriteableFile` to a new instance of `WriteableData`

```java
WriteableFile rw = new WriteableFile(key);
rw.writeFile(file);
```

Instantiating a `WriteableFile`, the key submitted to the constructor defines its path within the file system.
The file handle is submitted via the method `writeFile(File)`.

````java
new WriteableData("data.file", writeableFileList);
````

By instantiating a `WriteableData` the data file is created. The path and filename is given as the first parameter and the WriteableFile list as second parameter.

````java
GetDirectoryFiles(writeableFileList, directory, "")
````

You can use the static method `GetDirectoryFiles(List<WriteableFile>, File, String)` as a helper for acquiring all files in a folder as `WriteableFile`.
First parameter is the list for all entries to be appended. Second is the File handle to the directory. Third is the string path to be added to the beggining of all file paths.

### Reading Data

By instatiating `ReadableData`, all entries in a Data File are mapped internally, you can call `getNames()` to retrieve available entries and `readFile(String)` to read each individual entry.

```java
ReadableData readableData = new ReadableData("data.bdf");
List<String> keys = readableData.getNames();
for (String key : keys) {
	ReadableFile readFile = readableData.readFile(key);
	File newFile = new File(key);
	newFile.mkdirs();
	newFile.delete();
	
	FileOutputStream fos = new FileOutputStream(newFile);
	fos.write(readFile.getData());
	fos.close();
}
readableData.close();
```

Example of reading data from a Data File and recreating the files.

### Streaming Data

```java
MediaPlayer player = new MediaPlayer();
ReadableData readableData = new ReadableData(filePath);
FileHeader fileHeader = readableData.getFileHeader(filename);
if (fileHeader != null) {
	fileHeader.getDataCapacity();
	long dataPosition = fileHeader.getDataPosition();
	long dataLength = fileHeader.getDataLength();

	File file = new File(filePath);
	FileInputStream inputStream = new FileInputStream(file);
	player.setDataSource(inputStream.getFD(), dataPosition, dataLength);
	inputStream.close();
}
````

Example of streaming data from file, without any need to copy data. Here we use `getFileHeader(String)` to get information about the position of our video file within the data file.
We use that to pass data to Android`s MediaPlayer, indicating where to stream video from.

## Install Library

__Step 1.__ Get this code and compile it

##  License

MIT License. See the file LICENSE.md with the full license text.

## Author

[![Caio Comandulli](https://avatars3.githubusercontent.com/u/3738961?v=3&s=150)](https://github.com/caiocomandulli "On Github")

Copyright (c) 2016 Caio Comandulli
