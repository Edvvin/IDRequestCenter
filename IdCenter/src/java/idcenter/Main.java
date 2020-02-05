package idcenter;

import entities.Documentrequest;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;




public class Main {
    
    public static String getTermin(){
        LocalDateTime ldt = LocalDateTime.now();
        String res = "";
        res+=ldt.getYear();
        res+="-";
        res+=String.format("%02d",ldt.getMonthValue());
        res+="-";
        res+=String.format("%02d",ldt.getDayOfMonth());
        res+="T";
        res+=String.format("%02d",ldt.getHour());
        res+=":";
        res+=String.format("%02d",ldt.getMinute());
        res+=":";
        res+=String.format("%02d",ldt.getSecond());
        return "2020-02-24T16:45:00";
    }
    
    public static final String checkTermin = "http://collabnet.netset.rs:8081/is/terminCentar/checkTimeslotAvailability";
    public static final String checkPerso = "http://collabnet.netset.rs:8081/is/persoCentar";
    public static final String centerId = "17117";
    
    static EntityManagerFactory emf;
    static EntityManager em;
    
    @Resource(lookup = "jms/__defaultConnectionFactory")
    static ConnectionFactory connectionFactory;
    @Resource(lookup = "myQueue")
    static javax.jms.Queue queue;
    static JMSContext context;
    static JMSProducer producer;
    
    
    public static void main(String[] args) {
        context = connectionFactory.createContext();
        producer = context.createProducer();
        emf = Persistence.createEntityManagerFactory("IdCenterPU");
        em = emf.createEntityManager();
        new Interface();
    }
    
}
