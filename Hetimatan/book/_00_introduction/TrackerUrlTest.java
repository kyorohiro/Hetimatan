package _00_introduction;

import java.io.File;
import java.io.IOException;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.http.HttpRequestUri;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TrackerUrlTest extends Application {
	private File mInput = (new File("dummy")).getAbsoluteFile().getParentFile();
	private TextField mUrl = new TextField("show tracker url"); 
	private Button mOpenFile = new Button("open torrent");

	@Override
	public void start(Stage primaryStage) throws Exception {
		FlowPane root = new FlowPane();
		root.setOrientation(Orientation.VERTICAL);
		Scene secne = new Scene(root);
		root.getChildren().add(mUrl);
		root.getChildren().add(mOpenFile);
		mUrl.minWidthProperty().bind(secne.widthProperty());
		primaryStage.setScene(secne);
		primaryStage.show();
		buttonSetting();
	}

	public void buttonSetting() {
		mOpenFile.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				if(mInput.isFile()) {
					fc.setInitialDirectory(mInput.getAbsoluteFile().getParentFile());
				} else if(mInput.isDirectory()){
					fc.setInitialDirectory(mInput.getAbsoluteFile());
				}
				File ret = fc.showOpenDialog(null);
				if(ret != null){
					mInput = ret;
					updateUrl();
				}
			}
		});
	}

	public void updateUrl() {
		try {
			mUrl.setText("" + getUrl());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getUrl() throws IOException {
		MetaFile metainfo = MetaFileCreater.createFromTorrentFile(mInput);
		TrackerRequest request = TrackerRequest.decode(metainfo);
		request.putPeerId(TorrentClient.createPeerIdAsPercentEncode());
		HttpRequestUri uri = request.createUri();
		uri.IsAbsolutePath(true);
		return uri.toString();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
