package net.hetimatan.util.event;

public class CloseTask extends EventTask {
	private EventTask mLastTask = null;
	public CloseTask(EventTaskRunner runner, EventTask lasttask) {
		super(runner);
		mLastTask = lasttask;
	}

	@Override
	public boolean isKeep() {
		if(mLastTask != null) {
			return mLastTask.isKeep();
		} else {
			return super.isKeep();
		}
	}

	@Override
	public void action() throws Throwable {
		super.action();
		if(mLastTask != null) {
			mLastTask.action();
			if(mLastTask.isKeep()) {
				return;
			}
		}
		getRunner().close();
	}
}
