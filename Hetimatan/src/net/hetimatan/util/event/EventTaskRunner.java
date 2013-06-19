package net.hetimatan.util.event;


import java.util.HashMap;
import java.util.WeakHashMap;

import net.hetimatan.util.io.ByteArrayBuilder;


public abstract class EventTaskRunner {
	public abstract int numOfWork();
	public abstract void releaseTask(EventTask task);
	public abstract void pushWork(EventTask task);
	public abstract void pushWork(EventTask task, int timeout);
	public abstract EventTask popWork();
	public abstract void close();
	public abstract boolean contains(EventTask task);


	//
	//-------------------------------------
	//

	private boolean mIsFirst = true;
	public void start(EventTask task) {
		if(mIsFirst) {
			putWorker(Thread.currentThread(), this);
			mIsFirst = false;
		}
	}

	public boolean isAlive() {return true;}

	//	private static WeakHashMap<Thread, EventTaskRunner> mMap = new WeakHashMap<Thread, EventTaskRunner>();
	private static HashMap<Thread, EventTaskRunner> mMap = new HashMap<Thread, EventTaskRunner>();
	public static synchronized void putWorker(Thread th, EventTaskRunner runner) {
		mMap.put(th, runner);
	}

	public static synchronized EventTaskRunner getYourWorker() {
		Thread current = Thread.currentThread();
		if(mMap.containsKey(current)) {
			return mMap.get(current);
		} else {
			return null;
		}
	}

	private ByteArrayBuilder mTemp = new ByteArrayBuilder(); 
	public ByteArrayBuilder getTemp() {
		return mTemp;
	}

	public static ByteArrayBuilder getByteArrayBuilder() {
		EventTaskRunner runner = EventTaskRunner.getYourWorker();
		if(runner != null){
			return runner.getTemp();
		} else {
			return new ByteArrayBuilder();
		}
	}

}
