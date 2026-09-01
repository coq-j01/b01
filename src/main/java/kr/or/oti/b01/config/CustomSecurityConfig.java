package kr.or.oti.b01.config;


import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import kr.or.oti.b01.domain.Member;
import kr.or.oti.b01.repository.MemberRepository;
import kr.or.oti.b01.security.Custom403Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomSecurityConfig {
	private final DataSource dataSource;
	private final UserDetailsService customUserDetailsService;
	private final MemberRepository memberRepository;
	@Bean
	public PasswordEncoder paddwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		log.info("filterChain() 호출 =========================================");
		
		//CSRF 토큰 발급 및 검증 기능을 사용하지 않음
		//http.csrf().disable();
		//CSRF 설정
		CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

        csrfRepository.setCookieName("XSRF-TOKEN");
        csrfRepository.setHeaderName("X-XSRF-TOKEN");
        
        http.csrf(csrf -> csrf.csrfTokenRepository(csrfRepository));
		
		http.formLogin()
			.loginPage("/member/login") //login페이지 설정
			.successHandler((request, response, authentication)->{  //로그인 성공 시 처리
				//로그인 성공 처리 구현
				String username = authentication.getName();
				//성공시 failcount =0 lock은 false
				Member member = memberRepository.findById(username)
						.orElseThrow();

				// 로그인 성공하면 실패 횟수 초기화
				member.changeFailCount(0);

				memberRepository.save(member);

				// 로그인 성공 후 이동
				response.sendRedirect("/board/list");
			})
			.failureHandler((request, response, exception)->{  //로그인 실패 시 처리
				//로그인 실패 처리 구현
				//failcount + 1
				// 로그인 실패 시에는 authentication이 없으므로
				// form으로 전달된 username을 직접 가져옴
				String username = request.getParameter("username");

				memberRepository.findById(username).ifPresent(member -> {

					int failCount = member.getFailCount() + 1;

					member.changeFailCount(failCount);

					// 5회 이상 실패하면 잠금
					if (failCount >= 5) {
						member.changeAccountNonLocked(false);
					}

					memberRepository.save(member);
				});

				if (exception instanceof LockedException) {
			        response.sendRedirect("/member/login?error=LOCKED");
			    } else {
			        response.sendRedirect("/member/login?error=LOGIN_FAIL");
			    }
			});
			
		http.logout()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
	    .logoutSuccessUrl("/member/login?logout");
		
		http.rememberMe()
			.key("*^kosa1004!$")
			.tokenRepository(persistentTokenRepository()) //토큰이 어디에 저장되는 지
			.userDetailsService(customUserDetailsService)
			.tokenValiditySeconds(60*60*24*30); // 토큰 유지 시간 -> 30일로 설정
		
		http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
		
		http.oauth2Login().loginPage("/member/login");
		
		return http.build();
	}
	@Bean
	public AccessDeniedHandler accessDeniedHandler() {
		return new Custom403Handler();
	}
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() throws Exception{
		log.info("webSecurityCustomizer() 호출 =========================================");
		//web에 대해서 보안 설정을 안 함
		return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());//requestMatchers()-> 주소 매칭
	}
	//토큰 저장소
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
		repo.setDataSource(dataSource); //@RequiredArgsConstructor 이거 덕에 데이터 소스가 안에 존재
		return repo;
	}
}
