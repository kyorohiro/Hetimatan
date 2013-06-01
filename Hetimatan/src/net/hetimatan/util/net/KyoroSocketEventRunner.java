package net.hetimatan.util.net;

import java.io.IOException;

import com.sun.corba.se.pept.transport.Selector;

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
		//if(
		waitBySelectable(timeout);///) {
			//
			pushWork(mOneShot);
		//}
	}

	public boolean waitBySelectable(int timeout) throws IOException, InterruptedException {
		if(Log.ON){Log.v(TAG, "waitBySelectable "+numOfWork());}
		if(numOfWork() == 0) {
			if(timeout<0) {
				mSelector.select(5000);//todo you test at 10000
			} else {
				mSelector.select(timeout);
			}
		} else {
			mSelector.select(0);			
		}
		boolean ret = false;
		while(mSelector.next()) {
			ret = true;
			if(!mSelector.getCurrentSocket().startEventTask()) {
			//	if(Log.ON){Log.v(TAG,"Wearning not task");}
			}
		}
		return ret;
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

	private SelctorLoopTask mOneShot = new SelctorLoopTask(this);
	public static class SelctorLoopTask extends EventTask {
		public SelctorLoopTask(KyoroSocketEventRunner runner) {
			super(runner);
		}

		@Override
		public void action() throws Throwable {
			super.action();
			EventTaskRunner runner = getRunner();
			if(runner != null) {
				if(getRunner().numOfWork()!=0){
					if(((KyoroSocketEventRunner)runner).waitBySelectable(0)){
					}
					((KyoroSocketEventRunner)runner).pushWork(this);
				}
			}
		}
	}
}
