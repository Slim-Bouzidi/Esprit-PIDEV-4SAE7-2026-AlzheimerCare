package assistancequotidienne2.assistancequotidienne2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AssistanceQuotidienne2Application {

    public static void main(String[] args) {
        SpringApplication.run(AssistanceQuotidienne2Application.class, args);
    }

}
