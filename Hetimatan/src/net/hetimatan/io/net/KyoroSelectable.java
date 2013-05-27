package net.hetimatan.io.net;


import java.lang.ref.WeakReference;
import java.nio.channels.SelectableChannel;

import net.hetimatan.util.event.EventTask;

public abstract class KyoroSelectable {
	public abstract SelectableChannel getRawChannel();
	private WeakReference<Object> mRelative = null; 
	private WeakReference<EventTask> mEventTask = null; 

	public void setRelative(Object obj) {
		mRelative = new WeakReference<Object>(obj);
	}

	public Object getRelative() {
		if(mRelative == null) {
			return null;
		}
		return mRelative.get();
	}

	public void setEventTaskAtWrakReference(EventTask task) {
		mEventTask =  new WeakReference<EventTask>(task);
	}

	public boolean startEventTask() {
		if(mEventTask == null) {
			return false;
		}
		EventTask task =  mEventTask.get();
		if(task == null) {
			return false;
		}
		task.getRunner().start(task);
		return true;
	}
}