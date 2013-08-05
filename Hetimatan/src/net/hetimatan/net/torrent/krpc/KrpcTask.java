package net.hetimatan.net.torrent.krpc;

public class KrpcTask {
	private String mTransactionId = "";
	public KrpcTask (String transactionId)  {
		mTransactionId = transactionId;
	}

	public String getTransactionId() {
		return mTransactionId;
	}

}
