package net.hetimatan.hetimatan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.hetimatan.hetimatan.HtanPeer.StatusCheck;
import net.hetimatan.net.torrent.util.metafile.MetaFile;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HtanMain extends Application {
	private File mTorrent = (new File(".")).getAbsoluteFile().getParentFile();
	private File mSource = null;

	private Text mTorrentFilePath = new Text(25, 25, mTorrent.getAbsolutePath());
	private Text mSourceFilePath = new Text(25, 25, "....");
	private Text mTrackerInfo = new Text(25, 25, ".....");
	private Button mOpenTorrentFileButton = new Button("open torrent file");
//	private Button mOpenSourceFileButton = new Button("open source file");
	private Button mStartDownloadButton = new Button("start download");
	private HtanPeer mPeer = new HtanPeer();


	@Override
	public void start(Stage primaryStage) throws Exception {
		FlowPane root = new FlowPane();
		root.setOrientation(Orientation.VERTICAL);
		Scene scene = new Scene(root, 400, 300);
		primaryStage.setTitle("へちまたん");
		primaryStage.setScene(scene);
		root.setStyle("-fx-background-color: #3cb371; -fx-text-fill: #ffa500");
		root.getChildren().add(mTorrentFilePath);
		root.getChildren().add(mOpenTorrentFileButton);
		root.getChildren().add(mStartDownloadButton);
		root.getChildren().add(mTrackerInfo);
		buttonSetting();
		primaryStage.show();
		
		mPeer.setStatusCheck(new PeerStatusChecker());
	}

	@Override
	public void stop() throws Exception {
		mPeer.stop();
		super.stop();
	}

	private void buttonSetting() {
		mOpenTorrentFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				if(mTorrent.isFile()) {
					fc.setInitialDirectory(mTorrent.getAbsoluteFile().getParentFile());
				} else if(mTorrent.isDirectory()){
					fc.setInitialDirectory(mTorrent.getAbsoluteFile());
				}
				File ret = fc.showOpenDialog(null);
				if(ret != null){// && ret.isFile()) {
					mTorrent = ret;
					mTorrentFilePath.setText(ret.getAbsolutePath());
					System.out.println(""+ret.getAbsolutePath());
				}
			}
		});
		/*
		mOpenSourceFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				if(mSource == null) {
				} else if(mSource.isFile()) {
					fc.setInitialDirectory(mSource.getAbsoluteFile().getParentFile());
				} else if(mSource.isDirectory()){
					fc.setInitialDirectory(mSource.getAbsoluteFile());
				}
				File ret = fc.showOpenDialog(null);
				if(ret != null){// && ret.isFile()) {
					mSource = ret;
					mSourceFilePath.setText(ret.getAbsolutePath());
					System.out.println(""+ret.getAbsolutePath());
				}
			}
		});*/
		mStartDownloadButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					if(!mPeer.isStarted()) {
						mPeer.setTorrentFile(mTorrent);
						mPeer.start();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public class PeerStatusChecker implements StatusCheck {
		@Override
		public void onUpdateTracker() {
			mTrackerInfo.setText(mPeer.getTrackerStatus());
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
