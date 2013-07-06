package net.hetimatan.hetimatan;

import java.io.File;
import java.io.IOException;

import net.hetimatan.console.MainCreateTorrentFile;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HtanToolMain extends Application {

	private File mInput = (new File("d")).getAbsoluteFile().getParentFile();
	private Text mText = new Text(""+mInput.getAbsolutePath());
	private TextField mAddress = new TextField("http://127.0.0.1/announce");
	private Text mInfo = new Text("...");
	private Button mOpen = new Button("open");
	private Button mStart = new Button("start");

	@Override
	public void start(Stage arg0) throws Exception {
		FlowPane root = new FlowPane();
		root.setOrientation(Orientation.VERTICAL);
		Scene secne = new Scene(root);
		root.getChildren().add(mText);
		root.getChildren().add(mOpen);
		root.getChildren().add(mAddress);
		root.getChildren().add(mStart);
		root.getChildren().add(mInfo);
		arg0.setScene(secne);
		arg0.show();
		buttonSetting();
	}

	public void buttonSetting() {
		mOpen.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FileChooser fc = new FileChooser();
				if(mInput.isFile()) {
					fc.setInitialDirectory(mInput.getAbsoluteFile().getParentFile());
				} else if(mInput.isDirectory()){
					fc.setInitialDirectory(mInput.getAbsoluteFile());
				}
				File ret = fc.showOpenDialog(null);
				if(ret != null){// && ret.isFile()) {
					mInput = ret;
					mText.setText(ret.getAbsolutePath());
					System.out.println(""+ret.getAbsolutePath());
				}
			}
		});
		mStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mInfo.setText("start");
				String address  =  mAddress.getText();
				File input = new File(mInput.getAbsolutePath());
				File output = new File(mInput.getAbsolutePath()+".torrent");
				try {
					MainCreateTorrentFile.createTorrentFile(address, input, output);
					mInfo.setText("end ok");
				} catch (IOException e) {
					e.printStackTrace();
					mInfo.setText("end error. "+e.getStackTrace());
				} finally {
					
				}
			}
		});
	}

	public void create() {
		
	}
	public static void main(String[] args) {
		Application.launch(args);
	}
}
