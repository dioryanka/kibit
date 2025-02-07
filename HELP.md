# Instant Payment API

### Short descriptions

The application has multiple endpoints, if the endpoint receive the userIds and the idempotencyKey (wich will identify the request itself).
If it is not a duplicated request, then the app will save the transactions into postgress table with a Status (EXECUTING, SUCCES, FAILED).
Initially every transaction start with EXECUTING status (obviously in a real world env we can introduce more status, before EXECUTING as well etc...)
If the the db is available and every db operation finished successfully we will send a kafka notification to the recipient (with idempotencyKey as well).
On the Consumer side we have a simple dummy Kafka app wich is just listening and send back the kafkaMessage about the transaction (in this example it is always SUCCESS)
to demonstrate the functionality.
The paymentApi is listening on this topic (topic-event-2), if the payment is succes, then we update the Transaction Status in the postgres table to SUCCES
and update the balance as well.

With the openAPI ui we can demonstrate the sendMoney functionality and check the result with the getStatus and getBalance endpoints.

In this example we have a Wallet table as well, where the walletId represents the userId as well.


### API endpoints

POST
	/payment/sendMoney
GET
	/payment/getBalance/{userId}
	/payment/getStatus/{idempotencyKey}
	

### Testcontainers support

This project uses [Testcontainers at development time](https://docs.spring.io/spring-boot/3.5.0-SNAPSHOT/reference/features/dev-services.html#features.dev-services.testcontainers).

Testcontainers has been configured to use the following Docker images:


Please review the tags of the used images and set them to the same as you're running in production.

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

