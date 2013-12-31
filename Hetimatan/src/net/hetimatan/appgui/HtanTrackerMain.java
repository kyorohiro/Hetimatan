package net.hetimatan.appgui;


import java.io.File;
import java.io.IOException;

import net.hetimatan.net.torrent.tracker.TrackerServer;
import net.hetimatan.net.torrent.tracker._server.TrackerDB;
import net.hetimatan.net.torrent.tracker._server.TrackerData;
import net.hetimatan.net.torrent.tracker._server.TrackerDatam;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.http.HttpGetRequestUri;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HtanTrackerMain extends Application implements TrackerServer.StatusCheck {

	private TrackerServer mServer = null;
	private MetaFile mMetafile = null;
	private Text mInfo = new Text(25, 25,"---\r\n-----\r\n-----\r\n-");
	private Text mPath = new Text("xx");
	private Button mOpenButton = new Button("open torrent");
	private Button mStartButton = new Button("start tracker");
	private File mTorrent = (new File("d")).getAbsoluteFile().getParentFile();

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
		root.getChildren().add(mOpenButton);
		root.getChildren().add(mStartButton);
		root.getChildren().add(mInfo);

		buttonSetting();
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		if(mServer !=  null) {
			mServer.close();
		}
	}

	private boolean isStarted() {
		if(mServer == null) {
			return false;
		}
		if(mServer.isBinded()){
			return true;
		} else {
			return false;
		}
	}
	private void buttonSetting() {
		mOpenButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if(isStarted()){return;}
				FileChooser fc = new FileChooser();
				if(mTorrent.isFile()) {
					fc.setInitialDirectory(mTorrent.getAbsoluteFile().getParentFile());
				} else if(mTorrent.isDirectory()){
					fc.setInitialDirectory(mTorrent.getAbsoluteFile());
				}
				File ret = fc.showOpenDialog(null);
				if(ret != null){// && ret.isFile()) {
					mTorrent = ret;
					mPath.setText(ret.getAbsolutePath());
					System.out.println(""+ret.getAbsolutePath());
				}
			}
		});
		mStartButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					if(isStarted()){return;}
					startServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
	
	public void startServer() throws IOException {
		File f = mTorrent;//new File(arg);
		if(f == null || !f.exists() || f.isDirectory()) {return;}
		mMetafile = MetaFileCreater.createFromTorrentFile(f);
		mServer = new TrackerServer();
		{
			String url = mMetafile.getAnnounce();
			HttpGetRequestUri uri = HttpGetRequestUri.createHttpRequestUri(url);
			mServer.setPort(uri.getPort());
		}
//		mServer.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
		mServer.startServer(null);
		mServer.addData(mMetafile);
		mServer.setStatusCheck(this);
	}

	@Override
	public void onUpdate(TrackerServer server, int event) {
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
}
