package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.client.TorrentHistory;

public class HelperLookAheadShakehand {
	
	public boolean parseable(MarkableReader _reader) throws IOException {
		boolean ret = false;
		long tmp = 0;
		tmp = _reader.getFilePointer();
		_reader.pushMark();
		try  {
			ret = false;
			MessageHandShake.decode(_reader);
			ret = true;
		} catch(IOException e) {
		} catch(NegativeArraySizeException e) {			
		} finally {
			_reader.backToMark();
			_reader.popMark();
			if(tmp != _reader.getFilePointer()) {
				TorrentHistory.get().pushMessage(" "+tmp+","+_reader.getFilePointer()+"ERROR¥n");
			}
		}
		return ret;
	}
	
}
