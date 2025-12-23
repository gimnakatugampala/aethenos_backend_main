package lk.exon.aethenosapi;

import lk.exon.aethenosapi.model.GenerateKeys;
import lk.exon.aethenosapi.payload.response.GeneralUserProfileResponse;
import lk.exon.aethenosapi.utils.GenerateKey;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
@Slf4j
@EnableScheduling
public class AethenosApiApplication {

    public static GenerateKeys keys;

    public static void main(String[] args) {
        SpringApplication.run(AethenosApiApplication.class, args);
    }

    @Bean
    public GeneralUserProfileResponse generalUserProfileResponse() {
        return new GeneralUserProfileResponse();
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            log.info("Generate New Security Keys");
            keys = new GenerateKey().generateKeys();
        };
    }
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
