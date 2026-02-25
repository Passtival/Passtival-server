package com.passtival.backend.domain.authenticationkey.service;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.passtival.backend.domain.authenticationkey.model.AuthenticationKey;
import com.passtival.backend.domain.authenticationkey.repository.AuthenticationKeyRepository;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationKeyImportService {

	private final AuthenticationKeyRepository authenticationKeyRepository;
	private final ResourceLoader resourceLoader;
	private final DataSource dataSource;

	@Value("${seed.auth-keys-path}")
	private String path;

	/**
	 * 인증 키를 엑셀에서 읽어와 DB에 저장합니다.
	 */
	@Transactional
	public void importXlsx() throws Exception {

		// 전체 배치 작업의 끝-끝 소요시간 측정 시작
		long importStartNanos = System.nanoTime(); // 배치 시작 시간 (나노초 단위)
		log.info("인증키 엑셀 불러오기 시작: {}", path);
		logThreadStats("start"); // 배치 시작 시점의 JVM 스레드 상태 로깅
		logHikariPoolStats("start"); // 배치 시작 시점의 HikariCP 커넥션 풀 상태 로깅

		Resource resource = resourceLoader.getResource(path);
		if (!resource.exists()) {
			throw new IllegalArgumentException("엑셀 파일을 찾을 수 없습니다:" + path);
		}

		// 1) 엑셀 파싱 구간 시간 측정
		long parseStartNanos = System.nanoTime();
		int totalRows = 0;
		int emptyCellRows = 0;
		int nullValueRows = 0;
		int invalidLengthRows = 0;
		try (InputStream is = resource.getInputStream();
			 Workbook workbook = new XSSFWorkbook(is)) {

			Sheet sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				throw new IllegalArgumentException("액셀 첫 번째 시트가 비어있습니다.");
			}

			List<AuthenticationKey> entities = new ArrayList<>();

			for (Row row : sheet) {
				totalRows++;
				Cell cell = row.getCell(0); // A열 기준
				if (cell == null) {
					emptyCellRows++;
					continue;
				}

				cell.setCellType(CellType.STRING);
				String value = cell.getStringCellValue();
				if (value == null) {
					nullValueRows++;
					continue;
				}

				String key = value.trim();
				if (key.length() == 5) { // 인증키는 항상 길이 5
					entities.add(new AuthenticationKey(key, null));
				} else {
					invalidLengthRows++;
				}
			}

			long parseElapsedMs = elapsedMillis(parseStartNanos);
			log.info(
				"인증키 파싱 완료 - totalRows: {}, validKeys: {}, emptyCellRows: {}, nullValueRows: {}, invalidLengthRows: {}, parseElapsedMs: {}",
				totalRows, // 전체 행 수
				entities.size(), // 유효한 인증키 수
				emptyCellRows, // 빈 셀이었던 행 수
				nullValueRows, // null 값이었던 행 수
				invalidLengthRows, // 길이 5가 아닌 행 수
				parseElapsedMs // 파싱 구간 소요시간 (밀리초 단위)
			);

			// 2) DB 저장 구간 시간 측정 (flush 포함)
			long saveStartNanos = System.nanoTime();
			authenticationKeyRepository.saveAll(entities);
			authenticationKeyRepository.flush();
			long saveElapsedMs = elapsedMillis(saveStartNanos);
			log.info("인증키 DB 저장 완료 - insertCount: {}, saveElapsedMs: {}", entities.size(), saveElapsedMs);
		}

		long totalElapsedMs = elapsedMillis(importStartNanos);
		logHikariPoolStats("end");
		logThreadStats("end");
		log.info("인증키 import 전체 완료 - totalElapsedMs: {}", totalElapsedMs);
	}

	private long elapsedMillis(long startNanos) {
		return (System.nanoTime() - startNanos) / 1_000_000;
	}

	private void logThreadStats(String phase) {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		log.info(
			"[{}] JVM Thread Stats - live: {}, peak: {}, daemon: {}, currentThread: {}",
			phase,
			threadBean.getThreadCount(), // 현재 live 스레드 수
			threadBean.getPeakThreadCount(), // 배치 시작 이후 JVM이 기록한 peak 스레드 수
			threadBean.getDaemonThreadCount(), // 현재 데몬 스레드 수
			Thread.currentThread().getName() // 현재 배치 작업을 수행하는 스레드 이름 (보통 "main")
		);
	}

	private void logHikariPoolStats(String phase) {
		if (!(dataSource instanceof HikariDataSource hikariDataSource)) {
			log.info("[{}] Hikari Pool Stats - datasource is not HikariDataSource ({})", phase,
				dataSource.getClass().getName()); //
			return;
		}

		HikariPoolMXBean poolMxBean = hikariDataSource.getHikariPoolMXBean();
		if (poolMxBean == null) {
			log.info("[{}] Hikari Pool Stats - pool MXBean is not initialized yet", phase);
			return;
		}

		log.info(
			"[{}] Hikari Pool Stats - active: {}, idle: {}, total: {}, pending: {}",
			phase,
			poolMxBean.getActiveConnections(),
			poolMxBean.getIdleConnections(),
			poolMxBean.getTotalConnections(),
			poolMxBean.getThreadsAwaitingConnection()
		);
	}
}
