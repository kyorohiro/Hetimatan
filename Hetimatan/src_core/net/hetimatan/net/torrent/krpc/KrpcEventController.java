package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.net.torrent.krpc.message.KrpcQuery;
import net.hetimatan.net.torrent.krpc.message.KrpcResponse;
import net.hetimatan.net.torrent.krpc.message.QueryFindNode;
import net.hetimatan.net.torrent.krpc.message.QueryPing;
import net.hetimatan.net.torrent.krpc.message.ResponsePing;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenString;
import net.hetimatan.util.event.net.io.KyoroDatagram;
import net.hetimatan.util.event.net.io.KyoroDatagramImpl;

public class KrpcEventController {

	private String _TODO_mMYID_ = "";
	private ByteKyoroFile mSendOutput = new ByteKyoroFile();
	private KyoroDatagram mSend = null;
	public KrpcEventController(KyoroDatagramImpl bootedSocket) {
		mSend = bootedSocket;
	}

	public void sendQuery(byte[] address, KrpcQuery query) throws IOException {
		KyoroDatagram send = mSend;///new KyoroDatagramImpl();
		mSendOutput.seek(0);
		query.encode(mSendOutput.getLastOutput());
		int len = (int)mSendOutput.length();
		send.send(mSendOutput.getBuffer(), 0, len, address);
	}

	public void sendResponse(byte[] address, KrpcResponse response) throws IOException {
		KyoroDatagram send = mSend;//new KyoroDatagramImpl();
		mSendOutput.seek(0);
		response.encode(mSendOutput.getLastOutput());
		int len = (int)mSendOutput.length();
		send.send(mSendOutput.getBuffer(), 0, len, address);
	}


	public void reponse(byte[] address, BenDiction response) {
		
	}

	public void query(byte[] address, BenDiction query) throws IOException {
		String methodName = KrpcQuery.getQueryName(query);
		if("ping".equals(methodName)) {
			onReceiveQureyPing(address, QueryPing.decode(query));
		}
		if("find_node".equals(methodName)) {
			onReceiveQureyFindNode(address, QueryFindNode.decode(query));
		}
	}

	// 
	// if recv ping then send pine response
	protected void onReceiveQureyPing(byte[] address, QueryPing query) throws IOException {
		ResponsePing response = new ResponsePing(query.getTransactionId(), 
				new BenString(_TODO_mMYID_));
		sendResponse(address, response);
	}

	//
	//
	protected void onReceiveQureyFindNode(byte[] address, QueryFindNode query) throws IOException {
		ResponsePing response = new ResponsePing(
				query.getTransactionId(),
				new BenString(_TODO_mMYID_));
		sendResponse(address, response);
	}

}
