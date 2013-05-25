package net.hetimatan.net.http.task;


import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HttpGetRequestTask extends EventTask {

	WeakReference<HttpGet> mOwner = null;

	private EventTask mLast = null;
	public HttpGetRequestTask(HttpGet client, EventTaskRunner runner, EventTask last) {
		super(runner);
		mOwner = new WeakReference<HttpGet>(client);
		mLast = last;
		errorAction(last);
	}

	//
	//
	@Override
	public void action() throws InterruptedException, IOException {
		mOwner.get().send();
		nextAction(new HttpGetReadHeaderTask(mOwner.get(), getRunner(), mLast));
	}
	
}
