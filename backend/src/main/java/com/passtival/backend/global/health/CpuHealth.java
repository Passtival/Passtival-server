package com.passtival.backend.global.health;

import java.lang.management.ManagementFactory;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import com.sun.management.OperatingSystemMXBean;

@Component("cpuHealth")
public class CpuHealth implements HealthIndicator {
	// 90% 이상이면 경고 필요에 따라 .env 참고하게 설정 고민 중
	private static final double MAX_LOAD = 0.90;

	@Override
	public Health health() {
		OperatingSystemMXBean os =
			(OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();

		// 0.0~1.0, 지원 안 하면 -1
		double load = os.getSystemCpuLoad();

		if (load < 0) {
			return Health.unknown().withDetail("reason", "unsupported").build();
		}
		// capacity 전용 'FATAL' 상태
		if (load >= MAX_LOAD) {
			return Health.status(new Status("FATAL"))
				.withDetail("systemCpuLoad", load)
				.build();
		}
		return Health.up().withDetail("systemCpuLoad", load).build();
	}
}