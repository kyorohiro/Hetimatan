package net.hetimatan.net.http;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.request.HttpGetRequester;
import net.hetimatan.net.http.request.HttpGetResponse;
import net.hetimatan.net.http.task.client.HttpGetConnectionTask;
import net.hetimatan.net.http.task.client.HttpGetReadBodyTask;
import net.hetimatan.net.http.task.client.HttpGetReadHeaderTask;
import net.hetimatan.net.http.task.client.HttpGetRequestTask;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;
import net.hetimatan.util.event.net.MessageSendTask;
import net.hetimatan.util.http.HttpRequest;
import net.hetimatan.util.http.HttpRequestUri;
import net.hetimatan.util.http.HttpRequestHeader;
import net.hetimatan.util.http.HttpResponse;
import net.hetimatan.util.log.Log;

public class HttpGet {

	public static final String TAG = "HttpGet";
	public String sId = "[httpget]";

	private HttpGetRequester mCurrentRequest = null;
	private HttpGetResponse mResponse = null;
	private KyoroSocket mCurrentSocket = null;
	private String mHost = "127.0.0.1";
	private String mPath = "/";
	private int mPort = 80;
	private EventTaskRunner mRunner = null;
	private CashKyoroFile mSendCash = null;
	private HttpGetTaskManager mTaskManager = new HttpGetTaskManager();

	public HttpGet() throws IOException {}

	public EventTaskRunner getRunner() {return mRunner;}

	public CashKyoroFile getSendCash() {return mSendCash;}

	public KyoroSocket getSocket() {return mCurrentSocket;}

	protected HttpGetResponse getGetResponse() {return mResponse;}

	protected HttpGetRequester createGetRequest() {
		if (mCurrentRequest == null) {
			mCurrentRequest = new HttpGetRequester();
		}
		return mCurrentRequest;
	}

	public void update(String host, String path, int port) throws IOException {
		mHost = host;
		mPath = path;
		mPort = port;
		sId = "[httpget "+mHost+":"+mPort+mPath+"]";
		HttpHistory.get().pushMessage(sId+"#update:"+"\n");
		dispose();
		mSendCash = new CashKyoroFile(1024, 3);
	}

	public void update(String location) throws IOException {
		MarkableFileReader reader = null;
		try {
			reader = new MarkableFileReader(location.getBytes());
			HttpRequestUri geturi = HttpRequestUri.decode(reader);
			update(geturi.getHost(), geturi.getMethod(), geturi.getPort());
		} finally {
			reader.close();
		}
	}

	public KyoroSocketEventRunner startTask(KyoroSocketEventRunner runner, EventTask last) {
		HttpHistory.get().pushMessage(sId+"#startTask"+"\n");
		mTaskManager.mLast = last;
		if(runner == null) {
			runner = new KyoroSocketEventRunner();
		}
		mRunner = runner;
		HttpGetConnectionTask connectionTask = new HttpGetConnectionTask(this, last);
		connectionTask.nextAction(new HttpGetRequestTask(this, last));
		runner.start(connectionTask);
		return runner; 
	}

	public void connection() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#connection()");}
		mCurrentRequest = createGetRequest();
		mResponse = null;
		mCurrentRequest.getUrlBuilder().setHost(mHost).setPath(mPath).setPort(mPort);
		mCurrentSocket = mCurrentRequest.connect(null);
	}

	public void send() throws InterruptedException, IOException {
		if(Log.ON){Log.v(TAG, "HttpGet#send()");}
		KyoroSocketEventRunner runner = KyoroSocketEventRunner.getYourWorker();
		
		mCurrentRequest.setSelector(runner.getSelector());
		HttpRequest request = ((HttpGetRequester)mCurrentRequest).createHttpRequest();
		CashKyoroFile cash = getSendCash();
		request.encode(cash.getLastOutput());
		MessageSendTask sendTask = new MessageSendTask(getSocket(), getSendCash());
		HttpGetReadHeaderTask readHeaderTask = new HttpGetReadHeaderTask(this, mTaskManager.mLast);
		readHeaderTask.nextAction(new HttpGetReadBodyTask(this, mTaskManager.mLast));
		sendTask.nextAction(readHeaderTask);
		getRunner().pushTask(sendTask);
		mTaskManager.mSendTaskChain = sendTask;
	}

	public void recvHeader() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#revcHeader()");}
		if(mResponse == null) {
			mResponse = mCurrentRequest.getResponse(mCurrentSocket);
		}
		HttpHistory.get().pushMessage(sId+"#recvHeader:"+"\n");
		mResponse.readHeader();
	}

	public void recvBody() throws IOException, InterruptedException {
		HttpHistory.get().pushMessage(sId+"#recvBody:"+"\n");
		mResponse.readBody();

		try {
			CashKyoroFile vf = mResponse.getVF();
			vf.seek(mResponse.getVFOffset());
			int len = (int)vf.length();
			byte[] buffer = new byte[len];
			vf.read(buffer, 0, len);
			System.out.println("@1:"+new String(buffer, 0, mResponse.getVFOffset()));
			System.out.println("@2:"+new String(buffer));
			System.out.println("@3:"+mResponse.getVFOffset()+","+buffer.length);

		} finally {
			close();
		}
	}

	public boolean isRedirect() throws IOException {
		HttpGetResponse response = getGetResponse();
		HttpResponse httpResponse = response.getHttpResponse();
		String statusCode = httpResponse.getStatusCode();
		for(String candidate :HttpResponse.REDIRECT_STATUSCODE) {
			if (candidate.equals(statusCode)) {
				return true;
			}
		}
		return false;
	}

	public String getLocation() throws IOException {
		HttpGetResponse response = getGetResponse();
		HttpResponse httpResponse = response.getHttpResponse();
		String path = httpResponse.getHeader(HttpRequestHeader.HEADER_LOCATION);
		path = path.replaceAll(" ", "");
		if(path.startsWith("http://")) {
			return path;
		} else {
			if(!path.startsWith("/")) {
				path = "/"+path;
			}
			return mHost+":"+mPort+""+path;
		}
	}

	/**
	 * you must call dispose too.
	 * this method don't release response cash 
	 */
	public void close() throws IOException {
		HttpHistory.get().pushMessage(sId+"#close:"+"\n");
		if(mCurrentSocket != null) {
			mCurrentSocket.close();
			mCurrentSocket = null;
		}
		if(mSendCash != null) {
			mSendCash.close();
			mSendCash = null;
		}
	}

	public void dispose() throws IOException {
		if(mResponse!= null) {
			mResponse.close();
			mResponse = null;
		}
	}

	//
	//
	//
	public boolean isConnected() throws IOException {
		switch (mCurrentSocket.getConnectionState()) {
		case KyoroSocket.CN_CONNECTED:
			HttpHistory.get().pushMessage(sId+"#connected"+"\n");
			return true;
		case KyoroSocket.CN_CONNECTING:
			return false;
		case KyoroSocket.CN_DISCONNECTED:
		default:
			HttpHistory.get().pushMessage(sId+"#disconnected"+"\n");
			throw new IOException();
		}
	}

	public boolean headerIsReadeable() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#headerIsReadeable()");}
		if(mResponse == null) {
			mResponse = mCurrentRequest.getResponse(mCurrentSocket);
		}
		return mResponse.headerIsReadable();
	}

	public boolean bodyIsReadeable() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#bodyIsReadeable()");}
		if(mResponse == null) {
			mResponse = mCurrentRequest.getResponse(mCurrentSocket);
		}
		return mResponse.bodyIsReadable();
	}

}

