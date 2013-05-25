package net.hetimatan.net.http.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HttpFrontCloseTask extends EventTask {
	private WeakReference<HttpFront> mClientInfo = null;

	public HttpFrontCloseTask(HttpFront clientInfo, EventTaskRunner runner) {
		super(runner);
		mClientInfo = new WeakReference<HttpFront>(clientInfo);
	}

	@Override
	public void action() throws Throwable {
		HttpFront info = mClientInfo.get();
		if(info == null) {
			return;
		} 
		info.close();
	}
}
