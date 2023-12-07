package rockermq;

import com.alibaba.fastjson2.JSON;
import com.lmy.live.user.dto.UserDTO;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class MyConsumer {

    private final static Logger logger= LoggerFactory.getLogger(MyConsumer.class);
    public static void main(String[] args) {
        final ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                .setEndpoints("192.168.3.24:8081")
                // On some Windows platforms, you may encounter SSL compatibility issues. Try turning off the SSL option in
                // client configuration to solve the problem please if SSL is not essential.
                .enableSsl(false)
//                    .setCredentialProvider(sessionCredentialsProvider)
                .build();
        String tag = "yourMessageTagA";
        FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);

        try {
            PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                    .setClientConfiguration(clientConfiguration)
                    // Set the consumer group name.
                    .setConsumerGroup("lmy-live-user-provider")
                    // Set the subscription for the consumer.
                    .setSubscriptionExpressions(Collections.singletonMap("user-update-cache", filterExpression))
                    .setMessageListener(messageView -> {
                        // Handle the received message and return consume result.
                        logger.info("Consume message={}", messageView);
                        String msgStr=messageView.getBody().toString();
                        logger.info("consumer msgStr {}",msgStr);
                        return ConsumeResult.SUCCESS;
                    })
                    .build();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }
}
