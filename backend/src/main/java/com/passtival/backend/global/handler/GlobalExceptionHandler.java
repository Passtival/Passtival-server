package com.passtival.backend.global.handler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.passtival.backend.global.common.BaseResponse;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.discord.DiscordService;
import com.passtival.backend.global.exception.BaseException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final DiscordService discordService;

	// 의도된 비즈니스 예외(개발자가 기대한 흐름) -> 4xx, Discord 알림 전송 안함
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<BaseResponse<?>> handleBaseException(BaseException e) {
		String message = (e.getMessage() != null && !e.getMessage().isBlank())
			? e.getMessage()
			: e.getStatus().getMessage();
		log.warn("BaseException 발생: status={}, message={}", e.getStatus(), message);
		return ResponseEntity.status(HttpStatusCode.valueOf(e.getStatus().getCode()))
			.body(BaseResponse.fail(e.getStatus(), message));
	}

	// @Valid 검증 실패 -> 400, Discord 알림 전송 안함
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<BaseResponse<?>> handleValidationException(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		e.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		log.warn("요청 파라미터 검증 실패: {}", errors);
		return ResponseEntity.status(HttpStatusCode.valueOf(BaseResponseStatus.INVALID_REQUEST.getCode()))
			.body(BaseResponse.fail(BaseResponseStatus.INVALID_REQUEST, errors));
	}

	// MVC에서 던지는 잘못된 요청 처리 -> 400, Discord 알림 전송 안함
	@ExceptionHandler({
		HttpMessageNotReadableException.class,
		MethodArgumentTypeMismatchException.class,
		MissingServletRequestParameterException.class,
		ConstraintViolationException.class,
		TypeMismatchException.class,
		BindException.class
	})
	public ResponseEntity<BaseResponse<?>> handleBadRequest(Exception e) {
		log.warn("잘못된 요청 처리: {}", e.getClass().getSimpleName(), e.getMessage());
		return ResponseEntity.status(HttpStatusCode.valueOf(BaseResponseStatus.INVALID_REQUEST.getCode()))
			.body(BaseResponse.fail(BaseResponseStatus.INVALID_REQUEST));
	}

	// 404 에러 처리 ->  Discord 전송 안함
	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<BaseResponse<?>> handle404(NoHandlerFoundException e) {
		log.warn("404 Not Found: {}", e.getRequestURL());
		return ResponseEntity.status(HttpStatusCode.valueOf(BaseResponseStatus.NOT_FOUND.getCode()))
			.body(BaseResponse.fail(BaseResponseStatus.NOT_FOUND));
	}

	// 그 외 모든 예상치 못한 예외 -> 500, Discord 알림 전송
	@ExceptionHandler(Exception.class)
	public ResponseEntity<BaseResponse<?>> handleInternalServerError(Exception e, HttpServletRequest request) {
		String msg = (e.getMessage() != null && !e.getMessage().isBlank())
			? e.getMessage()
			: e.getClass().getName();
		log.error("500 서버 내부 오류 발생: {}", msg, e);

		// Discord 에러 알림 전송
		String stackTrace = getStackTrace(e);
		discordService.sendErrorNotification(msg, request.getRequestURL().toString(), stackTrace);

		return ResponseEntity.status(HttpStatusCode.valueOf(BaseResponseStatus.INTERNAL_SERVER_ERROR.getCode()))
			.body(BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR));
	}

	private String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}
