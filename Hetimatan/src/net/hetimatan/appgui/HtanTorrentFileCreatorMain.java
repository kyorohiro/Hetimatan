package net.hetimatan.appgui;

import java.io.File;
import java.io.IOException;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import net.hetimatan.appcui.MainCreateTorrentFile;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HtanTorrentFileCreatorMain extends Application {

	private File mInput = (new File("d")).getAbsoluteFile().getParentFile();
	private Text mText = new Text(""+mInput.getAbsolutePath());
	private TextField mAddress = new TextField("http://127.0.0.1/announce");
	private Text mInfo = new Text("...");
	private Button mOpenFile = new Button("open file");
	private Button mOpenFolder = new Button("open folder");
	private Button mStart = new Button("start");

	@Override
	public void start(Stage arg0) throws Exception {
		FlowPane root = new FlowPane();
		root.setOrientation(Orientation.VERTICAL);
		Scene secne = new Scene(root);
		root.getChildren().add(mText);
		root.getChildren().add(mOpenFile);
		root.getChildren().add(mOpenFolder);
		root.getChildren().add(mAddress);
		root.getChildren().add(mStart);
		root.getChildren().add(mInfo);
		arg0.setScene(secne);
		arg0.show();
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
				if(ret != null){// && ret.isFile()) {
					mInput = ret;
					mText.setText(ret.getAbsolutePath());
					System.out.println(""+ret.getAbsolutePath());
				}
			}
		});
		mOpenFolder.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DirectoryChooser fc = new DirectoryChooser();
				if(mInput.isFile()) {
					fc.setInitialDirectory(mInput.getAbsoluteFile().getParentFile());
				} else if(mInput.isDirectory()){
					fc.setInitialDirectory(mInput.getAbsoluteFile());
				}
				File ret = fc.showDialog(null);
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
