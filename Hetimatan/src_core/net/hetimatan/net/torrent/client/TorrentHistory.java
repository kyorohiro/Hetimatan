package net.hetimatan.net.torrent.client;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.util.event.GlobalAccessProperty;
import net.hetimatan.util.log.Log;



public class TorrentHistory implements TorrentClientListener {

	private CashKyoroFile mCash = null;
	private static TorrentHistory sHistory = null;

	private TorrentHistory() throws IOException {
		File parent = (new File("dummy")).getAbsoluteFile().getParentFile();
		String path = GlobalAccessProperty.getInstance().get("my.home", parent.getAbsolutePath());
		File home = new File(path);
		mCash = new CashKyoroFile(new File(home, "history"), 512, 2);
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

	public synchronized void pushReceive(TorrentClientFront front, TorrentMessage message) {
		if(message == null || front == null) {
			return;
		}
		log("["+front.getDebug()+"]"+"recv", message);
	}

	public synchronized void pushSend(TorrentClientFront front, TorrentMessage message) {
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

	@Override
	public void onConnection(TorrentClientFront front) throws IOException {
		pushMessage(""+front.mDebug+": connect front");
	}

	@Override
	public void onClose(TorrentClientFront front) throws IOException {
		pushMessage(""+front.mDebug+": close front");
	}

	@Override
	public void onClose(TorrentClient client) throws IOException {
		pushMessage(""+client.sId+": close client");
	}

	@Override
	public void onShakeHand(TorrentClientFront front) throws IOException {
		pushMessage(""+front.mDebug+": shakehand");
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		pushSend(front, message);
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		pushReceive(front, message);
	}

	@Override
	public void onResponsePeerList(TrackerClient client) throws IOException {

	}

}
