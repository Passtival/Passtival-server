package com.passtival.backend.global.security.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.passtival.backend.domain.member.model.entity.Member;
import com.passtival.backend.domain.member.repository.MemberRepository;
import com.passtival.backend.global.common.BaseResponseStatus;
import com.passtival.backend.global.exception.BaseException;
import com.passtival.backend.global.security.model.AuthUserDto;
import com.passtival.backend.global.security.model.CustomOAuth2User;
import com.passtival.backend.global.security.model.KakaoResponse;
import com.passtival.backend.global.security.model.Oauth2Response;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberRepository memberRepository;

	/**
	 * OAuth2 로그인 사용자 정보 처리 (Spring Security 표준 인터페이스)
	 * @param userRequest OAuth2 사용자 요청 정보
	 * @return OAuth2User 사용자 인증 정보
	 * @throws OAuth2AuthenticationException OAuth2 인증 처리 실패 시
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		Oauth2Response oAuth2Response = null;

		try {
			// 1. 상위 클래스에서 OAuth2User 정보 가져오기
			OAuth2User oAuth2User = super.loadUser(userRequest);

			// 2. OAuth2 제공자별 응답 파싱
			oAuth2Response = parseOAuth2Response(userRequest, oAuth2User);

			// 3. 소셜 ID 검증
			validateSocialId(oAuth2Response);

			// 4. 회원 정보 처리 (신규 생성 또는 기존 조회)
			Member member = processUserMembership(oAuth2Response);

			// 5. CustomOAuth2User 생성 및 반환
			return createCustomOAuth2User(member, oAuth2Response);

		} catch (OAuth2AuthenticationException e) {
			throw e;
		} catch (Exception e) {
			OAuth2Error error = new OAuth2Error(
				"oauth2_processing_error",
				"OAuth2 로그인 처리 중 오류가 발생했습니다",
				null
			);
			throw new OAuth2AuthenticationException(error, e);
		}
	}

	/**
	 * OAuth2 제공자별 응답 파싱
	 * @param userRequest OAuth2 사용자 요청
	 * @param oAuth2User OAuth2 사용자 정보
	 * @return OAuth2Response 파싱된 응답 정보
	 * @throws OAuth2AuthenticationException 지원하지 않는 제공자인 경우
	 */
	private Oauth2Response parseOAuth2Response(OAuth2UserRequest userRequest, OAuth2User oAuth2User)
		throws OAuth2AuthenticationException {
		try {

			String registrationId = userRequest.getClientRegistration().getRegistrationId();

			if ("kakao".equals(registrationId)) {

				return new KakaoResponse(oAuth2User.getAttributes());

			} else {

				BaseException cause = new BaseException(BaseResponseStatus.UNSUPPORTED_PROVIDER);

				OAuth2Error error = new OAuth2Error(
					"unsupported_provider",
					cause.getMessage(),
					null);

				throw new OAuth2AuthenticationException(error, cause);
			}
		} catch (Exception e) {

			BaseException cause = new BaseException(BaseResponseStatus.OAUTH2_PROCESSING_ERROR);

			log.error("parseOAuth2Response 중 예외 발생", e);

			OAuth2Error error = new OAuth2Error(
				"oauth2_parsing_error",
				cause.getMessage(),
				null);

			throw new OAuth2AuthenticationException(error, cause);
		}
	}

	/**
	 * 소셜 ID 유효성 검증
	 * @param oAuth2Response OAuth2 응답 정보
	 * @throws OAuth2AuthenticationException 소셜 ID가 유효하지 않은 경우
	 */
	private void validateSocialId(Oauth2Response oAuth2Response) throws OAuth2AuthenticationException {
		try {

			String socialId = oAuth2Response.getSocialId();

			if (socialId == null || socialId.trim().isEmpty()) {
				log.warn("소셜 ID를 가져올 수 없음: provider={}", oAuth2Response.getProvider());

				BaseException cause = new BaseException(BaseResponseStatus.SOCIAL_ID_NOTFOUND);

				OAuth2Error error = new OAuth2Error(
					"invalid_social_id",
					cause.getMessage(),
					null
				);

				throw new OAuth2AuthenticationException(error, cause);
			}
		} catch (OAuth2AuthenticationException e) {
			throw e;
		} catch (Exception e) {

			log.error("validateSocialId 중 예외 발생", e);

			BaseException cause = new BaseException(BaseResponseStatus.SOCIAL_ID_VERIFICATION_FAILED);

			OAuth2Error error = new OAuth2Error(
				"social_id_validation_error",
				cause.getMessage(),
				null
			);

			throw new OAuth2AuthenticationException(error, cause);
		}
	}

	/**
	 * 회원 정보 처리 (신규 생성 또는 기존 조회)
	 * @param oAuth2Response OAuth2 응답 정보
	 * @return Member 회원 정보
	 * @throws OAuth2AuthenticationException 회원 처리 실패 시
	 */
	private Member processUserMembership(Oauth2Response oAuth2Response) throws
		OAuth2AuthenticationException {
		try {

			String socialId = oAuth2Response.getSocialId();
			Optional<Member> existingMember = memberRepository.findBySocialId(socialId);

			if (existingMember.isEmpty()) {
				// 신규 회원 생성
				return createNewMember(oAuth2Response);
			} else {
				// 기존 회원 조회
				Member member = existingMember.get();
				return member;
			}

		} catch (OAuth2AuthenticationException e) {
			throw e;
		} catch (Exception e) {

			log.error("회원 정보 처리 중 예외 발생, socialId: {}", oAuth2Response.getSocialId(), e);

			BaseException cause = new BaseException(BaseResponseStatus.MEMBER_PROCESSING_ERROR);

			OAuth2Error error = new OAuth2Error(
				"member_processing_error",
				cause.getMessage(), // 메시지도 cause의 것으로 통일
				null
			);

			throw new OAuth2AuthenticationException(error, cause);
		}
	}

	/**
	 * 신규 회원 생성
	 * @param oAuth2Response OAuth2 응답 정보
	 * @return Member 생성된 회원 정보
	 * @throws OAuth2AuthenticationException 회원 생성 실패 시
	 */
	private Member createNewMember(Oauth2Response oAuth2Response) throws OAuth2AuthenticationException {
		try {

			Member newMember = Member.createSocialMember(
				oAuth2Response.getSocialId(),
				oAuth2Response.getName()
			);

			Member savedMember = memberRepository.save(newMember);

			return savedMember;

		} catch (Exception e) {

			BaseException cause = new BaseException(BaseResponseStatus.NEW_MEMBER_PROCESSING_ERROR);

			log.error("createNewMember 중 예외 발생, socialId: {}", oAuth2Response.getSocialId(), e);

			OAuth2Error error = new OAuth2Error(
				"member_creation_error",
				cause.getMessage(),
				null
			);
			throw new OAuth2AuthenticationException(error, cause);
		}
	}

	/**
	 * CustomOAuth2User 생성
	 * @param member 회원 정보
	 * @param oAuth2Response OAuth2 응답 정보 (신규 회원용)
	 * @return CustomOAuth2User 사용자 인증 정보
	 * @throws OAuth2AuthenticationException 생성 실패 시
	 */
	private CustomOAuth2User createCustomOAuth2User(Member member, Oauth2Response oAuth2Response)
		throws OAuth2AuthenticationException {
		try {
			AuthUserDto authUserDto = AuthUserDto.builder()
				.userId(member.getMemberId())
				.socialId(member.getSocialId())
				.name(member.getName())
				.role("ROLE_" + member.getRole().name())
				.build();

			return new CustomOAuth2User(authUserDto);

		} catch (Exception e) {
			BaseException cause = new BaseException(BaseResponseStatus.MEMBER_DETAILS_CREATION_ERROR);

			log.error("createCustomOAuth2User 중 오류가 발생했습니다, socialId: {}", oAuth2Response.getSocialId(), e);

			OAuth2Error error = new OAuth2Error(
				"oauth2_user_creation_error",
				cause.getMessage(),
				null
			);
			throw new OAuth2AuthenticationException(error, cause);
		}
	}
}