package net.hetimatan.net.http.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpServer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HttpServerWaitAcceptableTask extends EventTask {
	private WeakReference<HttpServer> mServer = null;

	public HttpServerWaitAcceptableTask(HttpServer httpServer, EventTaskRunner runner) {
		super(runner);
		mServer = new WeakReference<HttpServer>(httpServer);
		errorAction(new HttpServerClose(httpServer, runner));
	}

	@Override
	public void action() throws Throwable {
		HttpServer server = mServer.get();
		if(0==getRunner().numOfWork()) {
			server.waitAndDispatchMessage(Integer.MAX_VALUE);
		} else {
			System.out.println("---");
		}
	}
}