package com.kibit.paymentapi;

import org.springframework.boot.SpringApplication;

public class TestPaymentapiApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaymentapiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
