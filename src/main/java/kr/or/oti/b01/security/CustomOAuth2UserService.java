package kr.or.oti.b01.security;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import kr.or.oti.b01.domain.Member;
import kr.or.oti.b01.domain.MemberRole;
import kr.or.oti.b01.repository.MemberRepository;
import kr.or.oti.b01.security.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		log.info("userRequest = " + userRequest);
		// 등록된 클라이언트 정보
		ClientRegistration clientRegistration = userRequest.getClientRegistration();
		log.info("clientRegistration = " + clientRegistration);
		String clientName = clientRegistration.getClientName();
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String email = "";
		Map<String, Object> paramMap = oAuth2User.getAttributes();
		switch (clientName) {
		case "kakao":
			email = getKakaoEmail(paramMap);
			break;
		}
		log.info("email = " + email);
		return generateDTO(email, paramMap);
	}

	private OAuth2User generateDTO(String email, Map<String, Object> paramMap) {
		// 1. email을 이용하여 Mapper 객체를 얻는다.
		Optional<Member> result = memberRepository.findByEmail(email);
		Member member = null;
		if (result.isEmpty()) {
			// 2. Member 객체가 존재하지 않을 경우
				// 2-1. email을 이용하여 회원가입을 한다.
			member = Member.builder()
					.mid(email)
					.email(email)
					.social(true)
					.del(false)
					.failCount(0)
					.passwordChangedDate(LocalDate.now())
					.accountNonLocked(true)
					.build();
			member.addRole(MemberRole.USER);
			memberRepository.save(member);
					
		}else {
			// 3. Member 객체가 존재할 경우
			member = result.get();
		}
		
		// 4. Member를 이용하여 MEmberDTO 객체를 생성하여 리턴한다.
		return entityToDTO(member, paramMap);
	}
	// Member 엔티티를 로그인 사용자 정보로 변환
    private MemberDTO entityToDTO(Member member, Map<String,Object> paramMap) {
        MemberDTO result = new MemberDTO(
                member.getMid(),
                member.getMpw(),
                member.getEmail(),
                member.isDel(),
                member.isAccountNonLocked(),
                member.getPasswordChangedDate(),
                member.getRoleSet()
        );
        result.setProps(paramMap);
        return result;
    }

	private String getKakaoEmail(Map<String, Object> paramMap) {
		Object value = paramMap.get("kakao_account");
		if (value != null) {
			LinkedHashMap<String, Object> accountMap = (LinkedHashMap<String, Object>) value;
			return (String) accountMap.get("email");
		}

		return null;
	}
}
