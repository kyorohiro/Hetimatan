package net.hetimatan.io.net;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.SelectableChannel;

import net.hetimatan.util.event.EventTask;

public abstract class KyoroSelectable {
	public abstract SelectableChannel getRawChannel();
	private String mDebug = "001";
	private WeakReference<Object> mRelative = null; 

	private WeakReference<EventTask> mAcceptTask = null; 
	private WeakReference<EventTask> mReadTask = null; 
	private WeakReference<EventTask> mWriteTask = null; 
	private WeakReference<EventTask> mConnectTask = null; 

	public void setRelative(Object obj) {
		mRelative = new WeakReference<Object>(obj);
	}

	public void setDebug(String deubg) {
		mDebug = deubg;
	}

	public Object getRelative() {
		if(mRelative == null) {
			return null;
		}
		return mRelative.get();
	}

	public void setEventTaskAtWrakReference(EventTask task, int state) {
		if((state&KyoroSelector.ACCEPT)==KyoroSelector.ACCEPT) {
			mAcceptTask = new WeakReference<EventTask>(task);
		}
		if((state&KyoroSelector.READ)==KyoroSelector.READ) {
			mReadTask = new WeakReference<EventTask>(task);
		}
		if((state&KyoroSelector.WRITE)==KyoroSelector.WRITE) {
			mAcceptTask = new WeakReference<EventTask>(task);
		}
		if((state&KyoroSelector.CONNECT)==KyoroSelector.CONNECT) {
			mConnectTask = new WeakReference<EventTask>(task);
		}
	}

	public boolean startEventTask(int key) {
		boolean ret = false;
		if((key&KyoroSelector.ACCEPT)==KyoroSelector.ACCEPT) {
			ret |= action(mAcceptTask);
		}
		if((key&KyoroSelector.READ)==KyoroSelector.READ) {
			ret |= action(mReadTask);
		}
		if((key&KyoroSelector.WRITE)==KyoroSelector.WRITE) {
			ret |= action(mWriteTask);
		}
		if((key&KyoroSelector.CONNECT)==KyoroSelector.CONNECT) {
			ret |= action(mConnectTask);
		}
		return true;
	}
	private boolean action(WeakReference<EventTask> eventTask ) {
		if(eventTask == null) {
			return false;
		}
		EventTask task =  eventTask.get();
		if(task == null) {
			return false;
		}
		task.getRunner().start(task);
		return true;
	}

	public void close() throws IOException  {
		mRelative = null;
		mAcceptTask = null;
		mConnectTask = null;
		mReadTask = null;
		mWriteTask = null;
	}
}
