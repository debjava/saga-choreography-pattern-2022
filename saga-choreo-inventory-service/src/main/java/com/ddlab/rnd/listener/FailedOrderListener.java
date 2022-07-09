package com.ddlab.rnd.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ddlab.rnd.event.CancelEvent;
import com.ddlab.rnd.services.InventoryServiceImpl;

@Component
public class FailedOrderListener {
	
	@Autowired
	private InventoryServiceImpl inventoryService;
	
	@KafkaListener(topics = "${kafka.payment.cancel.topic.name}")
	public void cancelOrder(CancelEvent cancelEvent) {
		System.out.println("Failed Order Listener: "+cancelEvent);
		cancelEvent.setActionName("CANCEL_ORDER");
		inventoryService.cancelOrder(cancelEvent);
	}
}
