package com.passtival.backend.domain.admin.service;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.admin.model.request.FoundItemRequest;
import com.passtival.backend.domain.lostfound.model.entity.FoundItem;
import com.passtival.backend.domain.lostfound.repository.LnfRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminLnfService {

	private final LnfRepository lnfRepository;

	@Transactional
	public void createFoundItem(FoundItemRequest request) {
		FoundItem foundItem = FoundItem.builder()
			.title(request.getTitle())
			.area(request.getArea())
			.foundDateTime(request.getFoundDateTime())
			.imagePath(request.getImagePath())
			.build();

		lnfRepository.save(foundItem);
	}

	@Transactional
	public void deleteFoundItem(Long id) {

		if (!lnfRepository.existsById(id)) {
			throw new BaseException(BaseResponseStatus.FOUND_ITEM_NOT_FOUND);
		}

		lnfRepository.deleteById(id);
	}
}
