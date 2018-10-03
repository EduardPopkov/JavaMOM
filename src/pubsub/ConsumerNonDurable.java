package pubsub;

import com.sun.messaging.ConnectionConfiguration;

import javax.jms.*;

public class ConsumerNonDurable {
    public static void main(String[] args) {
        ConnectionFactory factory = new com.sun.messaging.ConnectionFactory();

        try (JMSContext context = factory.createContext("admin", "admin")){
            ((com.sun.messaging.ConnectionFactory) factory).setProperty(ConnectionConfiguration.imqAddressList,
                    "mq://127.0.0.1:7676,mq://127.0.0.1:7676");

            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Destination ordersTopic = context.createTopic("TradingOrdersTopic");
            context.setClientID("client123");
            JMSConsumer consumer = context.createDurableConsumer((Topic) ordersTopic, "hello");

            connection.start();
            System.out.println("Wait...");

            while (true){
                Message m = consumer.receive();
                if(m != null){
                    if(m instanceof TextMessage){
                        System.out.println(((TextMessage) m).getText());
                    } else{
                        break;
                    }
                }
            }


        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
