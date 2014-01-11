package net.hetimatan.net.stun;

import java.io.IOException;

import net.hetimatan.util.event.net.KyoroSocketEventRunner;
import net.hetimatan.util.http.HttpObject;

public class Test {
	public static HtunServer todo = null;
	public static HtunClient todoc = null;
	public static void main(String[] args) {
		try {
			todo = new HtunServer(
					HttpObject.address("127.0.0.1", 8080),
					HttpObject.address("127.0.0.1", 8081));
			todoc = new HtunClient(
					HttpObject.address("127.0.0.1", 8082),
					HttpObject.address("127.0.0.1", 8080));
			KyoroSocketEventRunner runner = todo.startTask(null);
			KyoroSocketEventRunner runnerC = todoc.startTask(null);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
