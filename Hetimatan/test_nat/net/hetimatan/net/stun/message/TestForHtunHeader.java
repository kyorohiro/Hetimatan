package net.hetimatan.net.stun.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.net.stun.message.attribute.HtunChangeRequest;
import junit.framework.TestCase;

public class TestForHtunHeader extends TestCase {
	public void testEmptyAttribute() throws IOException {
		byte[] id = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		HtunHeader header = new HtunHeader(HtunHeader.BINDING_REQUEST, id);
		CashKyoroFile output = new CashKyoroFile(1000);
		header.encode(output.getLastOutput());
		byte[] buffer = CashKyoroFileHelper.newBinary(output);
		{
			byte[] expected = {
					0x00, 0x00, 
					0x00, HtunHeader.BINDING_REQUEST,
					0x00, 0x00, // length
					1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 //id
			};
			for(int i=0;i<expected.length;i++) {
				assertEquals("["+i+"]", expected[i], buffer[i]);
			}
		}

		{//decode
			MarkableFileReader reader = new MarkableFileReader(buffer);
			HtunHeader exHeader = HtunHeader.decode(reader);
			assertEquals(HtunHeader.BINDING_REQUEST, exHeader.getType());
			reader.close();
		}
	}


	public void testChangeReauestAttribute() throws IOException {
		byte[] id = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		HtunHeader header = new HtunHeader(HtunHeader.BINDING_REQUEST, id);
		header.addAttribute(new HtunChangeRequest(HtunChangeRequest.STATUS_CHANGE_IP));
		CashKyoroFile output = new CashKyoroFile(1000);
		header.encode(output.getLastOutput());
		byte[] buffer = CashKyoroFileHelper.newBinary(output);
		{
			byte[] expected = {
					0x00, 0x00, 
					0x00, HtunHeader.BINDING_REQUEST,
					0x00, 0x08, // length
					1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, //id
					0x00, HtunAttribute.CHANGE_RESUQEST,
					0x00, 0x04,
					0x00, 0x00, 0x00, HtunChangeRequest.STATUS_CHANGE_IP
			};
			for(int i=0;i<expected.length;i++) {
				assertEquals("["+i+"]", expected[i], buffer[i]);
			}
		}

		{//decode
			MarkableFileReader reader = new MarkableFileReader(buffer);
			HtunHeader exHeader = HtunHeader.decode(reader);
			assertEquals(HtunHeader.BINDING_REQUEST, exHeader.getType());
			assertEquals(1, exHeader.numOfAttribute());
			assertEquals(false, ((HtunChangeRequest)exHeader.getHtunAttribute(0)).chagePort());
			assertEquals(true, ((HtunChangeRequest)exHeader.getHtunAttribute(0)).changeIp());
			
			reader.close();
		}
	}
}
