package com.ddlab.rnd.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ddlab.rnd.event.CancelEvent;
import com.ddlab.rnd.event.OrderEvent;
import com.ddlab.rnd.event.ShippingEvent;
import com.ddlab.rnd.services.ShippingServiceImpl;

@Component
public class ShippingListener {

	@Autowired
	private ShippingServiceImpl shipService;

	@KafkaListener(topics = "${kafka.shipping.out.topic.name}")
	public void receiveItemForShipping(ShippingEvent shipEvent) {
		System.out.println("shipEvent.getShippingAdress()--->" + shipEvent.getShippingAdress());
		boolean invalidShipAdres = shipEvent.getShippingAdress().startsWith("Invalid");
		System.out.println("invalidShipAdres : " + invalidShipAdres);
		boolean validAction = shipEvent.getActionName().equalsIgnoreCase("SHIP_ITEM_ADDRESS");
		System.out.println("validAction : " + validAction);

		if (validAction && !invalidShipAdres) {
			System.out.println("Your item has been received by the courier company...");
			OrderEvent orderEvent = new OrderEvent();
			orderEvent.setOrderId(shipEvent.getOrderId());
			orderEvent.setActionName("ORDER_COMPLETE");
			shipService.completeOrder(orderEvent);
			System.out.println("Package has been successfully shipped to your address...");
		} else {
			System.out.println("Your order will be cancelled...");
			// Cancel shipping
			CancelEvent cancelEvent = new CancelEvent();
			cancelEvent.setActionName("CANCEL_PAYMENT");
			cancelEvent.setOrderId(shipEvent.getOrderId());
			cancelEvent.setOrderName(shipEvent.getItemName());
			cancelEvent.setReason("This item does not ship to address: " + shipEvent.getShippingAdress());
			shipService.cancelOrder(cancelEvent);
		}
	}

}
