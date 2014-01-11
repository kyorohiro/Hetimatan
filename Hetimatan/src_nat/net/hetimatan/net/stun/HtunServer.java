package net.hetimatan.net.stun;

import java.io.IOException;

import net.hetimatan.io.net.KyoroDatagramImpl;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.url.PercentEncoder;

public class HtunServer {

	private KyoroSocketEventRunner mRunner = null;
	private KyoroDatagramImpl mDatagramSocket = null;
	private byte[] mMainIp = null;
	private byte[] mSubIp = null;

	public HtunServer(byte[] mainIp, byte[] subIp) throws IOException {
	    mDatagramSocket = new KyoroDatagramImpl();
	    mMainIp = mainIp;
	    mSubIp = subIp;
	}

	public void init() throws IOException {
		mDatagramSocket.bind(HttpObject.bToPort(mMainIp, 4));
	}

	public KyoroSocketEventRunner startTask(KyoroSocketEventRunner runner) throws IOException {
		init();

		if(runner == null) {
			mRunner = new KyoroSocketEventRunner();
		}
		mRunner.waitIsSelect(true);
		mDatagramSocket.regist(mRunner.getSelector(), KyoroSelector.READ);
		mDatagramSocket.setEventTaskAtWrakReference(new ReceiveTask(), KyoroSelector.READ);
		mRunner.start(null);

		return mRunner;
	}

	public class ReceiveTask extends EventTask {
		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			byte[] ip = mDatagramSocket.receive();
			byte[] buffer = mDatagramSocket.getByte();
			System.out.println("##="+new String(buffer));
			
			PercentEncoder encoder = new PercentEncoder();
			runner.pushTask(new SendTask(
					ip,
					(""+encoder.encode(ip)).getBytes()
					));
		}
	}

	public class SendTask extends EventTask {
		byte[] mMessage = null;
		byte[] mIp = null;
		public SendTask(byte[] ip, byte[] meesage) {
			mIp = ip;
			mMessage = meesage;
		}

		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			mDatagramSocket.send(mMessage, mIp);
		}
	}
}
