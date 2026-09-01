package kr.or.oti.b01.security.dto;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import kr.or.oti.b01.domain.MemberRole;
import lombok.Data;

@Data
public class MemberDTO implements UserDetails, OAuth2User {
	
	private String mid;
	private String mpw;
	private String email;
	private boolean del;
	private boolean accountNonLocked;
	private LocalDate expiredDate;
	private LocalDate passwordChangedDate;
	
	private boolean social;
	private Map<String, Object> props; //소셜 로그인 정보
	
	private Set<MemberRole> roleSet;

	public MemberDTO(String mid, String mpw, String email, boolean del, boolean accountNonLocked, LocalDate passwordChangedDate, Set<MemberRole> roleSet) {
		super();
		this.mid = mid;
		this.mpw = mpw;
		this.email = email;
		this.del = del;
		this.accountNonLocked = accountNonLocked;
		this.passwordChangedDate = passwordChangedDate;
		this.roleSet = roleSet;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if(this.roleSet != null) {
			return this.roleSet.stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_"+memberRole.name()))
						.collect(Collectors.toList());
		}
		return null;
	}

	@Override
	public String getPassword() {
		return mpw;
	}

	@Override
	public String getUsername() {
		return mid;
	}

	@Override
	public boolean isAccountNonExpired() {
		// 유효기간 -> 기간 설정해서 기간 넘어가면 사용 못하도록 설정
		//관리자가 관리
		if (expiredDate == null) {
	        return true;
	    }

	    return !LocalDate.now().isAfter(expiredDate);
	}

	@Override
	public boolean isAccountNonLocked() {
		// 계정 잠금 설정 ->5회 이상 실패하면 lock
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// 자격 증명 유효 기간 - 비밀번호 등 인증 자격 증명의 만료 여부
		if (passwordChangedDate == null) {
	        return true;
	    }

	    return LocalDate.now() //비밀번호 변경 90일이 지나면 false
	            .isBefore(passwordChangedDate.plusDays(90));
	}

	@Override
	public boolean isEnabled() { //해당 권한 활성화 비활성화
		return !del; //탈퇴 회원인 경우 비활성화
	}

	@Override
	public Map<String, Object> getAttributes() {
		return getProps();
	}

	@Override
	public String getName() {
		return this.mid;
	}
}
