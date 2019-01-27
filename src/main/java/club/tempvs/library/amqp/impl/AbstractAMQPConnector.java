package club.tempvs.library.amqp.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class AbstractAMQPConnector {

    private static final int DELIVERY_TIMEOUT = 30000; //30 seconds

    protected final ConnectionFactory amqpConnectionFactory;

    protected void execute(Consumer<String> action) {
        Connection connection = null;
        Channel channel = null;

        try {
            connection = amqpConnectionFactory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(getQueue(), false, false, false, null);

            QueueingConsumer consumer = new QueueingConsumer(channel);
            channel.basicConsume(getQueue(), true, consumer);

            while (true) {
                QueueingConsumer.Delivery delivery = consumer.nextDelivery(DELIVERY_TIMEOUT);

                if (delivery != null) {
                    String message = new String(delivery.getBody());
                    action.accept(message);
                } else {
                    throw new RuntimeException("The " + getQueue() + " queue is empty");
                }
            }
        } catch (RuntimeException e) {
            //do nothing, just skip until the next execution
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract String getQueue();
}
