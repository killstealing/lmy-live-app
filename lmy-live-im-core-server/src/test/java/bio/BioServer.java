package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket=new ServerSocket();
        serverSocket.bind(new InetSocketAddress(9090));
        Socket socket=serverSocket.accept();
        while(true){
            InputStream inputStream = socket.getInputStream();
            byte[] bytes=new byte[10];
            inputStream.read(bytes);
            System.out.println("服务端收到的数据是： "+new String(bytes));


        }
    }
}
