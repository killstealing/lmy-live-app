package imclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImClientApplication1 {
    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(ImClientApplication1.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
