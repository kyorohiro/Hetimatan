package net.hetimatan.net.torrent.tracker;


import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.tracker.db.TrackerData;
import net.hetimatan.net.torrent.tracker.db.TrackerDatam;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenInteger;
import net.hetimatan.net.torrent.util.bencode.BenList;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.io.ByteArrayBuilder;


//
//
//TrackerClient and TrackerServer use this class
public class TrackerResponse {

	public final static String KEY_FAILURE_REASON  = "failure_reason";
	public final static String KEY_WARNING_MESSAGE = "warning message";
	public final static String KEY_INTERVAL        = "interval";
	public final static String KEY_TRACKER_ID      = "tracker id";
	public final static String KEY_COMPLETE       = "complete";
	public final static String KEY_INCOMPLETE      = "incomplete";
	// - peers bencoding
	//     list
	// - peers binary
	//     <ip(inet_aton):4byte> <port:2byte> (<ip:4byte> <port:2byte>)* #bigendian
	public final static String KEY_PEERS           = "peers";
	public final static String KEY_PEER_ID         = "peer_id";
	public final static String KEY_IP              = "ip";
	public final static String KEY_PORT            = "port";



	private int mInterval = 0;
	private long mComplete = 0;
	private long mIncomplete = 0;
	private int mCompact = 0;
	private ByteArrayBuilder mAddressList = new ByteArrayBuilder(512);
	private LinkedList<String> mPeerId = new LinkedList<String>();

	private String mFailureReason = "";
	private String mWarningMessage = "";

	public TrackerResponse putWarningMessage(String message) {
		mWarningMessage = message;//KEY_FAILURE_REASON
//		KEY_WARNING_MESSAGE
		return this;
	}

	public TrackerResponse putFaulureReason(String message) {
		mFailureReason = message;//KEY_FAILURE_REASON
		return this;
	}

	public TrackerResponse putInterval(int interval) {
		mInterval = interval;
		return this;
	}

	public TrackerResponse putComplete(int complete) {
		mComplete = complete;
		return this;
	}

	public TrackerResponse putIncomplete(int incomplete) {
		mIncomplete = incomplete;
		return this;
	}

	public TrackerResponse putCompact(int compact) {
		mCompact = compact;
		return this;
	}
	
	public long getIncomplete() {
		return mIncomplete;
	}

	public int getInterval() {
		return mInterval;
	}

	public long getComplete() {
		return mComplete;
	}

	public int getCompact() {
		return mCompact;
	}

	public String getFailureReason() {
		return mFailureReason;
	}

	public String getWarningMessage() {
		return mWarningMessage;
	}

	public int numOfIp() {
		return mAddressList.length()/6;
	}

	public int getPort(int location) {
		int size = numOfIp();
		if(location >= size) {
			return 0;
		}
		byte[] buffer = mAddressList.getBuffer();
		byte[] tmp = new byte[2];
		int index = location*6;
		tmp[0] = buffer[index+4];
		tmp[1] = buffer[index+5];
		return HttpObject.bToPort(tmp);
	}

	public String getIP(int location) {
		int size = numOfIp();
		if(location >= size) {
			return "";
		}
		byte[] buffer = mAddressList.getBuffer();
		byte[] tmp = new byte[4];
		int index = location*6;
		tmp[0] = buffer[index+0];
		tmp[1] = buffer[index+1];
		tmp[2] = buffer[index+2];
		tmp[3] = buffer[index+3];
		return HttpObject.ntoa(tmp);
	}

	public static TrackerResponse decode(MarkableReader reader) throws IOException {
		BenDiction diction = null;
		try {
			reader.pushMark();
			diction = BenDiction.decodeDiction(reader);
		} catch(IOException e) {
			reader.backToMark();
			throw e;
		} finally {
			reader.pushMark();
		}

		TrackerResponse response = new TrackerResponse();
		response.putWarningMessage(BenObject.parseString(diction.getBenValue(KEY_WARNING_MESSAGE), response.mWarningMessage));
		response.putFaulureReason(BenObject.parseString(diction.getBenValue(KEY_FAILURE_REASON), response.mFailureReason));
		response.putInterval(BenObject.parseInt(diction.getBenValue(KEY_INTERVAL), (int)response.mInterval));
		response.putIncomplete(BenObject.parseInt(diction.getBenValue(KEY_INCOMPLETE), (int)response.mIncomplete));
		response.putComplete(BenObject.parseInt(diction.getBenValue(KEY_COMPLETE), (int)response.mComplete));

		BenObject peers = diction.getBenValue(KEY_PEERS);
		if(peers != null) {

			if(peers.getType() == BenObject.TYPE_STRI){
				response.putCompact(1);
			} else {
				response.putCompact(0);			
			}

			{
				response.mAddressList.clear();
				response.mPeerId.clear();
				if (1 == response.getCompact()) {
					if(peers != null) {
						response.mAddressList.append(peers.toByte());
					}
				} else {
					int size = peers.size();
					for(int i=0;i<size;i++) {
						BenObject p = peers.getBenValue(i);
						BenObject peerid = p.getBenValue(KEY_PEER_ID);
						BenObject ip = p.getBenValue(KEY_IP);
						BenObject port = p.getBenValue(KEY_PORT);

						if(ip==null || port==null) {
							continue;
						}
						response.mAddressList.append(HttpObject.aton(ip.toString()));
						response.mAddressList.append(HttpObject.portToB(port.toInteger()));
						if(peerid==null) {
							response.mPeerId.add("");						
						} else {
							response.mPeerId.add(peerid.toString());
						}
					}
				}
			}
		}
		return response;
	}


	public static BenDiction createResponce(TrackerData data, TrackerDatam info, int compact) {
		TrackerDatam[] peerInfos = new TrackerDatam[50];// default response peer's size
		int len = data.getPeerInfoAtRamdom(peerInfos);

		BenDiction diction  = new BenDiction();
		diction.append(KEY_INTERVAL,   new BenInteger(data.getInterval()));
		diction.append(KEY_COMPLETE,   new BenInteger(data.getComplete()));
		diction.append(KEY_INCOMPLETE, new BenInteger(data.getIncomplete()));

		if(compact != 1) {
			BenList peers = new BenList();
			diction.append(KEY_PEERS, peers);
			for(int i=0;i<len;i++) {
				BenDiction peer = new BenDiction();
				peer.append(KEY_PEER_ID, new BenString(peerInfos[i].getPeerId()));
				peer.append(KEY_IP     , new BenString(peerInfos[i].getIP()));
				peer.append(KEY_PORT   , new BenInteger(peerInfos[i].getPort()));
				peers.append(peer);
			}
		} else {
			ByteBuffer buffer = ByteBuffer.allocate(50*6);
			buffer.order(ByteOrder.BIG_ENDIAN);
			for(int i=0;i<len;i++) {
				byte[] ip;
				try {
					ip = HttpObject.aton(peerInfos[i].getIP());
					buffer.put(ip, 0, 4);
					buffer.putShort((short)peerInfos[i].getPort());
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			byte[] bufferAsArray = buffer.array();
			diction.append(KEY_PEERS, new BenString(bufferAsArray, 0, buffer.position(), "utf8"));

		}
		return diction;
	}


}
