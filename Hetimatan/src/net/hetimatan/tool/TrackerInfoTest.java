package net.hetimatan.tool;

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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class TrackerInfoTest extends Application {
	private File mInput = (new File("dummy")).getAbsoluteFile().getParentFile();
	private Text mInfo = new Text("-----------"); 
	private Button mOpenFile = new Button("open torrent");

	@Override
	public void start(Stage primaryStage) throws Exception {
		FlowPane root = new FlowPane();
		root.setOrientation(Orientation.VERTICAL);
		Scene secne = new Scene(root);
		root.getChildren().add(mInfo);
		root.getChildren().add(mOpenFile);
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
			mInfo.setText("" + getInfo());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getInfo() throws IOException {
		MetaFile metainfo = MetaFileCreater.createFromTorrentFile(mInput);
		StringBuilder builder = new StringBuilder();
		String LF = System.getProperty("line.separator");
		builder.append("announce:"+metainfo.getAnnounce()+LF);
		builder.append("pieceLength:"+metainfo.getPieceLength()+LF);
		builder.append("infosha1:"+metainfo.getInfoSha1AsBenString()+LF);
		
		Long[] flen = metainfo.getFileLengths();
		long flenSum = 0;
		for(int i=0;i<flen.length;i++) {
			flenSum += flen[i];
			builder.append("filelength:["+i+"]:"+flen[i]+LF);
		}
		builder.append("bitfieldLen:"+(flenSum/metainfo.getPieceLength()));
		
		return builder.toString();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}
