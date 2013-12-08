package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableReader;

public class HelperLookAheadShakehand {
	
	public boolean parseable(MarkableReader _reader) throws IOException {
		boolean ret = false;
		_reader.pushMark();
		try  {
			ret = false;
			MessageHandShake.decode(_reader);
			ret = true;
		} catch(IOException e) {
		} catch(NegativeArraySizeException e) {			
		} finally {
			_reader.popMark();
		}
		return ret;
	}
	
}
