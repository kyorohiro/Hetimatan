package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.io.net.KyoroDatagramImpl;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.net.torrent.krpc.message.KrpcQuery;
import net.hetimatan.net.torrent.krpc.message.KrpcResponse;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.url.PercentEncoder;

public class KrpcTracker {
	private int mPort = 10001;
	private KrpcEventController mEventManager = null;
	private KyoroDatagramImpl mReceiver = null;
	private EventTask mReceiveTask = null;

	public int getPort() {
		return mPort;
	}

	public KyoroDatagramImpl getDatagram() {
		return mReceiver;
	}

	public void boot() throws IOException {
		if(mReceiver == null) {
			mReceiver = new KyoroDatagramImpl();
			for(int i=0;i<100;i++) {
				try {
					mReceiver.bind(mPort);
					mEventManager = new KrpcEventController(mReceiver);
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
			runner.waitIsSelect(true);
		}
		boot();
		mReceiver.regist(runner.getSelector(), KyoroSelector.READ);
		mReceiver.setEventTaskAtWrakReference(mReceiveTask = new Receivetask(), KyoroSelector.READ);
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
			if(KrpcQuery.check(diction)) {
				mEventManager.query(address, diction);
			}
			else if(KrpcResponse.check(diction)) {
				mEventManager.reponse(address, diction);
			}
			Log.v("test", diction.toString());
			PercentEncoder e = new PercentEncoder();
			Log.v("test",""+new String(e.encode(address))+""+HttpObject.ntoa(address)+":"+ByteArrayBuilder.parseShort(address, 4, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		} catch(IOException e) {
			PercentEncoder encoder = new PercentEncoder();
			Log.v("test", encoder.encode(mReceiver.getByte()));			
		}
	}

	public void sendQuery(byte[] address, KrpcQuery query) throws IOException {
		mEventManager.sendQuery(address, query);
	}

	public class Receivetask extends EventTask {
		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			receive();
		}
	}
}
