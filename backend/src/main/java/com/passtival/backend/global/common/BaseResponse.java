package com.passtival.backend.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.passtival.backend.global.exception.code.ErrorCode;

import lombok.AccessLevel;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {

	@JsonProperty("isSuccess")
	@Getter(AccessLevel.NONE) // isSuccess에 대한 getter 생성 방지
	private final boolean isSuccess;

	@JsonProperty("code")
	private final int code;

	@JsonProperty("message")
	private final String message;

	@JsonProperty("result")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	// 요청에 성공한 경우
	private BaseResponse(T result) {
		this.isSuccess = true;
		this.code = 200;
		this.message = "요청에 성공하였습니다.";
		this.result = result;
	}

	// 요청에 실패한 경우
	private BaseResponse(ErrorCode errorCode) {
		this.isSuccess = false;
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
	}

	// 요청에 실패한 경우 + response 담을 값이 있는 경우
	private BaseResponse(ErrorCode errorCode, T result) {
		this.isSuccess = false;
		this.code = errorCode.getCode();
		this.message = errorCode.getMessage();
		this.result =result;
	}

	// 커스텀 메세지 입력
	private BaseResponse(ErrorCode errorCode, String customMessage) {
		this.isSuccess = false;
		this.code = errorCode.getCode();
		this.message = customMessage;
	}

	/**
	 * 정적 헬퍼 메서드 추가
 	 */

	// 성공 응답
	public static <T> BaseResponse<T> success(T result) {
		return new BaseResponse<>(result);
	}

	// 실패 응답
	public static <T> BaseResponse<T> fail(ErrorCode errorCode){
		return new BaseResponse<>(errorCode);
	}

	// 실패 응답 + 데이터
	public static <T> BaseResponse<T> fail(ErrorCode errorCode, T result){
		return new BaseResponse<>(errorCode, result);
	}

	// 실패 응답 + 커스텀 메세지
	public static <T> BaseResponse<T> fail(ErrorCode errorCode, String customMessage) {
		return new BaseResponse<>(errorCode, customMessage);
	}


}
