package net.hetimatan.net.http.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HttpFrontWaitParseableHeader extends EventTask {
	public static final String TAG = "HttpFrontWaitParseableHeader";
	private WeakReference<HttpFront> mClientInfo = null;

	public HttpFrontWaitParseableHeader(HttpFront clientInfo, EventTaskRunner runner) {
		super(runner);
		mClientInfo = new WeakReference<HttpFront>(clientInfo);
		errorAction(new HttpFrontCloseTask(clientInfo, runner));
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action() throws Throwable {
		HttpFront info = mClientInfo.get();
		if(info == null) {
			return;
		} 
		if(info.isOkToParseHeader()) {
			nextAction(this);
		} else {
			nextAction(this);
		}
	}
}
