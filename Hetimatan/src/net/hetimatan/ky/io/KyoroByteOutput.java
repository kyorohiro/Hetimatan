package net.hetimatan.ky.io;

import java.io.IOException;

public interface KyoroByteOutput {
	public int write(int data) throws IOException;
	public int write(byte[] data) throws IOException;
	public int write(byte[] data, int start, int len) throws IOException;
}
