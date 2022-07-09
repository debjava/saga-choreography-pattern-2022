package com.ddlab.rnd.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ddlab.rnd.event.CancelEvent;
import com.ddlab.rnd.event.ShippingEvent;

@Service
public class PaymentServiceImpl {

	@Autowired
	private KafkaTemplate<String, ShippingEvent> kafkaTemplate;

	@Value("${kafka.shipping.out.topic.name}")
	private String topicName;

	@Autowired
	private KafkaTemplate<String, CancelEvent> cancelKafkaTemplate;

	@Value("${kafka.payment.cancel.topic.name}")
	private String cancelTopicName;

	public void shipToAddress(ShippingEvent shipEvent) {
		kafkaTemplate.send(topicName, shipEvent);
	}

	public void cancelOrder(CancelEvent cancelEvent) {
		cancelKafkaTemplate.send(cancelTopicName, cancelEvent);
	}

	public void reverseTransaction(CancelEvent cancelEvent) {
		System.out.println("Your transaction has been reversed, you will get back your amount in 48 hours...");
		cancelOrder(cancelEvent);
	}

}
