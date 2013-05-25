package net.hetimatan.net.http.request;



import java.io.IOException;
import java.net.Socket;

import net.hetimatan.io.filen.RACashFile;

public interface GetResponseInter {
	public int getVFOffset() ;
	public RACashFile getVF();
	public boolean headerIsReadable() throws IOException;
	public boolean bodyIsReadable() throws IOException;
	public void read() throws IOException, InterruptedException ;
	public void readHeader() throws IOException, InterruptedException ;
	public void readBody() throws IOException, InterruptedException ;
	public void close();
}
