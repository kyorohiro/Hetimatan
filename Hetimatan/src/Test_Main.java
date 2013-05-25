import net.hetimatan.MainStartClient;
import net.hetimatan.MainStartTracker;

public class Test_Main {
	public static final String COMMAND_CLIENT   = "client";
	public static final String COMMAND_UPLOADER = "uploader";
	public static final String COMMAND_TRACKER  = "tracker";

	public static void main(String[] args) {
//		/*
//		String pat = COMMAND_UPLOADER;
		String pat = COMMAND_CLIENT;
		String[] tmp = null;
		String command = null;
		if(pat.equals(COMMAND_UPLOADER)) {
			tmp = new String[2];
			tmp[0] = "../../a.torrent";
			tmp[1] = "./testdata/1mb/1m_a.txt";
			command = COMMAND_UPLOADER;
		}
		else if(pat.equals(COMMAND_CLIENT)) {
			tmp = new String[1];
			tmp[0] = "../../a.torrent";
			command = COMMAND_CLIENT;
		}
		else if(pat.equals(COMMAND_TRACKER)) { 
			tmp = new String[1];
			tmp[0] = "../../1m_a.txt.torrent";
			command = COMMAND_TRACKER;
		}

		if (COMMAND_UPLOADER.equals(command)) {
			MainStartClient.main(tmp);
		} 
		else if (COMMAND_CLIENT.equals(command)) {
			MainStartClient.main(tmp);
		} 		
		else if (COMMAND_TRACKER.equals(command)) {
			MainStartTracker.main(tmp);
		}
	}

}
