package net.hetimatan.comp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import net.hetimatan.util.log.Log;

public class Sample_NonBlockingServer {

    private static final int SERVER_PORT = 8888;
    private static final int BUF_SIZE = 2000;

    private Selector selector;

    public static void main(String[] args) {
        Sample_NonBlockingServer nserver = new Sample_NonBlockingServer();
        nserver.start();
    }


    public void start(){
        ServerSocketChannel serverChannel = null;
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            while (selector.select() > 0) {
                if(Log.ON){Log.v("","Select");}
                for (Iterator it = selector.selectedKeys().iterator();
                     it.hasNext();) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        if(Log.ON){Log.v("","Accept");}
                        doAccept((ServerSocketChannel) key.channel());
                    } else if (key.isReadable()) {
                        SocketChannel channel = (SocketChannel)key.channel();
                        doRead(channel);
                    }
                }
                if(Log.ON){Log.v("","DONE");}
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void doAccept(ServerSocketChannel serverChannel) {
        if(Log.ON){Log.v("","ACCEPT");}
        try {
            SocketChannel channel = serverChannel.accept();
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } 
        if(Log.ON){Log.v("","END");}
    }

    private void doRead(SocketChannel channel) {
        if(Log.ON){Log.v("","DoRead");}
        ByteBuffer buf = ByteBuffer.allocate(BUF_SIZE);
        Charset charset = Charset.forName("UTF-8");
        try {
            if (channel.read(buf) < 0) {
                return;
            }
            buf.flip();
            System.out.print(
                    charset.decode(buf).toString());
            buf.flip();
            if(Log.ON){Log.v("","Write");}
            if(buf.array().length == 0) {
                 System.out.println(new String(buf.array()));            	
            }
       //     System.out.println(new String(buf.array()));
            channel.write(buf);
            if(Log.ON){Log.v("","Close");}
            channel.close();
            if(Log.ON){Log.v("","FIN");}
        } catch (IOException ioe) {
           ioe.printStackTrace();
        } finally {
            if(Log.ON){Log.v("","END");}
        }
    }
}