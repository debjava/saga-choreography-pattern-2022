package com.ddlab.rnd.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ddlab.rnd.event.CancelEvent;
import com.ddlab.rnd.event.OrderEvent;

@Service
public class ShippingServiceImpl {
	
	@Autowired
	private KafkaTemplate<String, OrderEvent> kafkaTemplate;
	
	@Value("${kafka.order.complete.topic.name}")
	private String topicName;
	
	@Autowired
	private KafkaTemplate<String, CancelEvent> cancelKafkaTemplate;
	
	@Value("${kafka.cancel.ship.topic.name}")
	private String cancelTopicName;
	
	public void completeOrder(OrderEvent orderEvent) {
		kafkaTemplate.send(topicName, orderEvent);
		System.out.println("Your order processing has been complete");
	}
	
	public void cancelOrder(CancelEvent cancelEvent) {
		cancelKafkaTemplate.send(cancelTopicName, cancelEvent);
		System.out.println("Your order processing has been cancelled due to invalid address");
	}
}
