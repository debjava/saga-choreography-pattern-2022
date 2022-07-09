package com.ddlab.rnd.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ddlab.rnd.event.CancelEvent;
import com.ddlab.rnd.event.ItemEvent;

@Service
public class InventoryServiceImpl {

	@Autowired
	private KafkaTemplate<String, ItemEvent> kafkaTemplate;

	@Autowired
	private KafkaTemplate<String, CancelEvent> cancelKafkaTemplate;

	@Value("${kafka.inventory.out.topic.name}")
	private String topicName;

	@Value("${kafka.order.cancel.topic.name}")
	private String cancelTopicName;

	public void sendForPaymentInitiation(ItemEvent itemEvent) {
		System.out.println("Initiating payment for the item");
		kafkaTemplate.send(topicName, itemEvent);
		System.out.println("Sent for payment initiation ...");
	}

	public void compensate(CancelEvent cancelEvent) {
		System.out.println("This item is currenlty out of stock, hence order is cancelled...");
		cancelKafkaTemplate.send(cancelTopicName, cancelEvent);
	}
	
	public void cancelOrder(CancelEvent cancelEvent) {
		System.out.println("Order has been cancelled and item is put back in the inventory");
		cancelKafkaTemplate.send(cancelTopicName, cancelEvent);
	}

}
