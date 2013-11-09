package net.hetimatan.appcui;


import java.io.File;
import java.io.IOException;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;


public class MainStartClient {

	public static TorrentClient sPeer= null;

	public static void main(String[] args) {
		try {
			startClient(args);
		} catch (Throwable e) { e.printStackTrace(); }
	}

	public static void startClient(String[] args) throws Throwable {
		String metafileAsString = args[0];
		File metaFile = new File(metafileAsString);
		if(!metaFile.exists()) {
			System.out.println(""+metafileAsString +" is unexiast");
			return;
		}
		MetaFile metafile =MetaFileCreater.createFromTorrentFile(metaFile);
		TorrentClient peer = new TorrentClient(metafile, TorrentClient.createPeerIdAsPercentEncode());
		sPeer = peer;
		setMasterFileFromArgs(peer, args);
		peer.startTorrentClient(null);
	}

	private static void setMasterFileFromArgs(TorrentClient peer, String[] args) throws IOException {
		File[] master = new File[args.length-1];
		for(int i=0;i<master.length;i++){
			master[i] = new File(args[i+1]);
		}
		peer.setMasterFile(master);
	}

}
