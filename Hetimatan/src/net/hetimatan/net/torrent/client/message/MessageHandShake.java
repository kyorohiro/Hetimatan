package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.url.PercentEncoder;

//
//
// handshake: <pstrlen><pstr><reserved><info_hash><peer_id>
// ref https://wiki.theory.org/BitTorrentSpecification#Handshake
public class MessageHandShake extends TorrentMessage {
	private byte[] mProtocolId = "BitTorrent protocol".getBytes();
	private byte[] mInfoHash = EMPTY;
	private byte[] mPeerID = EMPTY;
	private byte[] mReserved = RESERVED;


	public void printLog() {
		PercentEncoder encoder = new PercentEncoder();
		System.out.println("protocolID:"+encoder.encode(mProtocolId));
		System.out.println("infoHash:"+encoder.encode(mInfoHash));
		System.out.println("peerID:"+encoder.encode(mPeerID));
		System.out.println("reserved:"+encoder.encode(mReserved));
	}

	public byte[] getProtocolId() {
		return mProtocolId;
	}

	public byte[] getInfoHash() {
		return mInfoHash;
	}

	public byte[] getPeerId() {
		return mPeerID;
	}

	public MessageHandShake(byte[] protocolId, byte[] reserved, byte[] infoHash, byte[] peerID) {
		super(TorrentMessage.DUMMY_SIGN_SHAKEHAND);
		mInfoHash = new byte[infoHash.length];
		mPeerID = new byte[peerID.length];
		mProtocolId = new byte[protocolId.length];
		mReserved = new byte[reserved.length];
		System.arraycopy(protocolId, 0, mProtocolId, 0, mProtocolId.length);
		System.arraycopy(reserved, 0, mReserved, 0, mReserved.length);
		System.arraycopy(infoHash, 0, mInfoHash, 0, mInfoHash.length);
		System.arraycopy(peerID, 0, mPeerID, 0, mPeerID.length);
	}

	public MessageHandShake(byte[] infoHash, byte[] peerID) {
		super(TorrentMessage.DUMMY_SIGN_SHAKEHAND);
		mInfoHash = new byte[infoHash.length];
		mPeerID = new byte[peerID.length];
		System.arraycopy(infoHash, 0, mInfoHash, 0, mInfoHash.length);
		System.arraycopy(peerID, 0, mPeerID, 0, mPeerID.length);
	}

	public void encode(OutputStream output) throws IOException {
    	output.write(PROTOCOL_ID.length());
    	output.write("BitTorrent protocol".getBytes());
        output.write(RESERVED);
        output.write(mInfoHash);
        if(isValidPeerId()) {
        	{
        		//debug
        		PercentEncoder en = new PercentEncoder();
        		System.out.println("peerid:"+mPeerID.length+"+"+new String(en.encode(mPeerID)));
        	}
    		output.write(mPeerID);
        }
    }

	public static MessageHandShake decode(MarkableReader reader) throws IOException {
		try {
			reader.pushMark();
			int prtocolIdLen = _protocolIDLength(reader);
			byte[] protocolId = _protocolID(reader, prtocolIdLen);
			byte[] reserved = _reserved(reader);
			byte[] infoHash = _infoHashId(reader);
			byte[] peerId = _peerId(reader);
			return new MessageHandShake(protocolId, reserved, infoHash, peerId);
		} catch(IOException e) {
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();
		}
	}

	public static int _protocolIDLength(MarkableReader reader) throws IOException {
		int protocolIdLength = reader.read();
		return protocolIdLength;
	}

	public static byte[] _reserved(MarkableReader reader) throws IOException {
		return _value(reader, 8);
	}

	public static byte[] _protocolID(MarkableReader reader, int length) throws IOException {
		return _value(reader, length);
	}

	public static byte[] _infoHashId(MarkableReader reader) throws IOException {
		return _value(reader, 20);
	}

	public static byte[] _peerId(MarkableReader reader) throws IOException {
		return _value(reader, 20);
	}

	public boolean isValidPeerId() {
        if(mPeerID == null || mPeerID == EMPTY || mPeerID.length != 20) {
        	return false;
        } else {
        	return true;
        }
	}

}
