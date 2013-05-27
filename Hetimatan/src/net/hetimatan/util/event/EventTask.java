package net.hetimatan.util.event;


import java.lang.ref.WeakReference;

import net.hetimatan.util.log.Log;


public abstract class EventTask implements Runnable {
	private EventTask mNextAction = null;
	private EventTask mErrorAction = null;
	private WeakReference<EventTaskRunner> mRunner = null;

	public EventTask(EventTaskRunner runner) {
		mRunner = new WeakReference<EventTaskRunner>(runner);
	}

	@Override
	public final void run() {
		try {
			//if(Log.ON){Log.v("a","action");}
			action();
			//if(Log.ON){Log.v("a","/action");}
			EventTaskRunner runner = mRunner.get();
			if(runner != null&&mNextAction != null) {
				mRunner.get().start(mNextAction);	
			} 
		} catch(Throwable t) {
			// for debug
			t.printStackTrace();
			if(mErrorAction != null) {
				mRunner.get().start(mErrorAction);
			}
		} 
	}

	public void action() throws Throwable {
		;
	}

	public final EventTask nextAction() {
		return mNextAction;
	}

	public final EventTask nextAction(EventTask task) {
		mNextAction = task;
		return task;
	}

	public final EventTask errorAction(EventTask task) {
		mErrorAction = task;
		return task;
	}

	public EventTaskRunner getRunner() {
		return mRunner.get();
	}

	public static class PrintlnLogTask extends EventTask {
		public PrintlnLogTask(EventTaskRunner runner) {
			super(runner);
		}
	}
}
