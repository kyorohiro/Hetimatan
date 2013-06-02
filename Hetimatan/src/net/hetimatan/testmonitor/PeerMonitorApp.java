package net.hetimatan.testmonitor;


import java.io.File;
import java.io.IOException;

import net.hetimatan.net.torrent.tracker.TrackerServer;
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
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	public void startServer() throws IOException {
		String arg = "./Hetimatan/testdata/1m_a.txt.torrent";
		mMetafile = MetaFileCreater.createFromTorrentFile(new File(arg));
		mServer = new TrackerServer();
		mServer.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
		mServer.startServer(null);
		mServer.addData(mMetafile);
	}

	@Override
	public void onUpdate(TrackerServer server) {
		mUpdatedNum++;
		mInfo.setText("###:"+mUpdatedNum);
	}
	private int mUpdatedNum = 0;
}
