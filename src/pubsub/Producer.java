package pubsub;

import com.sun.messaging.ConnectionConfiguration;

import javax.jms.*;

public class Producer {
    public static void main(String[] args) {
        ConnectionFactory factory = new com.sun.messaging.ConnectionFactory();

        try (JMSContext context = factory.createContext("admin", "admin")){
            ((com.sun.messaging.ConnectionFactory) factory).setProperty(ConnectionConfiguration.imqAddressList,
                    "mq://127.0.0.1:7676,mq://127.0.0.1:7676");

            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination cardsTopic = context.createTopic("TradingOrdersTopic");
            JMSProducer producer = context.createProducer();

            TextMessage message = context.createTextMessage();
            message.setText("This is a message");

            producer.send(cardsTopic, message);

            System.out.println("OK");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
