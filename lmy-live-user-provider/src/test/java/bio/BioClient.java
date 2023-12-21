package bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BioClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket=new Socket();
        socket.connect(new InetSocketAddress(9090));
        OutputStream outputStream = socket.getOutputStream();
        while(true){
            outputStream.write("test12345678901234567890".getBytes());
            outputStream.flush();
            System.out.println("发送数据");
            Thread.sleep(1000);
        }
    }
}
