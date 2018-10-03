package p2p;

import com.sun.messaging.ConnectionConfiguration;

import javax.jms.*;

public class ProducerSync {
    public static void main(String[] args) {
        ConnectionFactory factory = new com.sun.messaging.ConnectionFactory();

        try (JMSContext context = factory.createContext("admin", "admin")){
            ((com.sun.messaging.ConnectionFactory) factory).setProperty(ConnectionConfiguration.imqAddressList,
                    "mq://127.0.0.1:7676,mq://127.0.0.1:7676");

            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination ordersQueue = context.createQueue("TradingOrdersQueue");
            JMSProducer producer = context.createProducer();

            TextMessage message = session.createTextMessage();

            for(int i = 0; i < 5; i++){
                message.setText("This is message " + (i + 1) + " from producer");
                System.out.println("Sending message: " + message.getText());
                producer.send(ordersQueue, message);
            }

            producer.send(ordersQueue, session.createMessage());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
