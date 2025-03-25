package api.giybat.uz;

import api.giybat.uz.services.sms.SmsSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private SmsSendService smsSendService;
	@Test
	void contextLoads() {
		smsSendService.getToken();
	}

}
