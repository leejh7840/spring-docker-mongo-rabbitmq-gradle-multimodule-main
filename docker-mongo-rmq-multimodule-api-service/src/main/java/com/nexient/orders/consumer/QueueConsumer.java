package com.nexient.orders.consumer;

import com.nexient.orders.data.config.MessagingConfig;
import com.nexient.orders.data.entity.OrderStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QueueConsumer {

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeMessageFromQueue(OrderStatus orderStatus) {
        System.out.println("Message was received from queue : " + orderStatus);
    }

}