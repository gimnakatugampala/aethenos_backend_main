package lk.exon.aethenosapi.scheduler;

import lk.exon.aethenosapi.AethenosApiApplication;
import lk.exon.aethenosapi.utils.GenerateKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CreateKeyScheduler {

    @Scheduled(cron="0 0 0 * * ?")
    public void reportCurrentTime() throws Exception {
        log.info("Generate New Key");
        AethenosApiApplication.keys = new GenerateKey().generateKeys();
    }

}
