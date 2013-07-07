package net.hetimatan.net.http.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpServer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HttpServerAcceptTask extends EventTask {
	public static final String TAG = "HttpServerAcceptTask";
	private WeakReference<HttpServer> mServer = null;

	public HttpServerAcceptTask(HttpServer httpServer, EventTaskRunner runner) {
		super(runner);
		mServer = new WeakReference<HttpServer>(httpServer);
		errorAction(new HttpServerClose(httpServer, runner));
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action() throws Throwable {
		HttpServer server = mServer.get();
		server.accept();
	}
}