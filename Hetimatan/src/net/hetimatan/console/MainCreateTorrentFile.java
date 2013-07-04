package net.hetimatan.console;


import java.io.File;
import java.io.IOException;

import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;


/**
 * 80% complemte. 
 * Create Metafile(*.torrent file) 
 */
public class MainCreateTorrentFile {

	public static void main(String[] args) {
		if(args.length < 1) {
			showHelp();
			return;
		}
		Builder b = new Builder(args);
		String address = b.address;
		File targetFile = new File(b.input);
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
			File outputFile = new File(b.output);
			if(outputFile.exists()) {
				if(!outputFile.delete()) {
					System.out.print(" confuse output file ("+outputFile.getName()+")");
					return;
				}
			}
			RACashFile output = new RACashFile(outputFile, 1024, 2);
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
		message.append("Usage: java " + MainCreateTorrentFile.class.getName() + " -a [TRACKER_ADDRESS] -o [OUTPUT]... [FILE]...\r\n");
		message.append("TRACKER_ADDRESS is Tracker site address ..http://kyorohiro.info/raider/torrent/announce:6969\r\n");
		message.append("FILE is a Download Data from TorrentClient\r\n");
		message.append("\r\n");
		message.append("Sample: ");
		message.append("java " + MainCreateTorrentFile.class.getName() + " http://example.com/announce:6969 ./test.txt " + "\r\n");
		System.out.println(message.toString());
	}

	static class Builder {
		String address = "http://127.0.0.1";
		String input   = "input";
		String output  = "a.torrent";

		public Builder(String[] args) {
			for(int i=0;i<args.length;i++) {
				String v = args[i];
				if(v.equals("-o")&&i+1<args.length) {
					output = args[i+1];
				} 
				else if(v.equals("-a")&&i+1<args.length) {
					address = args[i+1];
				} 
				else if(v.equals("-i")&&i+1<args.length) {
					input = args[i+1];
				}
				else {
					input = v;					
				}
			}
		}
	}
}
