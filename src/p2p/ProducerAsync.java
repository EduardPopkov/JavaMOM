package p2p;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;

import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;

public class ProducerAsync {
    public static void main(String[] args) {
        ConnectionFactory factory = new com.sun.messaging.ConnectionFactory();

        try (JMSContext context = factory.createContext("admin", "admin")){
            ((com.sun.messaging.ConnectionFactory) factory).setProperty(ConnectionConfiguration.imqAddressList,
                    "mq://127.0.0.1:7676,mq://127.0.0.1:7676");

            Destination ordersQueue = context.createQueue("TradingOrdersQueue");
            JMSProducer producer = context.createProducer();

            producer.send(ordersQueue, "this is a message");

            System.out.println("OK");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
