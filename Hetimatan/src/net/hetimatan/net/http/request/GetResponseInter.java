package net.hetimatan.net.http.request;



import java.io.IOException;
import java.net.Socket;

import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.util.http.HttpResponse;

public interface GetResponseInter {
	public int getVFOffset() ;
	public CashKyoroFile getVF();
	public boolean headerIsReadable() throws IOException;
	public boolean bodyIsReadable() throws IOException;
	public void read() throws IOException, InterruptedException ;
	public void readHeader() throws IOException, InterruptedException ;
	public void readBody() throws IOException, InterruptedException ;
	public HttpResponse getHttpResponse() throws IOException;
	public void close();
}
