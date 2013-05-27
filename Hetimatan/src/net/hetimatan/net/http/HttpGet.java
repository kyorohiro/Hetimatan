package net.hetimatan.net.http;


import java.io.IOException;

import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.request.GetRequesterInter;
import net.hetimatan.net.http.request.GetResponseInter;
import net.hetimatan.net.http.request.KyoroSocketGetRequester;
import net.hetimatan.net.http.task.HttpGetConnectionTask;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.EventTaskRunnerImple;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;

public class HttpGet {

	public static final String TAG = "HttpGet";
	private GetRequesterInter mCurrentRequest = null;
	private GetResponseInter mResponse = null;
	private KyoroSocket mCurrentSocket = null;
	private String mHost = "127.0.0.1";
	private String mPath = "/";
	private int mPort = 80;

	public HttpGet() {}
	public HttpGet(String host, String path, int port) {
		mHost = host;
		mPath = path;
		mPort = port;
	}

	protected GetRequesterInter createGetRequest() {
		if (mCurrentRequest == null) {
			mCurrentRequest = new KyoroSocketGetRequester();
			mCurrentRequest.setHost(mHost).setPath(mPath).setPort(mPort);
		}
		return mCurrentRequest;
	}

	public EventTaskRunner startTask(EventTaskRunner runner, EventTask last) {
		if(Log.ON){Log.v(TAG, "HttpGet#startTask()");}
		if(runner == null) {
			runner = new EventTaskRunnerImple();
		}
		runner.start(new HttpGetConnectionTask(this, runner, last));
		return runner; 
	}

	public void connection() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#connection()");}
		mCurrentRequest = createGetRequest();
		mCurrentSocket = mCurrentRequest._connectionRequest();
	}

	public boolean isConnected() throws IOException {
		int state = mCurrentSocket.getConnectionState();
		switch (state) {
		case KyoroSocket.CN_CONNECTED:
			if(Log.ON){Log.v(TAG, "isConnected()true");}
			return true;
		case KyoroSocket.CN_CONNECTING:
			return false;
		case KyoroSocket.CN_DISCONNECTED:
		default:
			if(Log.ON){Log.v(TAG, "isConnected()disconnected");}
			throw new IOException();
		}
	}

	public void send() throws InterruptedException, IOException {
		if(Log.ON){Log.v(TAG, "HttpGet#send()");}
		KyoroSocketEventRunner runner = KyoroSocketEventRunner.getYourWorker();
		KyoroSelector selector = null;
		if(runner != null) {
			selector = runner.getSelector();
		}
		if(selector == null) {
			selector = new KyoroSelector();
		}
		mCurrentRequest.setSelector(selector);
		mCurrentRequest._writeRequest(mCurrentSocket);
		mResponse = mCurrentRequest._getResponse(mCurrentSocket);
	}

	public boolean headerIsReadeable() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#headerIsReadeable()");}
		return mResponse.headerIsReadable();
	}

	public boolean bodyIsReadeable() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#bodyIsReadeable()");}
		return mResponse.bodyIsReadable();
	}

	public void recvHeader() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#revcHeader()");}
		mResponse.readHeader();
	}

	protected GetResponseInter getGetResponse() {
		return mResponse;
	}

	public void recvBody() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#recvBody()");}
		mResponse.readBody();

		try {
			RACashFile vf = mResponse.getVF();
			vf.seek(mResponse.getVFOffset());
			int len = (int)vf.length();
			byte[] buffer = new byte[len];
			vf.read(buffer, 0, len);
//			System.out.println("@1:"+new String(buffer, 0, mResponse.getVFOffset()));
//			System.out.println("@2:"+new String(buffer));
//			System.out.println("@3:"+mResponse.getVFOffset()+","+buffer.length);

		} finally {
			mCurrentSocket.close();
			mCurrentSocket = null;
		}
	}

	public void close() throws IOException {
		Log.v(TAG, "close");
		mCurrentSocket.close();
		mCurrentSocket = null;		
	}
}
