package net.hetimatan.net.http.task;


import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.log.Log;

public class HttpGetReadHeaderTask extends EventTask {

	WeakReference<HttpGet> mOwner = null;
	private EventTask mLast = null;

	public HttpGetReadHeaderTask(HttpGet client, EventTaskRunner runner, EventTask last) {
		super(runner);
		mOwner = new WeakReference<HttpGet>(client);
		mLast = last;
		errorAction(last);
	}

	private boolean mHeaderIsReadable = false;
	@Override
	public void action() throws IOException, InterruptedException {
		if(!mHeaderIsReadable) {
			mHeaderIsReadable = mOwner.get().headerIsReadeable();
			if(Log.ON){Log.v("===", "mHeaderIsReadable="+mHeaderIsReadable);}
			if(!mHeaderIsReadable) {
				nextAction(this);
				//nextAction(null);
				return;
			}
		}
	
		mOwner.get().recvHeader();
		nextAction(new HttpGetReadBodyTask(mOwner.get(), getRunner(), mLast));
	}
	
}
