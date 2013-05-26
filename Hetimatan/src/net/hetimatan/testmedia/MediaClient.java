package net.hetimatan.testmedia;


import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class MediaClient extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		Group root = new Group();
		//
		Scene scene = new Scene(root, 400, 300);
		// 
		primaryStage.setTitle("test");
		primaryStage.setScene(scene);
		primaryStage.show();

		//
//		String mpath = "http://download.oracle.com/otndocs/products/javafx/oow2010-2.flv";
		//mpath = "file:///c:/a/h264.mp4";
//		String mpath = "file:///c:/a/a.flv";
		String mpath = "file:///c:/a/c.mp4";
		
		Media media = new Media(mpath);
		MediaPlayer player = new MediaPlayer(media);
		player.setAutoPlay(true);
		player.setCycleCount(MediaPlayer.INDEFINITE);
		MediaView view = new MediaView(player);
		Circle c = new Circle();

		root.getChildren().add(view);
		
	}


	public static void main(String[] args) {
		Application.launch(args);
	}
}
