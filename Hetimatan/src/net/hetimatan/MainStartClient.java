package net.hetimatan;


import java.io.File;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class MainStartClient {

	public static void main(String[] args) {
		try { start(args);
		} catch (Throwable e) { e.printStackTrace(); }
	}
	public static TorrentPeer sPeer= null;
	public static void start(String[] args) throws Throwable {
		String metafileAsString = args[0];
		File metaFile = new File(metafileAsString);
		if(!metaFile.exists()) {
			System.out.println(""+metafileAsString +" is unexiast");
			return;
		}
		MetaFile metafile =MetaFileCreater.createFromTorrentFile(metaFile);
		TorrentPeer peer = new TorrentPeer(metafile, TorrentPeer.createPeerId());
		sPeer = peer;
		{
			File[] master = new File[args.length-1];
			for(int i=0;i<master.length;i++){
				master[i] = new File(args[i+1]);
			}
			peer.setMasterFile(master);
		}
		peer.startTask(null);
	}

	public static class Last extends EventTask {
		public Last(EventTaskRunner runner) {
			super(runner);
		}
		@Override
		public void action() throws Throwable {
			super.action();
			System.out.println("END");
		}
	}
}
