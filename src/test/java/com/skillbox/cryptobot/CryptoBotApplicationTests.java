package com.skillbox.cryptobot;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class CryptoBotApplicationTests {

	@Test
	void contextLoads() {
		Pattern p = Pattern.compile("[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)");
		String t = "/subscribe 9999999.12";
		System.out.println(t.replaceAll("(/subscribe|\\s)", ""));
	}

}
