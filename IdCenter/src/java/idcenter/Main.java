package idcenter;

import entities.Documentrequest;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;




public class Main {
    
    public static String getTermin(){
        return "2020-02-24T16:45:00";
    }
    
    public static final String checkTermin = "http://collabnet.netset.rs:8081/is/terminCentar/checkTimeslotAvailability";
    public static final String submitPerso = "http://collabnet.netset.rs:8081/is/persoCentar/submit";
    public static final String checkPerso = "http://collabnet.netset.rs:8081/is/persoCentar";
    public static final String centerId = "17117";
    public static EntityManagerFactory emf;
    public static EntityManager em;
    public static void main(String[] args) {
        emf = Persistence.createEntityManagerFactory("IdCenterPU");
        em = emf.createEntityManager();
        new Interface();
    }
    
}
