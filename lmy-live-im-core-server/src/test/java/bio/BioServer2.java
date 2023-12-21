package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class BioServer2 {
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 10, 3,
            TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("commonAsyncPool - " + ThreadLocalRandom.current().nextInt(10000));
            return thread;
        }
    });

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(9090));
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                threadPoolExecutor.execute(() -> {
                    while(true){
                        InputStream inputStream = null;
                        try {
                            inputStream = socket.getInputStream();
                            byte[] bytes = new byte[10];
                            //阻塞调用
                            inputStream.read(bytes);
                            System.out.println("服务端收到的数据是： " + new String(bytes));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
