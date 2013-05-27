package net.hetimatan.util.event;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;

public class EventTaskRunnerImple extends EventTaskRunner {
	private SingleTaskRunner mRunner = new SingleTaskRunner();

	private LinkedList<EventTask> mTasks = new LinkedList<EventTask>();
	private LinkedList<DefferTask> mDefferTasks = new LinkedList<DefferTask>();

	private Worker mWorker = null;

	public EventTaskRunnerImple() {
	}

	public synchronized void pushWork(EventTask task) {
		start(task);
	}

	public synchronized void pushWork(EventTask task, int timeout) {
		mDefferTasks.add(new DefferTask(task, timeout));
		kickWorker();
	}

	public int updateDeffer() {
		long curTime = System.currentTimeMillis();
		long time = -1;
		long ret = -1;
		for(int i=0;i<mDefferTasks.size();i++) {
			DefferTask task = mDefferTasks.get(i);
			time = task.deffer(curTime);
			if(time<=0) {
				pushWork(task.getEventTask());
				mDefferTasks.remove(i);
			} else {
				if(ret==-1) {
					ret = time;					
				}
				else if(ret>time) {
					ret = time;
				}
			}
		}
		return (int)ret;
	}

	public synchronized EventTask popWork() {
		try {
		if (mTasks.size() > 0) {
			return mTasks.remove(0);//removeFirst();
//			return mTasks.removeFirst();//(0);//removeFirst();
		} else {
			return null;
		}
		} catch(NoSuchElementException e) {
			return null;
		}
	}

	@Override
	public void start(EventTask task) {
		super.start(task);
		//pushWork(task);
//		mTasks.addLast(task);
		mTasks.add(task);
		if (mRunner == null || !mRunner.isAlive()) {
			mRunner = new SingleTaskRunner();
			mRunner.startTask(mWorker = new Worker(this));
		} else if(mWorker != null){
			mWorker.kick();
		}
	}

	public void dispose() {
		mRunner.endTask();
	}

	public void kickWorker() {
		mWorker.kick();
	}

	public void waitPlus(int time) throws InterruptedException, IOException {
		if(time<0) {
			Thread.sleep(10000);
		} else {
			Thread.sleep(time);			
		}
	}

	public static class Worker implements Runnable {

		WeakReference<EventTaskRunnerImple> mRunner = null;

		public Worker(EventTaskRunnerImple runner) {
			mRunner = new WeakReference<EventTaskRunnerImple>(runner);
		}

		public void kick() {
			synchronized(this){
				notifyAll();
			}
		}

		@Override
		public void run() {
			putWorker(Thread.currentThread(), mRunner.get());
			try {
				while (true) {
					EventTaskRunnerImple runner = mRunner.get();
					if (null == runner) {
						break;
					}
					EventTask task = runner.popWork();
					if (task == null) {
						 synchronized(this){
					//		 if(Log.ON){ Log.v("mm","--wait--");}
							 runner.waitPlus(runner.updateDeffer());
						 }
					} else {
					//	if(Log.ON){Log.v("mm","--run--");}
						task.run();
					//	if(Log.ON){Log.v("mm","--/run--");}
					}
				}
			} catch(InterruptedException e) {
				//e.printStackTrace();
			} catch(IOException e) {
				//e.printStackTrace();
			}
		}
	}

	@Override
	public void close() {
		if(Log.ON){Log.v("mm","EventTaskRunner#close");}
		mRunner.endTask();
	}

	@Override
	public boolean contains(EventTask task) {
		return mTasks.contains(task);
	}

	@Override
	public int numOfWork() {
		return mTasks.size();
	}


	public static class DefferTask {
		private EventTask mDefferTasks = null;
		private long mDefferEnd = 0;
		private long mDefferStart = 0;
		public DefferTask(EventTask task, int deffer) {
			mDefferStart = System.currentTimeMillis();
			mDefferEnd = mDefferStart + (long)deffer;
		}

		public long deffer(long curTime) {
			return mDefferEnd-curTime;
		}

		public EventTask getEventTask() {
			return mDefferTasks;
		}
	}
	
	
} 