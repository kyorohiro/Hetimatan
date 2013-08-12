package _99_;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.util.event.CloseRunnerTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.http.HttpRequestUri;

public class GetTest {

	public static void main(String[] args) {
		GetTest gettest = new GetTest();
		if(args.length != 1) {
			try {
				GetTest.showMessage(System.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String address = args[0];

		try {
			gettest.request(address);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void request(String address) throws IOException {
		HttpRequestUri uri = HttpRequestUri.crateHttpGetRequestUri(address);
		HttpGet httpGet = new HttpGet();
		httpGet.update(uri.getHost(), uri.getPath(), uri.getPort());
		CloseRunnerTask closeRunnerTask = new CloseRunnerTask(null);
		EventTaskRunner runner = httpGet.startTask(null, closeRunnerTask);
		try {
			runner.waitByClose(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		httpGet.dispose();
	}

	public static void showMessage(OutputStream output) throws IOException {
		String message = "java " + GetTest.class.getName() + " [URL]...";
		output.write(message.getBytes());
	}
}
