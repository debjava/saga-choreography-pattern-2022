package com.ddlab.rnd.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ddlab.rnd.event.CancelEvent;
import com.ddlab.rnd.event.ItemEvent;
import com.ddlab.rnd.event.OrderEvent;
import com.ddlab.rnd.services.InventoryServiceImpl;

@Component
public class PlacedOrderListener {
	
	@Autowired
	private InventoryServiceImpl inventoryService;

	@KafkaListener(topics = "${kafka.order.topic.name}",groupId = "saga-order-grp-id")
	public void listenOrderPlaced(OrderEvent orderEvent) {
		System.out.println("Placed Order has come to inventory ...");
		System.out.println("Order Details:" + orderEvent);
		if(orderEvent.getActionName().equalsIgnoreCase("ORDER_PLACED")) {
			// Make next call to initiate the payment
			boolean itemAvlable = isItemAvailable(orderEvent.getOrderName());
			System.out.println("orderEvent.getShipToAddress()--->"+orderEvent.getShipToAddress());
			if(itemAvlable) {
				// Initiate for Payment
				ItemEvent itemEvent = new ItemEvent();
				itemEvent.setOrderId(orderEvent.getOrderId());
				itemEvent.setItemName(orderEvent.getOrderName());
				itemEvent.setPrice(orderEvent.getOrderPrice());
				itemEvent.setActionName("RECEIVE_PAYMENT");
				itemEvent.setShipToAddress(orderEvent.getShipToAddress());
				
				inventoryService.sendForPaymentInitiation(itemEvent);
			} else {
				CancelEvent cancelEvent = new CancelEvent();
				cancelEvent.setActionName("CANCEL_ORDER");
				cancelEvent.setReason("Outof Stock");
				
				inventoryService.compensate(cancelEvent);
			}
		}
	}
	
	private boolean isItemAvailable(String name) {
		if(name.startsWith("Vivo")) return false;
		
		return true;
	}
}
