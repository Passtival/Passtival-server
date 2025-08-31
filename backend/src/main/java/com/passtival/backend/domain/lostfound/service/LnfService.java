package com.passtival.backend.domain.lostfound.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.passtival.backend.domain.lostfound.model.entity.FoundItem;
import com.passtival.backend.domain.lostfound.model.response.FoundItemResponse;
import com.passtival.backend.domain.lostfound.repository.LnfRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LnfService {

	private final LnfRepository lnfRepository;

	public FoundItemResponse getFoundItemById(Long id) {
		FoundItem foundItem = lnfRepository.findById(id)
			.orElseThrow(() -> new BaseException(BaseResponseStatus.FOUND_ITEM_NOT_FOUND));
		return FoundItemResponse.of(foundItem);
	}

	public List<FoundItemResponse> getAllFoundItems() {
		List<FoundItem> foundItems = lnfRepository.findAll();

		if (foundItems.isEmpty()) {
			throw new BaseException(BaseResponseStatus.FOUND_ITEM_NOT_FOUND);
		}

		return foundItems.stream()
			.map(FoundItemResponse::of)
			.toList();
	}
}
