package net.hetimatan.net.http.task;


import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HttpGetReadBodyTask extends EventTask {

	private WeakReference<HttpGet> mOwner = null;
	private EventTask mLast = null;
	private boolean mHeaderIsReadable = false;

	public HttpGetReadBodyTask(HttpGet client, EventTaskRunner runner, EventTask last) {
		super(runner);
		mOwner = new WeakReference<HttpGet>(client);
		mLast = last;
		errorAction(last);
	}

	//
	@Override
	public void action() throws IOException, InterruptedException {
		mOwner.get().recvBody();
		nextAction(mLast);
		//todo 
//		if(mOwner.get().bodyIsReadeable()) {
//			nextAction(new HttpGetRecvBodyTask(mOwner.get(), getRunner(), mLast));
//		} else {
//			Thread.yield();
//			nextAction(this);
//		}
	}
	
}
