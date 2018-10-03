package pubsub;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.IllegalStateException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Test {
    public static void main(String[] args) throws NamingException, JMSException {
        AtomicBoolean result = new AtomicBoolean(true);
        Connection connection = null;
        InitialContext initialContext = null;
        try {
            // Step 1. Create an initial context to perform the JNDI lookup.
            initialContext = new InitialContext();

            // Step 2. look-up the JMS queue object from JNDI
            Queue queue = (Queue) initialContext.lookup("queue/exampleQueue");

            // Step 3. look-up the JMS connection factory object from JNDI
            ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

            // Step 4. Create a JMS Connection
            connection = cf.createConnection();

            // Step 5. Start the connection
            connection.start();

            // Step 5. Create a JMS Session
            Session senderSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Step 6. Create a JMS Message Producer
            MessageProducer producer = senderSession.createProducer(queue);

            // Step 8. Prepare two selectors
            String redSelector = "color='red'";
            String greenSelector = "color='green'";

            // Step 9. Create a JMS Message Consumer that receives 'red' messages
            Session redSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer redConsumer = redSession.createConsumer(queue, redSelector);
            redConsumer.setMessageListener(new SimpleMessageListener("red", result));

            // Step 10. Create a second JMS message consumer that receives 'green' messages
            Session greenSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer greenConsumer = greenSession.createConsumer(queue, greenSelector);
            greenConsumer.setMessageListener(new SimpleMessageListener("green", result));

            // Step 11. Create another JMS message consumer that receives any messages.
            Session blankSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer anyConsumer = blankSession.createConsumer(queue);
            anyConsumer.setMessageListener(new SimpleMessageListener("any", result));

            // Step 12. Create three messages, each has a color property
            TextMessage redMessage = senderSession.createTextMessage("Red");
            redMessage.setStringProperty("color", "red");
            TextMessage greenMessage = senderSession.createTextMessage("Green");
            greenMessage.setStringProperty("color", "green");
            TextMessage blueMessage = senderSession.createTextMessage("Blue");
            blueMessage.setStringProperty("color", "blue");

            // Step 13. Send the Messages
            producer.send(redMessage);
            System.out.println("Message sent: " + redMessage.getText());
            producer.send(greenMessage);
            System.out.println("Message sent: " + greenMessage.getText());
            producer.send(blueMessage);
            System.out.println("Message sent: " + blueMessage.getText());

            Thread.sleep(5000);

            if (!result.get())
                throw new IllegalStateException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            // Step 12. Be sure to close our JMS resources!
            if (initialContext != null) {
                initialContext.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
class SimpleMessageListener implements MessageListener {

    private final String name;
    private AtomicBoolean result;

    SimpleMessageListener(final String listener, AtomicBoolean result) {
        name = listener;
        this.result = result;
    }

    @Override
    public void onMessage(final Message msg) {
        TextMessage textMessage = (TextMessage) msg;
        try {
            String colorProp = msg.getStringProperty("color");
            System.out.println("Receiver " + name +
                    " receives message [" +
                    textMessage.getText() +
                    "] with color property: " +
                    colorProp);
            if (!colorProp.equals(name) && !name.equals("any")) {
                result.set(false);
            }
        } catch (JMSException e) {
            e.printStackTrace();
            result.set(false);
        }
    }
}