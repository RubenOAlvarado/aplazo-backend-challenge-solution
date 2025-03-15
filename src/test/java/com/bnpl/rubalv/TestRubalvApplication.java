package com.bnpl.rubalv;

import org.springframework.boot.SpringApplication;

public class TestRubalvApplication {

	public static void main(String[] args) {
		SpringApplication.from(RubalvApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
