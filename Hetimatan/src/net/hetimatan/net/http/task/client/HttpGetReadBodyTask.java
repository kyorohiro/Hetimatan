package net.hetimatan.net.http.task.client;


import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HttpGetReadBodyTask extends EventTask {

	public static final String TAG = "HttpGetReadBodyTask";
	private WeakReference<HttpGet> mOwner = null;
	private EventTask mLast = null;
	private boolean mHeaderIsReadable = false;

	public HttpGetReadBodyTask(HttpGet client, EventTaskRunner runner, EventTask last) {
		super(runner);
		mOwner = new WeakReference<HttpGet>(client);
		mLast = last;
		errorAction(last);
	}

	@Override
	public String toString() {
		return TAG;
	}

	//
	@Override
	public void action() throws IOException, InterruptedException {
		HttpGet httpGet = mOwner.get();
		httpGet.recvBody();
		if(httpGet.isRedirect()) {
			httpGet.updateRedirect(httpGet.getLocation());
			httpGet.startTask(getRunner(), mLast);
		} else {
			nextAction(mLast);
		}
		//todo 
//		if(mOwner.get().bodyIsReadeable()) {
//			nextAction(new HttpGetRecvBodyTask(mOwner.get(), getRunner(), mLast));
//		} else {
//			Thread.yield();
//			nextAction(this);
//		}
	}
	
}
