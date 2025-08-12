package com.passtival.backend.global.handler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// 모든 예외를 처리하는 핸들러
	@ExceptionHandler(BaseException.class)
	public BaseResponse<?> handleBaseException(BaseException e) {
		log.error("BaseException 발생: status={}, message={}",
			e.getStatus(), e.getMessage());
		return BaseResponse.fail(e.getStatus(),e.getMessage());
	}

	// 404 에러 처리
	@ExceptionHandler(NoHandlerFoundException.class)
	public BaseResponse<?> handle404(NoHandlerFoundException e) {
		log.error("404 Not Found: {}", e.getRequestURL());
		return BaseResponse.fail(BaseResponseStatus.NOT_FOUND);
	}

	// @Valid 또는 @Validated 어노테이션으로 검증 실패 시 발생하는 예외 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public BaseResponse<?> handleValidationExceptions(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return BaseResponse.fail(BaseResponseStatus.INVALID_REQUEST,errors);
	}

}
