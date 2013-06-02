package net.hetimatan.testmonitor;


import java.io.File;
import java.io.IOException;

import net.hetimatan.net.torrent.tracker.TrackerServer;
import net.hetimatan.net.torrent.tracker.db.TrackerDB;
import net.hetimatan.net.torrent.tracker.db.TrackerData;
import net.hetimatan.net.torrent.tracker.db.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PeerMonitorApp extends Application implements TrackerServer.StatusCheck {

	private TrackerServer mServer = null;
	private MetaFile mMetafile = null;
	private Text mInfo = new Text(25, 25,"---\r\n-----\r\n-----\r\n-");

	@Override
	public void init() throws Exception {
		super.init();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {		
		Group root = new Group();
		Scene scene = new Scene(root, 400, 300);
		primaryStage.setTitle("test");
		primaryStage.setScene(scene);
		primaryStage.show();
		root.getChildren().add(mInfo);
		startServer();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	public void startServer() throws IOException {
		String arg = "./testdata/1m_a.txt.torrent";
		File f = new File(arg);
		mMetafile = MetaFileCreater.createFromTorrentFile(new File(arg));
		mServer = new TrackerServer();
		mServer.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
		mServer.startServer(null);
		mServer.addData(mMetafile);
		mServer.setStatusCheck(this);
	}

	@Override
	public void onUpdate(TrackerServer server) {
		TrackerDB db = server.getTrackerDB();		
		StringBuilder builder = new StringBuilder();
		builder.append("###:"+ db.numOfTrackerData()+"\r\n");
		int len = db.numOfTrackerData();
		for(int i=0;i<len;i++) {
			String infoHashAsRaider = db.getInfoHash(i);
			TrackerData data = db.getManagedData(infoHashAsRaider);
			builder.append(""+infoHashAsRaider+"\r\n");
			builder.append("  "+data.getComplete()+","+data.getIncomplete()+"\r\n");
			int jlen = data.numOfPeerInfo();
			for(int j=0;j<jlen;j++) {
				TrackerPeerInfo info = data.getPeerInfo(data.getKeyPeerInfo(j));
				builder.append("   * " + info.getIP()+":"+info.getPort()+",["+info.getPeerId()+"]\r\n");
				builder.append("       " + info.isComplete()+":"+info.getLeft()+":\r\n");
				builder.append("       " + info.getDownloaded()+":" + info.getUploaded()+"\r\n");
			}
		}
		mInfo.setText(builder.toString());
	}
}
