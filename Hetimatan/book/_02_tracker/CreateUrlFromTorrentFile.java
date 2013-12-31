package _02_tracker;

import java.io.File;
import java.io.IOException;

import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.http.HttpGetRequestUri;

//
//[課題]
//指定れたTorrentファイルを読み込んだあと、任意のTrackerへアクセスするためのURLを生成せよ。
//
public class CreateUrlFromTorrentFile {

	public static void main(String[] args) {
		try {
			File single = new File("./testdata/1k.txt.torrent");
			MetaFile metainfo = MetaFileCreater.createFromTorrentFile(single);
			TrackerRequest request = TrackerRequest.decode(metainfo);
			HttpGetRequestUri uri = request.createUri();
			uri.IsAbsolutePath(true);
			System.out.println(""+ uri.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
