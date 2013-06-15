package net.hetimatan.hetimatan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HtanMain extends Application {
	private File mTorrent = (new File(".")).getAbsoluteFile().getParentFile();
	private Text mInfo = new Text(25, 25, mTorrent.getAbsolutePath());
	private Button mOpenFileButton = new Button("open file");
	private Button mStartDownloadButton = new Button("start download");
	private HtanPeer mPeer = new HtanPeer();

	@Override
	public void start(Stage primaryStage) throws Exception {		
		FlowPane root = new FlowPane();
		Scene scene = new Scene(root, 400, 300);
		primaryStage.setTitle("test");
		primaryStage.setScene(scene);
		primaryStage.show();
		root.getChildren().add(mInfo);
		root.getChildren().add(mOpenFileButton);
		root.getChildren().add(mStartDownloadButton);		
		buttonSetting();
	}

	private void buttonSetting() {
		mOpenFileButton.setOnAction(new EventHandler<ActionEvent>() {
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
					mInfo.setText(ret.getAbsolutePath());
					System.out.println(""+ret.getAbsolutePath());
				}
			}
		});

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

	public static void main(String[] args) {
		Application.launch(args);
	}
}
