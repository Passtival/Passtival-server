package com.passtival.backend.domain.authenticationkey.service;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
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
	private final MeterRegistry meterRegistry;

	@Value("${seed.auth-keys-path}")
	private String path;

	private static final int MAX_ROWS = 10_000; // 기본 import 시 최대 10,000행

	private final AtomicInteger inProgressImports = new AtomicInteger(0);
	private final AtomicLong lastRequestedRows = new AtomicLong(0);
	private final AtomicLong lastReadRows = new AtomicLong(0);
	private final AtomicLong lastInsertedRows = new AtomicLong(0);

	@PostConstruct
	void registerImportGauges() {
		Gauge.builder("authkey.import.in.progress", inProgressImports, AtomicInteger::get)
			.description("Current number of authentication-key imports in progress")
			.register(meterRegistry);
		Gauge.builder("authkey.import.last.requested.rows", lastRequestedRows, AtomicLong::get)
			.description("Requested rows from the most recent authentication-key import")
			.register(meterRegistry);
		Gauge.builder("authkey.import.last.read.rows", lastReadRows, AtomicLong::get)
			.description("Read rows from the most recent authentication-key import")
			.register(meterRegistry);
		Gauge.builder("authkey.import.last.inserted.rows", lastInsertedRows, AtomicLong::get)
			.description("Inserted rows from the most recent authentication-key import")
			.register(meterRegistry);
	}

	/**
	 * 인증 키를 엑셀에서 읽어와 DB에 저장합니다.
	 */
	@Transactional
	public void importXlsx() throws Exception {
		importXlsx(MAX_ROWS);
	}

	@Transactional
	public void importXlsx(int requestedRows) throws Exception {
		if (requestedRows < 1) {
			throw new IllegalArgumentException("요청 row 수는 1 이상이어야 합니다.");
		}

		meterRegistry.counter("authkey.import.requests").increment();
		lastRequestedRows.set(requestedRows);
		inProgressImports.incrementAndGet();

		// 전체 배치 작업의 끝-끝 소요시간 측정 시작
		long importStartNanos = System.nanoTime();
		log.info("인증키 엑셀 불러오기 시작: {}, requestedRows: {}", path, requestedRows);
		logThreadStats("start");
		logHikariPoolStats("start");

		try {
			Resource resource = resourceLoader.getResource(path);
			if (!resource.exists()) {
				throw new IllegalArgumentException("엑셀 파일을 찾을 수 없습니다:" + path);
			}

			long parseStartNanos = System.nanoTime();
			int totalRows = 0;
			int emptyCellRows = 0;
			int nullValueRows = 0;
			int invalidLengthRows = 0;

			try (InputStream is = resource.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
				Sheet sheet = workbook.getSheetAt(0);
				if (sheet == null) {
					throw new IllegalArgumentException("액셀 첫 번째 시트가 비어있습니다.");
				}

				List<AuthenticationKey> entities = new ArrayList<>();
				int sourceRowCount = sheet.getLastRowNum() + 1;
				int readLimit = Math.min(sourceRowCount, requestedRows);

				for (int rowIndex = 0; rowIndex < readLimit; rowIndex++) {
					Row row = sheet.getRow(rowIndex);
					if (row == null) {
						totalRows++;
						emptyCellRows++;
						continue;
					}

					totalRows++;
					Cell cell = row.getCell(0);
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
					if (key.length() == 5) {
						entities.add(new AuthenticationKey(key, null));
					} else {
						invalidLengthRows++;
					}
				}

				long parseElapsedNanos = System.nanoTime() - parseStartNanos;
				long parseElapsedMs = TimeUnit.NANOSECONDS.toMillis(parseElapsedNanos);
				meterRegistry.timer("authkey.import.duration.parse").record(parseElapsedNanos, TimeUnit.NANOSECONDS);

				meterRegistry.counter("authkey.import.rows.read").increment(totalRows);
				meterRegistry.counter("authkey.import.rows.empty_cell").increment(emptyCellRows);
				meterRegistry.counter("authkey.import.rows.null_value").increment(nullValueRows);
				meterRegistry.counter("authkey.import.rows.invalid_length").increment(invalidLengthRows);
				lastReadRows.set(totalRows);

				log.info(
					"인증키 파싱 완료 - sourceRowCount: {}, requestedRows: {}, readLimit: {}, totalRows(read): {}, validKeys: {}, emptyCellRows: {}, nullValueRows: {}, invalidLengthRows: {}, parseElapsedMs: {}",
					sourceRowCount,
					requestedRows,
					readLimit,
					totalRows,
					entities.size(),
					emptyCellRows,
					nullValueRows,
					invalidLengthRows,
					parseElapsedMs
				);

				long saveStartNanos = System.nanoTime();
				authenticationKeyRepository.saveAll(entities);
				authenticationKeyRepository.flush();
				long saveElapsedNanos = System.nanoTime() - saveStartNanos;
				long saveElapsedMs = TimeUnit.NANOSECONDS.toMillis(saveElapsedNanos);
				meterRegistry.timer("authkey.import.duration.save").record(saveElapsedNanos, TimeUnit.NANOSECONDS);

				meterRegistry.counter("authkey.import.rows.inserted").increment(entities.size());
				lastInsertedRows.set(entities.size());

				log.info("인증키 DB 저장 완료 - insertCount: {}, saveElapsedMs: {}", entities.size(), saveElapsedMs);
			}

			meterRegistry.counter("authkey.import.success").increment();
		} catch (Exception e) {
			meterRegistry.counter("authkey.import.failures").increment();
			throw e;
		} finally {
			long totalElapsedNanos = System.nanoTime() - importStartNanos;
			long totalElapsedMs = TimeUnit.NANOSECONDS.toMillis(totalElapsedNanos);
			meterRegistry.timer("authkey.import.duration.total").record(totalElapsedNanos, TimeUnit.NANOSECONDS);

			logHikariPoolStats("end");
			logThreadStats("end");
			log.info("인증키 import 전체 완료 - totalElapsedMs: {}", totalElapsedMs);
			inProgressImports.decrementAndGet();
		}
	}

	private void logThreadStats(String phase) {
		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		log.info(
			"[{}] JVM Thread Stats - live: {}, peak: {}, daemon: {}, currentThread: {}",
			phase,
			threadBean.getThreadCount(),
			threadBean.getPeakThreadCount(),
			threadBean.getDaemonThreadCount(),
			Thread.currentThread().getName()
		);
	}

	private void logHikariPoolStats(String phase) {
		if (!(dataSource instanceof HikariDataSource hikariDataSource)) {
			log.info(
				"[{}] Hikari Pool Stats - datasource is not HikariDataSource ({})",
				phase,
				dataSource.getClass().getName()
			);
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
