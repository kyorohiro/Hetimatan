package net.hetimatan.net.stun;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.net.KyoroDatagramImpl;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.net.stun.message.HtunAttribute;
import net.hetimatan.net.stun.message.HtunHeader;
import net.hetimatan.net.stun.message.attribute.HtunChangeRequest;
import net.hetimatan.net.stun.message.attribute.HtunXxxAddress;
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
		mDatagramSocket.bind(mMainIp);
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


			//
			// parse message
			MarkableFileReader reader = new MarkableFileReader(buffer);
			HtunHeader header = HtunHeader.decode(reader);
			HtunAttribute changeRequest = header.findHtunAttribute(HtunAttribute.CHANGE_RESUQEST);
			if(changeRequest == null) {
				// error response
			}
			boolean changePort = ((HtunChangeRequest)changeRequest).chagePort();
			boolean changeIp = ((HtunChangeRequest)changeRequest).changeIp();

			//
			// create response
			KyoroDatagramImpl tmp = null;
			if(changeIp&&changePort) {
				tmp = mDatagramSocket;
			} else if(changeIp&&!changePort) {
				tmp = mDatagramSocket;
			} else if(!changeIp&&!changePort) {
				tmp = mDatagramSocket;				
			} else if(!changeIp&&changePort) {
				tmp = mDatagramSocket;				
			}

			//
			//
			HtunHeader response = new HtunHeader(HtunHeader.BINDING_RESPONSE, header.getId());
			response.addAttribute(new HtunXxxAddress(
					HtunAttribute.MAPPED_ADDRESS, 0x01, ip));
			response.addAttribute(new HtunXxxAddress(
					HtunAttribute.SOURCE_ADDRESS, 0x01, (byte[])tmp.getMemo()));
			response.addAttribute(new HtunXxxAddress(
					HtunAttribute.CHANGE_ADDRESS, 0x01, (byte[])tmp.getMemo()));

			CashKyoroFile output = new CashKyoroFile(1024);
			response.encode(output.getLastOutput());
			runner.pushTask(new SendTask(
					ip, CashKyoroFileHelper.newBinary(output)));
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
