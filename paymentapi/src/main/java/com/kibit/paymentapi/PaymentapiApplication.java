package com.kibit.paymentapi;

import com.kibit.paymentapi.model.Wallet;
import com.kibit.paymentapi.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class PaymentapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentapiApplication.class, args);
	}

	@Autowired
	private WalletRepository walletRepository;

	@Bean
	@ConditionalOnProperty(prefix = "app", name = "db.init.enabled", havingValue = "true")
	public CommandLineRunner demoCommandLineRunner() {
		return args -> {

			Wallet wallet = new Wallet(50);
			Wallet wallet2 = new Wallet(100);

			walletRepository.saveAll(List.of(wallet, wallet2));

		};
	}

}
