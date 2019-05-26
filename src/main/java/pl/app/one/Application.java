package pl.app.one;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import pl.app.one.config.BanditSimulationProperties;

import java.io.IOException;
import java.io.InputStream;

@SpringBootApplication
@Slf4j
public class Application extends SpringBootServletInitializer {
    public static void main(String... ar){
        SpringApplication.run(Application.class,ar);
    }

    @Bean
    public BanditSimulationProperties banditSimulationProperties() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        try(InputStream inputStream = this.getClass().getResourceAsStream("/configuration.json")) {
            return objectMapper.readValue(inputStream,BanditSimulationProperties.class);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return BanditSimulationProperties.builder().build();
    }
}
