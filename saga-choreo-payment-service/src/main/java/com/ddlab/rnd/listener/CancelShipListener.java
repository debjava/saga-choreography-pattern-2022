package com.ddlab.rnd.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ddlab.rnd.event.CancelEvent;
import com.ddlab.rnd.services.PaymentServiceImpl;

@Component
public class CancelShipListener {
	
	@Autowired
	private PaymentServiceImpl paymentService;
	
	@KafkaListener(topics = "${kafka.cancel.ship.topic.name}")
	public void listen(CancelEvent cancelEvent) {
		if(cancelEvent.getActionName().equals("CANCEL_PAYMENT")) {
			paymentService.reverseTransaction(cancelEvent);
		}
	}
}
