package net.hetimatan.net.http;

import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.net.MessageSendTask;

public class HttpGetTaskManager {
	public MessageSendTask mSendTaskChain = null;
	public EventTask mLast = null;
}
