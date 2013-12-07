package net.hetimatan.tool;

import java.io.IOException;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.util.event.CloseRunnerTask;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;

public class WGet {

	public static void main(String[] args) {
		System.out.println("start");
		
		try {
			HttpGet getter = new HttpGet();
			getter.update("http://localhost:80?q=%E7%97%9B%E3%81%84");
			CloseRunnerTask close = new CloseRunnerTask(null);
			KyoroSocketEventRunner runner = getter.startTask(null, close);
			runner.waitByClose(30000);
			runner.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();			
		} finally {
			System.out.println("end");
		}
	}
}
