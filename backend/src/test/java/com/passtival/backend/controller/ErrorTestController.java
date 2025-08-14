package com.passtival.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.passtival.backend.global.common.BaseResponse;

@RestController
@RequestMapping("/api/test/error")
public class ErrorTestController {

	@GetMapping("/500")
	public BaseResponse<?> test500Error() {
		throw new RuntimeException("테스트용 500 에러 발생");
	}

	@GetMapping("/null-pointer")
	public BaseResponse<?> testNullPointer() {
		String nullString = null;
		return BaseResponse.success(nullString.length()); // NullPointerException 발생
	}

	@GetMapping("/array-index")
	public BaseResponse<?> testArrayIndex() {
		int[] array = new int[3];
		return BaseResponse.success(array[10]); // ArrayIndexOutOfBoundsException 발생
	}

	@GetMapping("/arithmetic")
	public BaseResponse<?> testArithmetic() {
		int result = 10 / 0; // ArithmeticException 발생
		return BaseResponse.success(result);
	}

	@GetMapping("/custom/{message}")
	public BaseResponse<?> testCustomError(@PathVariable String message) {
		throw new IllegalArgumentException("커스텀 에러: " + message);
	}
}
