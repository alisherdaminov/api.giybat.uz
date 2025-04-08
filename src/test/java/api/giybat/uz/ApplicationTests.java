package api.giybat.uz;

import api.giybat.uz.services.sms.SmsSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@SpringBootTest
class ApplicationTests {

//	@Autowired
//	private SmsSendService smsSendService;

//	@Autowired
//	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Test
	void contextLoads() {
//		System.out.println(UUID.randomUUID().toString());
//		System.out.println(bCryptPasswordEncoder.encode("123456789"));
	}

}
