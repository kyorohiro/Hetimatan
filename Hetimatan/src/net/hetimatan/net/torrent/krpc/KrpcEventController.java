package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.io.net.KyoroDatagramImpl;
import net.hetimatan.net.torrent.krpc.message.KrpcQuery;
import net.hetimatan.net.torrent.krpc.message.KrpcResponse;
import net.hetimatan.net.torrent.krpc.message.QueryPing;
import net.hetimatan.net.torrent.krpc.message.ResponsePing;
import net.hetimatan.net.torrent.util.bencode.BenDiction;

public class KrpcEventController {

	private String _TODO_mMYID_ = "";
	private ByteKyoroFile mSendOutput = new ByteKyoroFile();
	public void sendQuery(byte[] address, KrpcQuery query) throws IOException {
		KyoroDatagramImpl send = new KyoroDatagramImpl();
		mSendOutput.seek(0);
		query.encode(mSendOutput.getLastOutput());
		int len = (int)mSendOutput.length();
		send.send(mSendOutput.getBuffer(), 0, len, address);
	}

	public void sendResponse(byte[] address, KrpcResponse response) throws IOException {
		KyoroDatagramImpl send = new KyoroDatagramImpl();
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
	}

	// 
	// if recv ping then send pine response
	protected void onReceiveQureyPing(byte[] address, QueryPing query) throws IOException {
		ResponsePing response = new ResponsePing(query.getTransactionId(), _TODO_mMYID_);
		sendResponse(address, response);
	}
}
