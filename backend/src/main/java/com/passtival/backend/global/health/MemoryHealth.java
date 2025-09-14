package com.passtival.backend.global.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

@Component("memoryHealth")
public class MemoryHealth implements HealthIndicator {
	// 예시: 사용 가능 메모리에 맞게 설정 필요 300MB 미만이면 FATAL
	private static final long MIN_FREE_MB = 300;

	@Override
	public Health health() {
		Runtime rt = Runtime.getRuntime();
		long freeMb = (rt.maxMemory() - (rt.totalMemory() - rt.freeMemory())) / (1024 * 1024);

		if (freeMb < MIN_FREE_MB) {
			return Health.status(new Status("FATAL"))
				.withDetail("freeMB", freeMb)
				.build();
		}
		return Health.up().withDetail("freeMB", freeMb).build();
	}
}