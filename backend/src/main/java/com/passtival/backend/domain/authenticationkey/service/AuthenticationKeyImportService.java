package com.passtival.backend.domain.authenticationkey.service;

import java.io.InputStream;
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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationKeyImportService {

	private final AuthenticationKeyRepository authenticationKeyRepository;
	private final ResourceLoader resourceLoader;

	@Value("${seed.auth-keys-path}")
	private String path;

	/**
	 * 인증 키를 엑셀에서 읽어와 DB에 저장합니다.
	 */
	@Transactional
	public void importXlsx() throws Exception {
		log.info("인증키 엑셀 불러오기 시작: {}", path);
		Resource resource = resourceLoader.getResource(path);
		if (!resource.exists()) {
			throw new IllegalArgumentException("엑셀 파일을 찾을 수 없습니다:" + path);
		}

		try (InputStream is = resource.getInputStream();
			 Workbook workbook = new XSSFWorkbook(is)) {

			Sheet sheet = workbook.getSheetAt(0);
			if (sheet == null) {
				throw new IllegalArgumentException("액셀 첫 번째 시트가 비어있습니다.");
			}

			List<AuthenticationKey> entities = new ArrayList<>();

			for (Row row : sheet) {
				Cell cell = row.getCell(0); // A열 기준
				if (cell == null)
					continue;

				cell.setCellType(CellType.STRING);
				String value = cell.getStringCellValue();
				if (value == null)
					continue;

				String key = value.trim();
				if (key.length() == 5) { // 인증키는 항상 길이 5
					entities.add(new AuthenticationKey(key, null));
				}
			}
			authenticationKeyRepository.saveAll(entities);
		}

	}
}
