package metainfo;

import java.io.File;
import java.io.IOException;

import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;

//
// [課題]
//   指定れたTorrentファイルを読み込んで、「Trackerアドレス」と「ファイル名」、「ファイルサイズ」、「ピースサイズ」、「ハッシュ値」
//   を取得せよ。
//
public class ReadMetaInfoFile {

	public static void main(String[] args) {
		try {
			//
			File single = new File("./testdata/1k.txt.torrent");
			showMetaInfo(single);
			//
			File multi = new File("./testdata/1kb.torrent");
			showMetaInfo(multi);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void showMetaInfo(File path) throws IOException {
		System.out.println("-------" + path.getAbsolutePath());
		MetaFile metafile = MetaFileCreater.createFromTorrentFile(path);
		System.out.println("announce:"+metafile.getAnnounce());
		for(int i=0;i<metafile.numOfFiles();i++) {
			System.out.println("path:"+metafile.getFiles()[i]+",length:"+metafile.getFileLengths()[i]);
		}
		System.out.println("piece length:"+metafile.getPieceLength());
		System.out.println("piece:"+metafile.getPieces().toPercentString());
		System.out.println("\n\n");
	}
}
