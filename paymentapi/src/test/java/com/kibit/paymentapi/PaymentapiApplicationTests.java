package com.kibit.paymentapi;

import com.kibit.paymentapi.dto.KafkaMessage;
import com.kibit.paymentapi.model.Wallet;
import com.kibit.paymentapi.repository.TransactionRepository;
import com.kibit.paymentapi.repository.WalletRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// activate automatic startup and stop of containers
@Testcontainers
// JPA drop and create table, good for testing
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
@EmbeddedKafka(partitions = 4, topics = "topic-event-1")
@TestPropertySource(properties = {
		"spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PaymentapiApplicationTests {

	@LocalServerPort
	private Integer port;

	@Autowired
	private WalletRepository walletRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private EmbeddedKafkaBroker embeddedKafkaBroker;

	private Consumer<String, KafkaMessage> consumer;

	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:15-alpine"
	);

	@BeforeEach
	void setUp() {
		Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("my_group_id", "true", embeddedKafkaBroker);
		consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		JsonDeserializer<KafkaMessage> jsonDeserializer = new JsonDeserializer<>(KafkaMessage.class);
		jsonDeserializer.addTrustedPackages("com.kibit.paymentapi.dto");
		DefaultKafkaConsumerFactory<String, KafkaMessage> consumerFactory = new DefaultKafkaConsumerFactory<>(
				consumerProps, new StringDeserializer(), jsonDeserializer);
		consumer = consumerFactory.createConsumer();
		embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "topic-event-1");

		RestAssured.baseURI = "http://localhost:" + port;
		Wallet wallet = new Wallet(50);
		Wallet wallet2 = new Wallet(100);

		walletRepository.saveAll(List.of(wallet, wallet2));
	}

	@Test
	public void testPreventDuplicateSpending() {
		Response result = given()
				.contentType(ContentType.JSON)
				.body("{\"idempotencyKey\": \"3fa85f22-5717-4562-b3fc-2c963f66afa6\", \"fromUserId\": 1, \"toUserId\": 2, \"amount\": 20, \"creditCardDetails\": \"string\"}")
				.when()
				.post("/payment/sendMoney")
				.then()
				.statusCode(200)
				.contentType(ContentType.JSON).extract().response();

		//we send the same query twice
		Response result2 = given()
				.contentType(ContentType.JSON)
				.body("{\"idempotencyKey\": \"3fa85f22-5717-4562-b3fc-2c963f66afa6\", \"fromUserId\": 1, \"toUserId\": 2, \"amount\": 20, \"creditCardDetails\": \"string\"}")
				.when()
				.post("/payment/sendMoney")
				.then()
				.statusCode(400)
				.contentType(ContentType.JSON).extract().response();

		System.out.println(result2.asString());
		assertTrue(result2.asString().contains("The current request has been duplicated"));
		assertTrue(transactionRepository.findById(UUID.fromString("3fa85f22-5717-4562-b3fc-2c963f66afa6")).isPresent());
	}

	@Test
	public void testInsufficientFunds() {
		Response result = given()
				.contentType(ContentType.JSON)
				.body("{\"idempotencyKey\": \"3fa85f21-5717-4562-b3fc-2c963f66afa6\", \"fromUserId\": 1, \"toUserId\": 2, \"amount\": 51, \"creditCardDetails\": \"string\"}")
				.when()
				.post("/payment/sendMoney")
				.then()
				.statusCode(200)
				.contentType(ContentType.JSON).extract().response();

		System.out.println(result.asString());

		assertTrue(result.asString().contains("Insufficient funds"));
	}

	@Test
	public void testMissingWalletException() {
		Response result = given()
				.contentType(ContentType.JSON)
				.body("{\"idempotencyKey\": \"3fa85f21-5717-4562-b3fc-2c963f66afa6\", \"fromUserId\": 3, \"toUserId\": 2, \"amount\": 1, \"creditCardDetails\": \"string\"}")
				.when()
				.post("/payment/sendMoney")
				.then()
				.statusCode(400)
				.contentType(ContentType.JSON).extract().response();

		assertTrue(result.asString().contains("Wallet not found for the userId"));
	}

	@Test
	public void testSendMoneyToUserSuccessfully() {
		Response result = given()
				.contentType(ContentType.JSON)
				.body("{\"idempotencyKey\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\", \"fromUserId\": 1, \"toUserId\": 2, \"amount\": 20, \"creditCardDetails\": \"string\"}")
				.when()
				.post("/payment/sendMoney")
				.then()
				.statusCode(200)
				.contentType(ContentType.JSON).extract().response();

		System.out.println(result.asString());

		assertTrue(result.asString().contains("3fa85f64-5717-4562-b3fc-2c963f66afa6 is now EXECUTING"));
		assertTrue(transactionRepository.findById(UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6")).isPresent());
		ConsumerRecord<String, KafkaMessage> received = KafkaTestUtils.getSingleRecord(consumer, "topic-event-1", Duration.ofSeconds(5));
		System.out.println(received.value().getIdempotencyKey());
		assertEquals("3fa85f64-5717-4562-b3fc-2c963f66afa6", received.value().getIdempotencyKey().toString());
	}
}