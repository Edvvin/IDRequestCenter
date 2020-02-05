/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package persojms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;
import javax.persistence.*;
/**
 *
 * @author edvvin
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(lookup = "myQueue")
    private static javax.jms.Queue queue;
    
    
    public static void main(String[] args) {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(queue);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersoJMSPU");
        EntityManager em = emf.createEntityManager();
        
        while(true){
            try {
                Message msg = consumer.receive();
                TextMessage tmsg = (TextMessage) msg;
                String id = tmsg.getText();
                // send request
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
