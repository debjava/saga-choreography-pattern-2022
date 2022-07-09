package com.ddlab.rnd.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ddlab.rnd.event.CancelEvent;
import com.ddlab.rnd.event.ItemEvent;
import com.ddlab.rnd.event.ShippingEvent;
import com.ddlab.rnd.services.PaymentServiceImpl;

@Component
public class PaymentListener {

	@Autowired
	private PaymentServiceImpl paymentService;

	@KafkaListener(topics = "${kafka.inventory.out.topic.name}")
	public void receivePayment(ItemEvent itemEvent) {
		if (itemEvent.getActionName().equals("RECEIVE_PAYMENT")) {
			boolean isReceived = isPaymentReceived(itemEvent.getPrice());
			System.out.println("itemEvent.getShipToAddress()---->" + itemEvent.getShipToAddress());
			if (isReceived) {
				System.out.println("Your payment has been successfull ...");
				// Confirm for shipping
				ShippingEvent shipEvent = new ShippingEvent();
				shipEvent.setActionName("SHIP_ITEM_ADDRESS");
				shipEvent.setItemName(itemEvent.getItemName());
				shipEvent.setOrderId(itemEvent.getOrderId());
				shipEvent.setShippingAdress(itemEvent.getShipToAddress());

				paymentService.shipToAddress(shipEvent);
				System.out.println("Your package is ready to be shipped...");
			} else {
				// Insufficient balance
				CancelEvent cancelEvent = new CancelEvent();
				cancelEvent.setOrderId(itemEvent.getOrderId());
				cancelEvent.setActionName("CANCEL_ORDER");
				cancelEvent.setReason("Insufficient fund...");
				cancelEvent.setOrderName(itemEvent.getItemName());
				paymentService.cancelOrder(cancelEvent);
				System.out.println("Order has been cancelled due to insufficient fund...");
			}
		}
	}

	public boolean isPaymentReceived(int amount) {
		if (amount > 50000)
			return false;
		else
			return true;
	}
}
