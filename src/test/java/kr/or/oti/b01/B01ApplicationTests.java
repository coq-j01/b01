package kr.or.oti.b01;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class B01ApplicationTests {

	@Test
	void contextLoads() {
		//암호화된 pw 확인 -> $2a$10$jxmrN4ee0jciUD/1cStDO./fYr1B3tI.nFPM5BPRRhqlEr9ioWBjG
		System.out.println("password(kosa1004) : "+ new BCryptPasswordEncoder().encode("kosa1004"));
	}

}
