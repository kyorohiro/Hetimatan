package _99_;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.util.event.CloseTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.http.HttpRequestUri;

public class GetTest {

	public static void main(String[] args) {
		GetTest gettest = new GetTest();
		if(args.length != 1) {
			try {
				gettest.showMessage(System.out);
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
//		CloseTask closeTask = new CloseTask(runner, lasttask)
		EventTaskRunner runner = httpGet.startTask(null, null);
		runner.close();
	}

	public void showMessage(OutputStream output) throws IOException {
		String message = "java " + this.getClass().getName() + " [URL]...";
		output.write(message.getBytes());
	}
}
