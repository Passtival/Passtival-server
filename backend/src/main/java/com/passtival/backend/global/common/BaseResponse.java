package com.passtival.backend.global.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {

	@JsonProperty("isSuccess")
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
		this.isSuccess = BaseResponseStatus.OK.isSuccess();
		this.code = BaseResponseStatus.OK.getCode();
		this.message = BaseResponseStatus.OK.getMessage();
		this.result = result;
	}

	// 요청에 실패한 경우
	private BaseResponse(BaseResponseStatus status) {
		this.isSuccess = status.isSuccess();
		this.code = status.getCode();
		this.message = status.getMessage();
	}

	// 요청에 실패한 경우 + response 담을 값이 있는 경우
	private BaseResponse(BaseResponseStatus status, T result) {
		this.isSuccess = status.isSuccess();
		this.code = status.getCode();
		this.message = status.getMessage();
		this.result =result;
	}

	// 커스텀 메세지 입력
	private BaseResponse(BaseResponseStatus status, String customMessage) {
		this.isSuccess = status.isSuccess();
		this.code = status.getCode();
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
	public static <T> BaseResponse<T> fail(BaseResponseStatus status){
		return new BaseResponse<>(status);
	}

	// 실패 응답 + 데이터
	public static <T> BaseResponse<T> fail(BaseResponseStatus status, T result){
		return new BaseResponse<>(status, result);
	}

	// 실패 응답 + 커스텀 메세지
	public static <T> BaseResponse<T> fail(BaseResponseStatus status, String customMessage) {
		return new BaseResponse<>(status, customMessage);
	}


}
