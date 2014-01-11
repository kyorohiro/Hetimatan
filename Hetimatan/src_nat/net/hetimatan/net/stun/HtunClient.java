package net.hetimatan.net.stun;

import java.io.IOException;

import net.hetimatan.io.net.KyoroDatagramImpl;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.net.stun.HtunServer.ReceiveTask;
import net.hetimatan.net.stun.HtunServer.SendTask;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.url.PercentEncoder;

public class HtunClient {

	private KyoroSocketEventRunner mRunner = null;
	private KyoroDatagramImpl mDatagramSocket = null;
	private byte[] mStunIp;
	private byte[] mMainIp;
	
	public HtunClient(byte[] mainIp, byte[] stunIp) {
		mStunIp = stunIp;
		mMainIp = mainIp;	
	}

	public void init() throws IOException {
		mDatagramSocket = new KyoroDatagramImpl();
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
		mRunner.pushTask(new SendTask(mStunIp, "test".getBytes()), 3000);
		return mRunner;
	}

	public class ReceiveTask extends EventTask {
		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			byte[] ip = mDatagramSocket.receive();
			byte[] buffer = mDatagramSocket.getByte();
			System.out.println("##="+new String(buffer));
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
			runner.pushTask(this, 3000);
		}
	}

}
