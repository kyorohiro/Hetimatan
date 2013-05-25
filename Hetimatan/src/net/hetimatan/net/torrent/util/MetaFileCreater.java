package net.hetimatan.net.torrent.util;


import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import net.hetimatan.ky.io.KyoroFileForFiles;
import net.hetimatan.ky.io.MarkableFileReader;
import net.hetimatan.ky.io.next.RACashFile;
import net.hetimatan.util.bencode.BenDiction;
import net.hetimatan.util.bencode.BenInteger;
import net.hetimatan.util.bencode.BenList;
import net.hetimatan.util.bencode.BenObject;
import net.hetimatan.util.bencode.BenString;

public class MetaFileCreater {
	public static MetaFile createFromTorrentDiction(BenDiction diction) throws IOException {
		// extract basic info
		BenString announce = (BenString) diction.getBenValue(MetaFile.TYPE_ANNOUNCE, BenObject.TYPE_STRI);
		BenDiction info = (BenDiction) diction.getBenValue(MetaFile.TYPE_INFO, BenObject.TYPE_DICT);
		BenInteger pieceLength = (BenInteger) info.getBenValue(MetaFile.TYPE_PIECE_LENGTH, BenObject.TYPE_INTE);
		BenString pieces = (BenString) info.getBenValue(MetaFile.TYPE_PIECES, BenObject.TYPE_STRI);
		BenString name = (BenString) info.getBenValue(MetaFile.TYPE_NAME, BenObject.TYPE_STRI);

		// extract download upload files
		String[] fileNameList = null;
		Long[] fileLengthList = null;

		// when single file
		if (null == info.getBenValue(MetaFile.TYPE_FILES)) {
			BenInteger length = (BenInteger) info.getBenValue(MetaFile.TYPE_LENGTH, BenObject.TYPE_INTE);
			fileNameList = new String[1];
			fileNameList[0] = name.toString();
			fileLengthList = new Long[1];
			fileLengthList[0] = (long) length.toInteger();
		}

		// when multi file
		else {
			BenList files = (BenList) info.getBenValue(MetaFile.TYPE_FILES, BenObject.TYPE_LIST);
			LinkedList<String> temporaryFileNameList = new LinkedList<String>();
			LinkedList<Long> temporaryFileLengthList = new LinkedList<Long>();
			MetaFile.extractFileList(name.toString(), temporaryFileNameList, temporaryFileLengthList, files);
			fileNameList = new String[temporaryFileNameList.size()];
			fileLengthList = new Long[temporaryFileLengthList.size()];
			temporaryFileNameList.toArray(fileNameList);
			temporaryFileLengthList.toArray(fileLengthList);
		}
		//
		return new MetaFile(diction, announce.toString(), name.toString(),
				pieces, pieceLength.toInteger(), fileNameList, fileLengthList);
	}

	public static MetaFile createFromTorrentFile(File file) throws IOException {
		MarkableFileReader reader = null;
		try {
			RACashFile vfile = new RACashFile(file, 512, 2);// read only
			reader = new MarkableFileReader(vfile, 512);
			BenDiction dection = BenDiction.decodeDiction(reader);
			return createFromTorrentDiction(dection);
		} finally {
			if (reader != null) {
				reader.close();}
		}
	}

	//
	// support single file only
	public static MetaFile createFromTargetFile(File targetFile, String address) throws IOException {
		if (targetFile.isDirectory() || !targetFile.exists()) {
			throw new IOException();
		}
		BenDiction root = new BenDiction();
		BenDiction info = new BenDiction();
		BenString pieces = null;
		root.append(MetaFile.TYPE_ANNOUNCE, new BenString(address));
		root.append(MetaFile.TYPE_INFO, info);
		info.append(MetaFile.TYPE_LENGTH, new BenInteger((int) targetFile.length()));
		info.append(MetaFile.TYPE_NAME, new BenString(targetFile.getName()));
		info.append(MetaFile.TYPE_PIECE_LENGTH, new BenInteger(MetaFile.DEFAULT_PIECE_LENGTH));
		info.append(MetaFile.TYPE_PIECES, pieces);

		MarkableFileReader reader = null;
		try {
			reader = new MarkableFileReader(targetFile, 512);
			info.append(MetaFile.TYPE_PIECES, MetaFile.createPieces(reader));
			return createFromTorrentDiction(root);
		} finally {
			if (reader != null) {
				reader.close();}
		}
	}

	//
	// multi file 
	public static MetaFile createFromTargetDir(File targetDir, String address) throws IOException {
		if (!targetDir.isDirectory() || !targetDir.exists()) {
			throw new IOException();
		}
		BenDiction root = new BenDiction();
		BenDiction info = new BenDiction();
		BenString pieces = null;
		root.append(MetaFile.TYPE_ANNOUNCE, new BenString(address));
		root.append(MetaFile.TYPE_INFO, info);

		//
		info.append(MetaFile.TYPE_LENGTH, new BenInteger((int) targetDir.length()));
		info.append(MetaFile.TYPE_NAME, new BenString(targetDir.getName()));

		
		LinkedList<File> findFiles = MetaFile.findFile(targetDir);
		BenList files = new BenList(); 
		for (File f : findFiles) {
//			System.out.println("FF#"+f.getName());
			BenDiction file = new BenDiction();
			file.append(MetaFile.TYPE_LENGTH, new BenInteger((int)f.length()));
			file.append(MetaFile.TYPE_PATH, MetaFile._filePath(targetDir, f));
			files.append(file);
		}
		info.append(MetaFile.TYPE_FILES, files);

		//
		info.append(MetaFile.TYPE_PIECE_LENGTH, new BenInteger(MetaFile.DEFAULT_PIECE_LENGTH));
		info.append(MetaFile.TYPE_PIECES, pieces);

		MarkableFileReader reader = null;
		try {
			reader = new MarkableFileReader(KyoroFileForFiles.create(findFiles), 512);
			info.append(MetaFile.TYPE_PIECES, MetaFile.createPieces(reader));
			return createFromTorrentDiction(root);
		} finally {
			if (reader != null) {
				reader.close();}
		}
	}
}
