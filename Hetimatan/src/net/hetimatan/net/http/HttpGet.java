package net.hetimatan.net.http;


import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.request.HttpGetRequester;
import net.hetimatan.net.http.request.HttpGetResponse;
import net.hetimatan.net.http.task.client.HttpGetConnectionTask;
import net.hetimatan.net.http.task.client.HttpGetReadHeaderTask;
import net.hetimatan.net.http.task.client.HttpGetRequestTask;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.EventTaskRunnerImple;
import net.hetimatan.util.http.HttpRequest;
import net.hetimatan.util.http.HttpRequestUri;
import net.hetimatan.util.http.HttpRequestHeader;
import net.hetimatan.util.http.HttpResponse;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;

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

	public HttpGet() throws IOException {
		mSendCash = new CashKyoroFile(1024, 3);
	}

	public EventTaskRunner getRunner() {
		return mRunner;
	}
	public CashKyoroFile getSendCash() {
		return mSendCash;
	}
	public KyoroSocket getSocket() {
		return mCurrentSocket;
	}

	public void update(String host, String path, int port) {
		mHost = host;
		mPath = path;
		mPort = port;
		sId = "[httpget "+mHost+":"+mPort+mPath+"]";
	}

	public void updateRedirect(String location) throws IOException {
		HttpHistory.get().pushMessage(sId+"#redirect:"+location+"\n");
		MarkableFileReader reader = null;
		try {
			reader = new MarkableFileReader(location.getBytes());
			HttpRequestUri geturi = HttpRequestUri.decode(reader);
			update(geturi.getHost(), geturi.getMethod(), geturi.getPort());
		} finally {
			reader.close();
		}
	}

	protected HttpGetRequester createGetRequest() {
		if (mCurrentRequest == null) {
			mCurrentRequest = new HttpGetRequester();
		}
		return mCurrentRequest;
	}

	public EventTaskRunner startTask(EventTaskRunner runner, EventTask last) {
		HttpHistory.get().pushMessage(sId+"#startTask"+"\n");
		mTaskManager.mLast = last;
		initForRestart();
		if(runner == null) {
			mRunner = runner = new EventTaskRunnerImple();
		}
		HttpGetConnectionTask connectionTask = new HttpGetConnectionTask(this, runner, last);
		connectionTask.nextAction(new HttpGetRequestTask(this, runner, last));
		runner.start(connectionTask);
		return runner; 
	}

	//todo 
	protected void initForRestart() {
		mCurrentRequest = null;//todo
	}

	public void connection() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#connection()");}
		mCurrentRequest = createGetRequest();
		mCurrentRequest.getUrlBuilder()
		.setHost(mHost).setPath(mPath).setPort(mPort);
		mCurrentSocket = mCurrentRequest._connectionRequest();
	}

	public boolean isConnected() throws IOException {
		int state = mCurrentSocket.getConnectionState();
		switch (state) {
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
		HttpRequest request = ((HttpGetRequester)mCurrentRequest).createHttpRequest();
		CashKyoroFile cash = getSendCash();
		request.encode(cash.getLastOutput());
		mTaskManager.startSendTask(this);
		mTaskManager.nextTask(new HttpGetReadHeaderTask(this, getRunner(), mTaskManager.mLast));
	}

	public boolean headerIsReadeable() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#headerIsReadeable()");}
		if(mResponse == null) {
			mResponse = mCurrentRequest._getResponse(mCurrentSocket);
		}
		return mResponse.headerIsReadable();
	}

	public boolean bodyIsReadeable() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#bodyIsReadeable()");}
		if(mResponse == null) {
			mResponse = mCurrentRequest._getResponse(mCurrentSocket);
		}
		return mResponse.bodyIsReadable();
	}

	public void recvHeader() throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "HttpGet#revcHeader()");}
		if(mResponse == null) {
			mResponse = mCurrentRequest._getResponse(mCurrentSocket);
		}
		HttpHistory.get().pushMessage(sId+"#recvHeader:"+"\n");
		mResponse.readHeader();
	}

	protected HttpGetResponse getGetResponse() {
		return mResponse;
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

	public HttpResponse getHttpResponse() throws IOException {
		HttpGetResponse response = getGetResponse();
		return response.getHttpResponse();
	}

	public void close() throws IOException {
		HttpHistory.get().pushMessage(sId+"#close:"+"\n");
		mCurrentSocket.close();
		mCurrentSocket = null;		
	}
}

