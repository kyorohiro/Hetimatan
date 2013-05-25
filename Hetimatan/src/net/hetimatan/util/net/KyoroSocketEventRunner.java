package net.hetimatan.util.net;

import java.io.IOException;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.EventTaskRunnerImple;
import net.hetimatan.util.log.Log;


public class KyoroSocketEventRunner extends EventTaskRunnerImple {

	public static final String TAG ="looper";
	private KyoroSelector mSelector = new KyoroSelector();
	private SelctorLoopTask mLoopTask = null;
	private boolean mWaitIsSelect = false;

	public KyoroSocketEventRunner() {
		super();
	}

	public KyoroSelector getSelector() {
		return mSelector;
	}

	public void waitIsSelect(boolean on) {
		mWaitIsSelect = on;
	}

	@Override
	public void waitPlus(int timeout) throws InterruptedException, IOException {
		if(!mWaitIsSelect) {
			super.waitPlus(timeout);
			return;
		}

		waitBySelectable(timeout);
	//	if(mLoopTask == null) {
	//		Log.v(TAG, "start select");
	//		mLoopTask = new SelctorLoopTask(this);
	//		mLoopTask.nextAction(mLoopTask);
	//		pushWork(mLoopTask);
	//	} else {
	//			waitBySelectable();
	//	}
	}

	public void waitBySelectable(int timeout) throws IOException, InterruptedException {
		Log.v(TAG, "waitBySelectable "+numOfWork());
		if(numOfWork() == 0) {
			if(timeout<0) {
				mSelector.select(5000);//todo you test at 10000
			} else {
				mSelector.select(timeout);
			}
		} else {
			mSelector.select(0);			
		}
		while(mSelector.next()) {
			if(!mSelector.getCurrentSocket().startEventTask()) {
			//	if(Log.ON){Log.v(TAG,"Wearning not task");}
			}
		}
	}

	@Override
	public void close() {
		super.close();
		if(mSelector != null) {
			try {
				mSelector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static synchronized KyoroSocketEventRunner getYourWorker() {
		EventTaskRunner runner = EventTaskRunner.getYourWorker();
		if(runner instanceof KyoroSocketEventRunner) {
			return (KyoroSocketEventRunner)runner;
		} else {
			return null;
		}
	}

	public static class SelctorLoopTask extends EventTask {
		public SelctorLoopTask(KyoroSocketEventRunner runner) {
			super(runner);
		}

		@Override
		public void action() throws Throwable {
			super.action();
			EventTaskRunner runner = getRunner();
			if(runner != null) {
				((KyoroSocketEventRunner)runner).waitBySelectable(-1);
			}
		}
	}
}
