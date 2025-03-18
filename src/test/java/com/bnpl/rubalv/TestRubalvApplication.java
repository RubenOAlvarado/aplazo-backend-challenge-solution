package com.bnpl.rubalv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class TestRubalvApplication {

	public static void main(String[] args) {
		SpringApplication.from(RubalvApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
