package net.hetimatan.util.event;


import java.lang.ref.WeakReference;

import net.hetimatan.util.log.Log;


public abstract class EventTask implements Runnable {
	public static int sid = 0;
	public int mid = sid++;
	private EventTask mNextAction = null;
	private EventTask mErrorAction = null;
	private WeakReference<EventTaskRunner> mRunner = null;

	public EventTask(EventTaskRunner runner) {
		mRunner = new WeakReference<EventTaskRunner>(runner);
	}

	public boolean isKeep() {
		return false;
	}

	public boolean isNext() {
		return true;
	}

	@Override
	public final void run() {
		try {
			if(Log.ON){Log.v("mm","["+mid+"]"+"action:"+toString());}
			action();
			//if(Log.ON){Log.v("a","/action");}
			EventTaskRunner runner = mRunner.get();
			if (isKeep()) {
				if(Log.ON){Log.v("mm","["+mid+"]"+"action: next null");}
				mRunner.get().start(this);				
			} else if(runner != null&&mNextAction != null&&isNext()) {
				if(Log.ON){Log.v("mm","["+mid+"]"+"action: next "+mNextAction.toString());}
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

}
