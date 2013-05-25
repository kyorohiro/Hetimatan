package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.ky.io.KyoroFile;
import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

public abstract class TorrentMessage {
	public static final String PROTOCOL_ID = "BitTorrent protocol";
	public static final byte[] RESERVED = {0, 0, 0, 0, 0, 0, 0, 0};
	public static final byte[] EMPTY    = {0, 0, 0, 0, 0, 0, 0, 0};
	public static final int DUMMY_SIGN_SHAKEHAND = 1001;
	public static final int DUMMY_SIGN_KEEPALIVE = 1002;
	public static final int DUMMY_SIGN_NULL = 1003;
	public static final byte SIGN_CHOKE = 0;
	public static final byte SIGN_UNCHOKE = 1;
	public static final byte SIGN_INTERESTED = 2;
	public static final byte SIGN_NOTINTERESTED = 3;
	public static final byte SIGN_HAVE = 4;
	public static final byte SIGN_BITFIELD = 5;
	public static final byte SIGN_REQUEST = 6;
	public static final byte SIGN_PIECE = 7;
	public static final byte SIGN_CANCEL = 8;


	private int mType = 0;
	public TorrentMessage(int type) {
		mType = type;
	}

	public int getType() {
		return mType;
	}

	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------
	//--------------------------------------------------------------------------

	/**
	 * sign's length is 1byte
	 */
	public static final byte SIGN_LENGTH = 1;

	public abstract void encode(OutputStream output) throws IOException;
	public static byte[] _value(MarkableReader reader, int length) throws IOException {
		try {
			byte[] hashId = new byte[length];
			reader.pushMark();
			int len = reader.read(hashId, 0, hashId.length);
			if(len!=length) {
				throw new IOException();
			}
			return hashId;
		} catch(IOException e) {
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();
		}
	}

	public static int read(MarkableReader reader, KyoroFile output, int start, int length) throws IOException {
		byte[] _buffer = new byte[256];
		int _len =0;
		int _writed=0;
		output.seek(0);

		while(true) {
			_len = _buffer.length;
			if ((length-_writed) < _len) {
				_len = (length-_writed);
			}
			if(_len<=0){
				break;
			}
			_len = reader.read(_buffer, 0, _len);
			if(_len<0){
				break;
			}
			output.addChunk(_buffer, 0, _len);
			_writed += _len;
		}
		return _writed;
	}

	public static int write(OutputStream output, KyoroFile file, int start, int length) throws IOException {
		byte[] _buffer = new byte[256];
		int _len =0;
		int _writed=0;
		file.seek(start);
		while(_writed<length) {
			_len = _buffer.length;
			if ((length-_writed) < _len) {
				_len = (length-_writed);
			}
			_len = file.read(_buffer, 0, _len);
			if(_len<0){
				break;
			}
			output.write(_buffer, 0, _len);
			_writed += _len;
		}
		return _writed;
	}

	public static byte _signed(MarkableReader reader, byte expect) throws IOException {
		int sign = reader.read();
		if(sign != expect) {
			throw new IOException();
		}
		return (byte)sign;
	}

	public static int _length(MarkableReader reader) throws IOException {
		return _decodeInt(reader);
	}

	public static int _length(MarkableReader reader, int expected) throws IOException {
		byte[] buffer = _value(reader, 4);
		int len = ByteArrayBuilder.parseInt(buffer, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
		if(len != expected) {
			throw new IOException();
		}
		return len;
	}

	public static int _decodeInt(MarkableReader reader) throws IOException {
		byte[] buffer = _value(reader, 4);
		return ByteArrayBuilder.parseInt(buffer, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
	}

	public static String toStringFrom(int id) {
		switch(id) {
		case TorrentMessage.SIGN_CHOKE: return "choke";
		case TorrentMessage.SIGN_UNCHOKE: return "unchoke";
		case TorrentMessage.SIGN_INTERESTED: return "interested";
		case TorrentMessage.SIGN_NOTINTERESTED: return "notinterested";
		case TorrentMessage.SIGN_HAVE: return "have";
		case TorrentMessage.SIGN_BITFIELD: return "bitfield";
		case TorrentMessage.SIGN_REQUEST: return "request";
		case TorrentMessage.SIGN_PIECE: return "piece";
		case TorrentMessage.SIGN_CANCEL: return "cancel";
		default:
			return "null("+id+")";
		}
	}
}
