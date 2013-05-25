package net.hetimatan.net.http.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.http.HttpServer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HttpServerClose extends EventTask {
	private WeakReference<HttpServer> mServer = null;

	public HttpServerClose(HttpServer httpServer, EventTaskRunner runner) {
		super(runner);
		mServer = new WeakReference<HttpServer>(httpServer);
	}

	@Override
	public void action() throws Throwable {
		HttpServer server = mServer.get();
		server.close();
	}
}