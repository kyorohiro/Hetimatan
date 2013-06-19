package net.hetimatan.console;



import java.io.File;
import java.io.IOException;

import net.hetimatan.net.torrent.util.bencode.BenString;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;

/**
 * 30% complete
 * Read Metafile(*.torrent file) 
 */
public class MainReadTorrentFile {

	public static void main(String[] args) {
		if (args.length != 1) {
			showHelp();
			return;
		}
		File torrentFile = new File(args[0]);
		if (!torrentFile.exists()) {
			showFileDoNotExists(torrentFile);
			showHelp();
			return;
		}

		try {
			MetaFile metaFile = MetaFileCreater.createFromTorrentFile(torrentFile);
			_showPieces(metaFile.getPieces());
		} catch (IOException e) {
			showTorrentFileReadIsFailed();
			e.printStackTrace();
		}
	}

	public static void show(String message) {
		System.out.println("#" + message);
	}

	public static void _showPieces(BenString pieces) {
		String TAG = "<PIECES>";
		{
			show(TAG + "Length:" + pieces.toByte().length);
		}
		{
			StringBuilder array = new StringBuilder();
			for (int i = 0; i < pieces.toByte().length; i++) {
				array.append("" + (0xFF & pieces.toByte()[i]) + "("
						+ ((char) pieces.toByte()[i]) + "),");
			}
			show(TAG + "Content:" + array.toString());
		}
	}

	public static void showTorrentFileReadIsFailed() {
		StringBuilder message = new StringBuilder();
		message.append("[Error] failed to read a torrent file.\r\n");
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
		message.append("Usage: java " + MainReadTorrentFile.class.getName() + " [FILE]...\r\n");
		message.append("FILE is a Torrent File\r\n");
		message.append("\r\n");
		message.append("Sample: ");
		message.append("java " + MainReadTorrentFile.class.getName() + " ./a.torrent " + "\r\n");
		System.out.println(message.toString());
	}
}
