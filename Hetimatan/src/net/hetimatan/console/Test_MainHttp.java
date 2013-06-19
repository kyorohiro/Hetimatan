package net.hetimatan.console;


import java.awt.FlowLayout;
import javax.swing.JFrame;

import net.hetimatan.net.http.HttpServer;

public class Test_MainHttp {

	public static void main(String[] args) {
		Test_MainHttp main = new Test_MainHttp();
		main.startGui();
	}

	public static HttpServer sServer = null;
	public void startGui() {
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new FlowLayout());
		frame.setBounds(0, 0, 200, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
//		{
//			TorrentClientTask task = new TorrentClientTask();
//			Thread th = new Thread(task);
//			th.start();
//		}
		{
			HttpServer server = new HttpServer();
			server.setPort(6881);
			server.startServer(null);
			sServer = server;
		}
	}

}
