package purureum.pudongpudong.infrastructure.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import purureum.pudongpudong.application.command.ParkCommandService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ParkSyncJob {
	
	private final ParkCommandService parkCommandService;
	
	@PostConstruct
	@Scheduled(cron = "0 0 3 1 * *")
	public void initParksData() {
		parkCommandService.saveAllParks()
				.doOnComplete(() -> log.info("공원 데이터 배치 작업 완료"))
				.doOnError(throwable -> log.error("배치 작업 중 에러 발생: {}", throwable.getMessage())) // 에러 핸들러 추가
				.subscribe();
	}
}
