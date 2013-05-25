package net.hetimatan;


import java.io.File;
import java.io.IOException;

import net.hetimatan.net.torrent.tracker.TrackerServer;
import net.hetimatan.net.torrent.util.MetaFile;
import net.hetimatan.net.torrent.util.MetaFileCreater;


/**
 * 60% complete
 */
public class MainStartTracker {

	public static void main(String[] args) {
		TrackerServer server = null;
		try {
			server = new TrackerServer();
			server.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
			argsToMetafile(server, args);
			server.doStart(null);
			while (true) {
				int v = System.in.read();
				if (v == -1) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
		System.exit(0);
	}

	public static void argsToMetafile(TrackerServer server, String[] args) {
		if(args == null) {return;}
		for(String path:args) {
			try {
				metaFile(server, path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void metaFile(TrackerServer server, String path) throws IOException {
		if (path == null) {
			return;
		}

		File metafile = new File(path);
		if (!metafile.exists() || !metafile.isFile()) {
			System.out.println("#dont exist "+metafile.getPath()+"#");
			return;
		}

		MetaFile metainfo = MetaFileCreater.createFromTorrentFile(metafile);
		server.addData(metainfo);
	}

}
