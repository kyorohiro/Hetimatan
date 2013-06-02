package net.hetimatan.net.torrent.tracker.db;


import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import net.hetimatan.util.url.PercentEncoder;


public class TrackerDB {
	public LinkedHashMap<String, TrackerData> mManagedDatas = new LinkedHashMap<String, TrackerData>();
	private PercentEncoder mEncoder = new PercentEncoder();

	public TrackerData getManagedData(String infoHashAsRaider) {
		if(checkManagingDataFromInfoHashAsRaider(infoHashAsRaider)) {
			return mManagedDatas.get(infoHashAsRaider);
		} else {
			return null;			
		}
	}

	public int numOfTrackerData() {
		return mManagedDatas.size();
	}

	public String getInfoHash(int index) {
		Object[] ob = mManagedDatas.keySet().toArray();
		if(index<ob.length) {
			Object ret = ob[index];
			if(ret != null) {
				return ret.toString();
			}
		}
		return "";
	}

	public void addManagedData(byte[] infoHashAsByte) {
		String key = mEncoder.encode(infoHashAsByte);
		if (!mManagedDatas.containsKey(key)) {
			TrackerData data = new TrackerData(infoHashAsByte);
			mManagedDatas.put(key, data);
		}
	}

	public boolean isManaged(String infoHashAsString) {
		byte[] infoHashAsByte = null;
		try {
			infoHashAsByte = mEncoder.decode(infoHashAsString.getBytes());
			return checkManagingDataFromInfoHash(infoHashAsByte);
		} catch (IOException e) {
			return false;
		}
	}

	public boolean checkManagingDataFromInfoHash(byte[] infoHashAsByte) {
		// convert Raider base Percent String.
		String key = mEncoder.encode(infoHashAsByte);
		if (mManagedDatas.containsKey(key)) {
			return true;
		} else {
			return false;
		}
	}

	private boolean checkManagingDataFromInfoHashAsRaider(String infoHashAsRaider) {
		if (mManagedDatas.containsKey(infoHashAsRaider)) {
			return true;
		} else {
			return false;
		}
	}

	public String convertInfoHashForRaider(String infoHashAsString) {
		try {
			byte[] infoHashAsByte = mEncoder.decode(infoHashAsString.getBytes());
			String key = mEncoder.encode(infoHashAsByte);
			return key;
		} catch (IOException e) {
			return infoHashAsString;
		}
	}

}
