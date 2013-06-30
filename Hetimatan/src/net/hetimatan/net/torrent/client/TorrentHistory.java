package net.hetimatan.net.torrent.client;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.util.event.GlobalAccessProperty;
import net.hetimatan.util.log.Log;


public class TorrentHistory {

	private RACashFile mCash = null;
	private static TorrentHistory sHistory = null;

	private TorrentHistory() throws IOException {
		File parent = (new File("dummy")).getAbsoluteFile().getParentFile();
		String path = GlobalAccessProperty.getInstance().get("my.home", parent.getAbsolutePath());
		File home = new File(path);
		mCash = new RACashFile(new File(home, "history"), 512, 2);
		mCash.isCashMode(false);
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdonwTask()));
	}

	public static TorrentHistory get() {
		if(sHistory == null) {
			try {
				sHistory = new TorrentHistory();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sHistory;
	}

	public void sync() {
		try { mCash.syncWrite(); } catch (IOException e) {}
	}

	public synchronized void pushMessage(String mes) {
		if(mes != null) {
			Log.v("HISTORY", mes);
			try {
				mCash.addChunk(mes.getBytes());
			} catch(IOException e) {

			}
		}
	}

	public synchronized void pushReceive(TorrentFront front, TorrentMessage message) {
		if(message == null || front == null) {
			return;
		}
		log("["+front.getDebug()+"]"+"recv", message);
	}

	public synchronized void pushSend(TorrentFront front, TorrentMessage message) {
		log("["+front.getDebug()+"]"+"send", message);
	}

	public synchronized void log(String action, TorrentMessage message) {
		try {
			if(message != null) {
				pushMessage(""+action+" "+message.toString()+"\r\n");
			}
		} finally {	
		}
	}

	public class ShutdonwTask implements Runnable {
		@Override
		public void run() {
			sync();
		}
	}

}
