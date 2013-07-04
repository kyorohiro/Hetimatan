package net.hetimatan.hetimatan;

import java.io.File;

import net.hetimatan.console.MainCreateTorrentFile;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class HtanToolMain extends Application {

	private File mInput = (new File("d")).getAbsoluteFile().getParentFile();
	private Text mText = new Text(""+mInput.getAbsolutePath());
	private TextArea mAddress = new TextArea("http://127.0.0.1/announce");
	private Button mOpen = new Button("open");
	private Button mStart = new Button("start");

	@Override
	public void start(Stage arg0) throws Exception {
		FlowPane root = new FlowPane();
		Scene secne = new Scene(root);
		root.getChildren().add(mText);
		root.getChildren().add(mOpen);
		root.getChildren().add(mAddress);
		root.getChildren().add(mStart);
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
				String[] args = new String[2];
				args[0] = ""+mAddress.getText();
				args[1] = ""+mInput.getAbsolutePath();
				MainCreateTorrentFile.main(args);
			}
		});
	}

	public void create() {
		
	}
	public static void main(String[] args) {
		Application.launch(args);
	}
}
