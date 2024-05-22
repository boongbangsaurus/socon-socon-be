package site.soconsocon.socon.store.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.soconsocon.socon.store.service.SoconService;
import site.soconsocon.socon.store.service.StoreService;

@Slf4j
@RequiredArgsConstructor
@Component
public class StoreScheduler {

    private final StoreService storeService;
    private final SoconService soconService;


    // 가게 폐업 상태 업데이트
    @Scheduled( cron = "0 0 0 * * *")
    public void updateCloseStore() {
        storeService.updateCloseStore();
    }

}
