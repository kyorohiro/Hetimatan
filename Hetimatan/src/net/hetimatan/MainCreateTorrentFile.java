package net.hetimatan;


import java.io.File;
import java.io.IOException;

import net.hetimatan.ky.io.next.RACashFile;
import net.hetimatan.net.torrent.util.MetaFile;
import net.hetimatan.net.torrent.util.MetaFileCreater;


/**
 * 80% complemte. 
 * Create Metafile(*.torrent file) 
 */
public class MainCreateTorrentFile {

	public static void main(String[] args) {
		if(args.length != 2) {
			showHelp();
			return;
		}
		String address = args[0];

		File targetFile = new File(args[1]);
		if(!targetFile.exists()) {
			showFileDoNotExists(targetFile);
			showHelp();
			return;
		}

		try {
			MetaFile metaFile = null;
			if(targetFile.isDirectory()) {
				metaFile = MetaFileCreater.createFromTargetDir(targetFile, address);
			} else {
				metaFile = MetaFileCreater.createFromTargetFile(targetFile, address);
			}
			File outputFile = new File("./a.torrent");
			if(outputFile.exists()) {
				if(!outputFile.delete()) {
					System.out.print(" confuse output file ("+outputFile.getName()+")");
					return;
				}
			}
			RACashFile output = new RACashFile(new File("./a.torrent"), 1024, 2);
			try {
				metaFile.save(output);
				output.syncWrite();
			} finally {
				output.close();
			}
		} catch (IOException e) {
			showTorrentFileCreateIsFailed();
			showHelp();
			e.printStackTrace();
		}
	}

	public static void showTorrentFileCreateIsFailed() {
		StringBuilder message = new StringBuilder();
		message.append("[Error] failed to create a torrent file.\r\n");
		message.append("\r\n");
		message.append("#--\r\n");
		System.out.println(message.toString());
	}

	public static void showFileDoNotExists(File file) {
		StringBuilder message = new StringBuilder();
		message.append("[Error] file is not found. " + file.getPath() + "\r\n");
		message.append("\r\n");
		message.append("#--\r\n");
		System.out.println(message.toString());
	}

	public static void showHelp() {
		StringBuilder message = new StringBuilder();
		message.append("Usage: java " + MainCreateTorrentFile.class.getName() + " [TRACKER_ADDRESS]... [FILE]...\r\n");
		message.append("TRACKER_ADDRESS is Tracker site address ..http://kyorohiro.info/raider/torrent/announce:6969\r\n");
		message.append("FILE is a Download Data from TorrentClient\r\n");
		message.append("\r\n");
		message.append("Sample: ");
		message.append("java " + MainCreateTorrentFile.class.getName() + " http://example.com/announce:6969 ./test.txt " + "\r\n");
		System.out.println(message.toString());
	}

}
