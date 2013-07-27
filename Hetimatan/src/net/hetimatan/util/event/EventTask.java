package net.hetimatan.util.event;

import net.hetimatan.util.log.Log;

public abstract class EventTask {
	public static int sid = 0;
	public int mid = sid++;
	private EventTask mNextAction = null;
	private EventTask mErrorAction = null;
	public EventTask() {
	}

	public boolean isKeep() {
		return false;
	}

	public boolean isNext() {
		return true;
	}


	public final void run(EventTaskRunner runner) {
		try {
			if(Log.ON){Log.d("taskrunner","["+mid+"]"+"action:"+toString());}
			action(runner);
			if (isKeep()) {
				if(Log.ON){Log.d("taskrunner","["+mid+"]"+"action: next keep");}
				runner.start(this);				
			} else if(runner != null&&mNextAction != null&&isNext()) {
				if(Log.ON){Log.d("taskrunner","["+mid+"]"+"action: next "+mNextAction.toString());}
				runner.start(mNextAction);	
			} 
		} catch(Throwable t) {
			// for debug
			t.printStackTrace();
			if(mErrorAction != null) {
				runner.start(mErrorAction);
			}
		} 
	}

	public void action(EventTaskRunner runner) throws Throwable {
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

}
