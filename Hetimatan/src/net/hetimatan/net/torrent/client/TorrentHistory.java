package net.hetimatan.net.torrent.client;

import java.io.File;
import java.io.IOException;

import net.hetimatan.ky.io.next.RACashFile;
import net.hetimatan.net.torrent.client.message.MessagePiece;
import net.hetimatan.net.torrent.client.message.MessageRequest;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.util.log.Log;


public class TorrentHistory {

	private RACashFile mCash = null;
	private static TorrentHistory sHistory = null;

	private TorrentHistory() throws IOException {
		mCash = new RACashFile(new File("history"), 512, 2);
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
		log("recv", message);
	}

	public synchronized void pushSend(TorrentFront front, TorrentMessage message) {
		log("send", message);
	}

	public synchronized void log(String action, TorrentMessage message) {
		try {
			String mes = null;
			switch(message.getType()) {
			case TorrentMessage.SIGN_CHOKE:
				mes = "["+action+"]TM: choke\n"; break;
			case TorrentMessage.SIGN_UNCHOKE:
				mes = "["+action+"]TM: unchoke\n"; break;
			case TorrentMessage.SIGN_INTERESTED:
				mes = "["+action+"]TM: interestedn\n"; break;
			case TorrentMessage.SIGN_NOTINTERESTED:
				mes = "["+action+"]TM: notinterested\n"; break;
			case TorrentMessage.SIGN_HAVE:
				mes = "["+action+"]TM: have\n"; break;
			case TorrentMessage.SIGN_BITFIELD:
				mes = "["+action+"]TM: bitfield\n"; break;
			case TorrentMessage.SIGN_REQUEST:
				MessageRequest request = (MessageRequest)message;
				mes = "["+action+"]TM: request"+request.toString()+"\n"; break;
			case TorrentMessage.SIGN_PIECE:
				MessagePiece piece = (MessagePiece)message;
				mes = "["+action+"]TM: piece"+piece.toString()+"\n"; break;
			case TorrentMessage.SIGN_CANCEL:
				mes = "["+action+"]TM: cancel\n"; break;
			default:
				mes = "["+action+"]TM: null\n"; break;
			}
			if(mes != null) {
				mCash.addChunk(mes.getBytes());
			}
		} catch(IOException e) {
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
