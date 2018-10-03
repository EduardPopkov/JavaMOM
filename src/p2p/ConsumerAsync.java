package p2p;

import com.sun.messaging.ConnectionConfiguration;

import javax.jms.*;

public class ConsumerAsync implements MessageListener {
    ConnectionFactory factory = new com.sun.messaging.ConnectionFactory();

    ConsumerAsync(){
        try (JMSContext context = factory.createContext("admin", "admin")){
            ((com.sun.messaging.ConnectionFactory) factory).setProperty(ConnectionConfiguration.imqAddressList,
                    "mq://127.0.0.1:7676,mq://127.0.0.1:7676");

            Destination ordersQueue = context.createQueue("TradingOrdersQueue");
            JMSConsumer consumer = context.createConsumer(ordersQueue);

            consumer.setMessageListener(this);

            System.out.print("listening...");
            Thread.sleep(10000);

        } catch (JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            System.out.println("\nBody message: " + message.getBody(String.class));
            System.out.println("\nmessage: \n" + message);
        } catch (JMSException e) {
            System.err.println("JMSException: " + e.toString());
        }
    }

    public static void main(String[] args) {
        new ConsumerAsync();
    }
}
