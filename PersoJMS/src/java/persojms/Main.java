/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package persojms;

import entities.Documentrequest;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;
import javax.persistence.*;
import org.json.simple.JSONObject;
/**
 *
 * @author edvvin
 */
public class Main {

    @Resource(lookup = "jms/__defaultConnectionFactory")
    private static ConnectionFactory connectionFactory;
    
    @Resource(lookup = "myQueue")
    private static javax.jms.Queue queue;
    
    public static final String submitPerso = "http://collabnet.netset.rs:8081/is/persoCentar/submit";
    
    public static void main(String[] args) {
        JMSContext context = connectionFactory.createContext();
        JMSProducer producer = context.createProducer();
        JMSConsumer consumer = context.createConsumer(queue);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersoJMSPU");
        EntityManager em = emf.createEntityManager();
        
        while(true){
            try {
                //Creating the json message
                Message msg = consumer.receive();
                TextMessage tmsg = (TextMessage) msg;
                String id = tmsg.getText();
                java.util.List<Documentrequest> lst = em.createNamedQuery("Documentrequest.findById")
                        .setParameter("id", id).getResultList();
                if(lst.size() == 0){
                    tmsg = context.createTextMessage(id);
                    producer.send(queue, tmsg);
                    continue;
                }
                    
                Documentrequest dr = lst.get(0);
                JSONObject obj = new JSONObject();
                obj.put("id",dr.getId());
                obj.put("ime",dr.getIme());
                obj.put("prezime", dr.getPrezime());
                obj.put("imeMajke",dr.getImeMajke());
                obj.put("imeOca",dr.getImeOca());
                obj.put("prezimeMajke", dr.getPrezimeMajke());
                obj.put("prezimeOca", dr.getPrezimeOca());
                obj.put("pol",dr.getPol());
                obj.put("datumRodjenja",dr.getDatumRodjenja());
                obj.put("nacionalnost",dr.getNacionalnost());
                obj.put("profesija",dr.getProfesija());
                obj.put("bracnoStanje",dr.getBracnoStanje());
                obj.put("opstinaPrebivalista",dr.getOpstinaPrebivalista());
                obj.put("ulicaPrebivalista",dr.getUlicaPrebivalista());
                obj.put("brojPrebivalista",dr.getBrojPrebivalista());
                obj.put("JMBG",dr.getJmbg());
                
                // attempting to send the document request to perso
                
                URL url = new URL(submitPerso);
                HttpURLConnection submitConnection = (HttpURLConnection) url.openConnection();
                submitConnection.setRequestMethod("POST");
                submitConnection.setDoOutput(true);
                OutputStream os = submitConnection.getOutputStream();
                os.write(obj.toString().getBytes());
                os.flush();
                os.close();
                
                int rcode = submitConnection.getResponseCode();
                if(rcode != 200){
                    tmsg = context.createTextMessage(id);
                    producer.send(queue, tmsg);
                }else{
                    dr.setStanje("uProdukciji");
                    em.getTransaction().begin();
                    em.persist(dr);
                    em.flush();
                    em.getTransaction().commit();
                }
            } catch (JMSException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
