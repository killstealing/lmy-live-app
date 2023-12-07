package rockermq;

import com.alibaba.fastjson2.JSON;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProducer {
    private final static Logger logger= LoggerFactory.getLogger(MyProducer.class);

    public static void main(String[] args) {
        String topic="user-update-cache";


        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints("192.168.3.24:8081");
        ClientConfiguration configuration = builder.build();
        // 初始化Producer时需要设置通信配置以及预绑定的Topic。
        Producer producer = null;
        try {
            producer = provider.newProducerBuilder()
                    .setTopics(topic)
                    .setClientConfiguration(configuration)
                    .build();

            String tag = "yourMessageTagA";
            Message message = provider.newMessageBuilder()
                    // Set topic for the current message.
                    .setTopic(topic)
                    // Message secondary classifier of message besides topic.
                    .setTag(tag)
                    // Key(s) of the message, another way to mark message besides message id.
//                .setKeys("yourMessageKey-3ee439f945d7")
                    // Set expected delivery timestamp of message.
                    .setDeliveryTimestamp(1000)
                    .setBody("hello ".getBytes())
                    .build();
            try {
                final SendReceipt sendReceipt = producer.send(message);
                logger.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
            } catch (Throwable t) {
                logger.error("Failed to send message", t);
            }
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }


    }
}
