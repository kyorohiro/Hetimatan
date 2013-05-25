package net.hetimatan.net.http.task;


import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HttpGetConnectionTask extends EventTask {

	WeakReference<HttpGet> mOwner = null;
	private EventTask mLast = null;
	private boolean mIsFirst = true;	

	public HttpGetConnectionTask(HttpGet client, EventTaskRunner runner, EventTask last) {
		super(runner);
		mOwner = new WeakReference<HttpGet>(client);
		mLast = last;
		errorAction(last);
	}

	//
	@Override
	public void action() throws InterruptedException, IOException {
		if (true == mIsFirst) {
			mIsFirst = false;
			mOwner.get().connection();
		}
		if(mOwner.get().isConnected()) {
			nextAction(new HttpGetRequestTask(mOwner.get(), getRunner(), mLast));
		} else {
			nextAction(this);
		}
	}
}
