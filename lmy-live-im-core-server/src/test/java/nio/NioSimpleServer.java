package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class NioSimpleServer {
    private static List<SocketChannel> socketChannelList=new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(9090));
        serverSocketChannel.configureBlocking(false);
        System.out.println("服务端启动成功");
        new Thread(()->{
            while(true){
                for (SocketChannel socketChannel:socketChannelList) {
                    ByteBuffer byteBuffer=ByteBuffer.allocate(10);
                    try {
                        socketChannel.read(byteBuffer);
                        System.out.println("服务端收到数据："+new String(byteBuffer.array()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        while(true){
            SocketChannel socketChannel=serverSocketChannel.accept();
            if(socketChannel!=null){
                System.out.println("连接成功了");
                socketChannel.configureBlocking(false);
                socketChannelList.add(socketChannel);
            }
        }
    }
}
