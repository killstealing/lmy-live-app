package dubbo;

import com.lmy.live.user.interfaces.IUserRpc;
import com.lmy.live.user.provider.rpc.UserRpcImpl;
import org.apache.dubbo.config.*;

public class DubboTest {

    private static final String REGISTER_ADDRESS = "nacos://127.0.0.1:8848?namespace=lmy-live-test&&username=lmy&&password=lmy";
    private static RegistryConfig registryConfig;
    private static ApplicationConfig applicationConfig;
    private IUserRpc userRpc;

    public static void initConfig() {
        registryConfig = new RegistryConfig();
        applicationConfig = new ApplicationConfig();
        registryConfig.setAddress(REGISTER_ADDRESS);
        applicationConfig.setName("dubbo-test-application");
        applicationConfig.setRegistry(registryConfig);
    }

    public void initProvider() {
        ProtocolConfig dubboProtocolConfig = new ProtocolConfig();
        dubboProtocolConfig.setPort(9090);
        dubboProtocolConfig.setName("dubbo");
        ServiceConfig<IUserRpc> serviceConfig = new ServiceConfig<>();
        serviceConfig.setInterface(IUserRpc.class);
        serviceConfig.setProtocol(dubboProtocolConfig);
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setRef(new UserRpcImpl());
        //核心
        serviceConfig.export();
        System.out.println("服务暴露");
    }

    public void initConsumer() {
        ReferenceConfig<IUserRpc> userRpcReferenceConfig = new ReferenceConfig<>();
        userRpcReferenceConfig.setApplication(applicationConfig);
        userRpcReferenceConfig.setRegistry(registryConfig);
        userRpcReferenceConfig.setLoadbalance("random");
        userRpcReferenceConfig.setInterface(IUserRpc.class);
        userRpc = userRpcReferenceConfig.get();
    }

    public static void main(String[] args) throws InterruptedException {
        initConfig();
        DubboTest dubboTest = new DubboTest();
        dubboTest.initProvider();
        dubboTest.initConsumer();
        for (;;){
            Thread.sleep(3000);
        }
    }
}






