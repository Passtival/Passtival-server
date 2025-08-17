package com.passtival.backend.domain.lostfound.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.lostfound.model.entity.FoundItem;
import com.passtival.backend.domain.lostfound.model.request.FoundItemRequest;
import com.passtival.backend.domain.lostfound.repository.LnfRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;
import com.passtival.backend.global.s3.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LnfService {

	private final LnfRepository lnfRepository;
	private final S3Service s3Service;

	public void createFoundItem(FoundItemRequest request) throws BaseException {
		// FoundItem 엔티티 생성
		try {
			FoundItem foundItem = FoundItem.builder()
				.title(request.getTitle())
				.area(request.getArea())
				.foundDateTime(request.getFoundDateTime())
				.imagePath(request.getImagePath())
				.build();
			lnfRepository.save(foundItem);
		} catch (Exception e) {
			throw new BaseException(BaseResponseStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public String getUploadUrl(String fileName) {
		return s3Service.generatePresignedUrl(fileName);
	}
}
