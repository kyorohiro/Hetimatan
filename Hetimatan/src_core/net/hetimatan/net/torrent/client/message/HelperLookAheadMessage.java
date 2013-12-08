package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import com.sun.org.apache.xml.internal.resolver.helpers.Debug;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.client.TorrentHistory;

public class HelperLookAheadMessage {

	private MessageNull mNullMessage = new MessageNull(0, -999);

	public HelperLookAheadMessage() {
	}

	public MessageNull getMessageNull() {
		return mNullMessage;
	}


	public boolean lookahead(MarkableReader reader) throws IOException {
		long tmp = 0;
		try  {
			tmp = reader.getFilePointer();
			reader.pushMark();
			mNullMessage = MessageNull.decode(reader);
			return true;
		} catch(IOException e) {
		} catch(NegativeArraySizeException e) {			
		} finally {
			reader.backToMark();
			reader.popMark();
			if(tmp != reader.getFilePointer()) {
				TorrentHistory.get().pushMessage(" "+tmp+","+reader.getFilePointer()+"ERROR¥n");
			}
		}
		return false;
	}
	
}
