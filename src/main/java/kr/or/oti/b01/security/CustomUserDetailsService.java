package kr.or.oti.b01.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.or.oti.b01.domain.Member;
import kr.or.oti.b01.repository.MemberRepository;
import kr.or.oti.b01.security.dto.MemberDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//username에 해당하는 상세정보 리턴해주는 메소드
		log.info("loadUserByUsername : "+username);
		//이부분 나중에 DB로 변환
//		UserDetails user = User.builder()
//				.username(username)
//				.password("$2a$10$jxmrN4ee0jciUD/1cStDO./fYr1B3tI.nFPM5BPRRhqlEr9ioWBjG") // 암호화된 비밀번호
//				.authorities("ROLE_USER")
//				.build();
		// role포함하여 member객체 넘기기
		Member member = memberRepository.getWithRoles(username)
				.orElseThrow(() -> new UsernameNotFoundException(username+"사용자가 존재하지 않습니다."));
		MemberDTO memberDTO = new MemberDTO(member.getMid(), member.getMpw(), member.getEmail(), member.isDel(), member.isAccountNonLocked(), member.getPasswordChangedDate(), member.getRoleSet());
		
		//설정된 유효기간 전달
		memberDTO.setExpiredDate(member.getExpiredDate());
		// 비밀번호 변경일 전달
		memberDTO.setPasswordChangedDate(
				member.getPasswordChangedDate()
		);
		
		return memberDTO;
	}

}
