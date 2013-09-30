package _02_tracker;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.tracker.TrackerServer;
import net.hetimatan.net.torrent.tracker.db.TrackerDB;
import net.hetimatan.net.torrent.tracker.db.TrackerData;
import net.hetimatan.net.torrent.tracker.db.TrackerDatam;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.EventTaskRunner;

public class DummyTracker  extends Application implements TrackerServer.StatusCheck {


	private TrackerServer mServer = null;
	private MetaFile mMetafile = null;
	private Text mInfo = new Text(25, 25,"---\r\n-----\r\n-----\r\n-");
	private File mTorrent = new File("./testdata/1k.txt.torrent");
	private Text mPath = new Text(mTorrent.getAbsolutePath());

	@Override
	public void init() throws Exception {
		super.init();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FlowPane root = new FlowPane();
		root.setOrientation(Orientation.VERTICAL);
		Scene scene = new Scene(root, 400, 300);
		primaryStage.setTitle("test");
		primaryStage.setScene(scene);
		primaryStage.show();
		root.getChildren().add(mPath);
		root.getChildren().add(mInfo);
		startServer();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		if(mServer !=  null) {
			mServer.close();
		}
		if(mClient != null) {
			mClient.close();
		}
		if(mRunner != null) {
			mRunner.close();
		}
	}

	
	public void startServer() throws IOException {
		File f = mTorrent;//new File(arg);
		if(f == null || !f.exists() || f.isDirectory()) {return;}
		mMetafile = MetaFileCreater.createFromTorrentFile(f);
		mServer = new TrackerServer();
		mServer.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
		mServer.startServer(null);
		mServer.addData(mMetafile);
		mServer.setStatusCheck(this);
	}

	private TorrentPeer mClient = null;
	private EventTaskRunner mRunner = null;
	public void startClient() throws IOException, URISyntaxException {
		mClient = new TorrentPeer(mMetafile, TorrentPeer.createPeerId());
		mRunner = mClient.startTask(null);
	}	

	public void onInit(TrackerServer server) {
		try {
			startClient();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public void onRequested(TrackerServer server) {
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
				TrackerDatam info = data.getPeerInfo(data.getKeyPeerInfo(j));
				builder.append("   * " + info.getIP()+":"+info.getPort()+",["+info.getPeerId()+"]\r\n");
				builder.append("       " + info.isComplete()+":"+info.getLeft()+":\r\n");
				builder.append("       " + info.getDownloaded()+":" + info.getUploaded()+"\r\n");
			}
		}
		mInfo.setText(builder.toString());		
	}

	//
	// TrackerServer.StatusCheck
	@Override
	public void onUpdate(TrackerServer server, int event) {
		if(event == TrackerServer.STATUS_BOOT) {
			onInit(server);
		} else {// TrackerServer.STATUS_RESPONSE
			onRequested(server);
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

}
