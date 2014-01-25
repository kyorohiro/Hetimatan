package net.hetimatan.io.net;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.SelectableChannel;
import java.util.LinkedList;
import java.util.Stack;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import net.hetimatan.util.io.ByteArrayBuilder;

//
// test用
// 画面上に表示する
//
public class KyoroDatagramMock extends KyoroDatagram {
	public static final int NIC_TYPE_FULL_CONE = 0;
	public static final int NIC_TYPE_RESTRICTED = 1;
	public static final int NIC_TYPE_RESTRICTED_PORT = 2;
	public static final int NIC_TYPE_SYMMETRIC = 3;

	private int mNatType = 0;
	public KyoroDatagramMock(int type) {
		mNatType = type;
	}

	public int getNatType() {
		return mNatType;
	}

	private Stack<DatagramPacket> mPackets = new Stack<>();
	private WeakReference<KyoroSelector> mCurrentSelector = null;
	public void onReceivePacket(byte[] content, byte[] ip) {
		mPackets.push(new DatagramPacket(content, ip));
		if(mCurrentSelector == null) {
			return;
		}
		KyoroSelector selector = this.mCurrentSelector.get();
		if(selector == null) {
			return;
		}
		selector.wakeup(this, KyoroSelector.READ);
	}

	private byte[] mIp = {0,0,0,0, 0,0};
	private DatagramPacket mCurrentPacket = null;

	@Override
	public SelectableChannel getRawChannel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bind(byte[] ip) throws IOException {
		for(int i=0;i<ip.length;i++) {
			mIp[i] = ip[i];
		}
		if( null != DatagramUiMgr.getInstance().find(mIp) ) {
			throw new IOException();
		}
		DatagramUiMgr.getInstance().bind(this);
	}

	public byte[] getIp() {
		return mIp;
	}

	@Override
	public void bind(int port) throws IOException {
		byte[] portByte = ByteArrayBuilder.parseInt(port, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
		mIp[4] = portByte[0];
		mIp[4+1] = portByte[1];
		if( null == DatagramUiMgr.getInstance().find(mIp) ) {
			throw new IOException();
		}
		DatagramUiMgr.getInstance().bind(this);
	}

	@Override
	public byte[] getByte() {
		if(mCurrentPacket == null) {
			return new byte[0];
		}
		return mCurrentPacket.mData;
	}

	@Override
	public byte[] receive() throws IOException {
		if(mPackets.size()>0) {
			DatagramPacket packet = mPackets.pop();
			if(packet != null) {
				mCurrentPacket = packet;
				return mCurrentPacket.mIp;
			}
		}
		mCurrentPacket = null;
		return new byte[0];
	}

	@Override
	public int send(byte[] message, byte[] address) throws IOException {
		KyoroDatagramMock datagram = DatagramUiMgr.getInstance().find(address);
		if(datagram == null) { throw new IOException();}
		datagram.onReceivePacket(message, getIp());
		return message.length;
	}

	@Override
	public int send(byte[] message, int start, int length, byte[] address)
			throws IOException {
		byte[] ms = new byte[length];
		for(int i=0;i>length;i++) {
			ms[i] = message[i+start];
		}
		return send(ms, address);
	}

	@Override
	public void regist(KyoroSelector selector, int key) throws IOException {
		mCurrentSelector = new WeakReference<KyoroSelector>(selector);
		selector.putClient(this);		
	}

	public static class DatagramPacket {
		private byte[] mData = new byte[0];
		private byte[] mIp = {0,0,0,0, 0,0};
		public DatagramPacket(byte[] content, byte[] ip) {
			mData = new byte[content.length];
			for(int i=0;i<mData.length;i++) {
				mData[i] = content[i];
			}
			
			mIp = new byte[ip.length];
			for(int i=0;i<ip.length;i++) {
				mIp[i] = ip[i];
			}
		}
	}

	@Override
	public void close() throws IOException {
		DatagramUiMgr.getInstance().close(this);
		super.close();
	}

	public static class DatagramUiMgr {
		private static DatagramUiMgr sInst = null;
		public static DatagramUiMgr getInstance() {
			if(sInst == null) {
				sInst = new DatagramUiMgr();
			}
			return sInst;
		}

		LinkedList<KyoroDatagramMock> bindedList = new LinkedList<>();
		public DatagramUiMgr() {
		}

		public void bind(KyoroDatagramMock datagram) {
			bindedList.add(datagram);
		}

		public void close(KyoroDatagramMock datagram) {
			bindedList.remove(datagram);
		}

		public KyoroDatagramMock find(byte[] ip) {
			for(KyoroDatagramMock item: bindedList) {
				if(item == null) {continue;}
				byte[] itemIp = item.getIp();
				boolean isEqual = true;
				for(int i=0;i<ip.length;i++) {
					if(itemIp[i] != ip[i]) {
						isEqual = false;
						break;
					}
				}
				if(isEqual) {
					return item;
				}
			}
			return null;
		}
	}
}
