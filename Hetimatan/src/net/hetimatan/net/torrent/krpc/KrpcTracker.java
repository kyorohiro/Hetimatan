package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.net.KyoroDatagramImpl;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;
import net.hetimatan.util.url.PercentEncoder;

public class KrpcTracker {
	private int mPort = 10001;
	private KyoroDatagramImpl mReceiver = null;
	private EventTask mReceiveTask = null;

	public int getPort() {
		return mPort;
	}

	public void boot() throws IOException {
		if(mReceiver == null) {
			mReceiver = new KyoroDatagramImpl();
			for(int i=0;i<100;i++) {
				try {
					mReceiver.bind(mPort);
					return;
				} catch(IOException e) {
					mPort++;
					mReceiver = null;
				}
			}
		}
		throw new IOException();
	}

	public boolean isBoot() {
		return (mReceiver==null?false:true); 
	}
	public void close() {
		try {
			mReceiver.close();
			mReceiver = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public KyoroSocketEventRunner start (KyoroSocketEventRunner runner) throws IOException {
		if(runner == null) {
			runner = new KyoroSocketEventRunner();
		}
		boot();
		mReceiver.regist(runner.getSelector(), KyoroSelector.READ);
		mReceiver.setEventTaskAtWrakReference(mReceiveTask, KyoroSelector.READ);
		return runner;
	}

	public void receive() throws IOException {
		byte[] address = mReceiver.receive();
		if(address == null) {
			return;
		}
		MarkableFileReader reader = new MarkableFileReader(mReceiver.getByte());
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			Log.v("test", diction.toString());
		} catch(IOException e) {
			PercentEncoder encoder = new PercentEncoder();
			Log.v("test", encoder.encode(mReceiver.getByte()));			
		}
	}

	public class Receivetask extends EventTask {
		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			receive();
		}
	}
}
