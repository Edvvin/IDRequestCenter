package idcenter;

import entities.Documentrequest;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.logging.*;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.persistence.*;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class CreateRequest implements ActionListener{
    private Interface gui;
    private int idMaybe = 0;
    public CreateRequest(Interface gui){
        this.gui = gui;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        
        //checking the Termin
        String termin = Main.getTermin();
        String terminCheckUrl = Main.checkTermin + "?regionalniCentarId=" + Main.centerId + "&termin=" + termin;
        URL url;
        JSONParser par = new JSONParser();
        BufferedReader input;
        try{
            url = new URL(terminCheckUrl);
            HttpURLConnection terminConnection = (HttpURLConnection) url.openConnection();
            terminConnection.setRequestMethod("GET");
            int rcode = terminConnection.getResponseCode();
            if(rcode != 200){
                System.out.println("Termin Check Error: " + rcode);
                return;
            }
            input = new BufferedReader(new InputStreamReader(terminConnection.getInputStream()));
            JSONObject response = (JSONObject) par.parse(input);
            boolean avail = (boolean) response.get("dostupnost");
            if(!avail){
                gui.showMessage("Termin: " + termin + " is not available");
                return;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(CreateRequest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateRequest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(CreateRequest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Find available ID
        while(true){
            java.util.List<Documentrequest> lst = 
                    Main.em.createNamedQuery("Documentrequest.findById")
                            .setParameter("id", Main.centerId + String.format("%07d", idMaybe))
                            .getResultList();
            if(lst.size()>0){
                idMaybe++;
                continue;
            }
            try {
                url = new URL(Main.checkPerso + "/" + Main.centerId + String.format("%07d", idMaybe));
                HttpURLConnection persoConnection = (HttpURLConnection) url.openConnection();
                persoConnection.setRequestMethod("GET");
                int rcode = persoConnection.getResponseCode();
                if(rcode == 404){
                    break;
                }
                if(rcode == 400){
                    gui.showMessage("Error bad access!");
                    return;
                }
            } catch (MalformedURLException ex) {
                Logger.getLogger(CreateRequest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CreateRequest.class.getName()).log(Level.SEVERE, null, ex);
            }
            idMaybe++;
        }
        
        
        // creating an instance
        String newId = String.format("%07d", idMaybe);
        String id = Main.centerId+newId;
        Documentrequest dr = new Documentrequest(id);
        String temp;
        double test = 1.0;
        temp = gui.jmbg.getText();
        if(temp.length() != 13){
            gui.showMessage("JMBG must have exactly 13 characters");
            return;
        }
        dr.setJmbg(temp);
        temp = gui.ime.getText();
        test*=temp.length();
        dr.setIme(temp);
        temp = gui.prezime.getText();
        test*=temp.length();
        dr.setPrezime(temp);
        temp = gui.imeMajke.getText();
        test*=temp.length();
        dr.setImeMajke(temp);
        temp = gui.imeOca.getText();
        test*=temp.length();
        dr.setImeOca(temp);
        temp = gui.prezimeMajke.getText();
        test*=temp.length();
        dr.setPrezimeMajke(temp);
        temp = gui.prezimeOca.getText();
        test*=temp.length();
        dr.setPrezimeOca(temp);
        temp = gui.nacionalnost.getText();
        test*=temp.length();
        dr.setNacionalnost(temp);
        temp = gui.profesija.getText();
        test*=temp.length();
        dr.setProfesija(temp);
        temp = gui.opstina.getText();
        test*=temp.length();
        dr.setOpstinaPrebivalista(temp);
        temp = gui.ulica.getText();
        test*=temp.length();
        dr.setUlicaPrebivalista(temp);
        temp = gui.broj.getText();
        test*=temp.length();
        dr.setBrojPrebivalista(temp);
        if(gui.muski.getState())
            dr.setPol("musko");
        else
            dr.setPol("zensko");
        dr.setBracnoStanje(gui.brak.getSelectedItem());
        temp = "";
        temp+=gui.godina.getSelectedItem();
        temp+= "-";
        temp+=gui.mesec.getSelectedItem();
        temp+= "-";
        temp+=gui.dan.getSelectedItem();
        dr.setDatumRodjenja(temp);
        dr.setStanje("kreiran");
        
        if(test < 0.1){
            gui.showMessage("All fields must be filled");
            return;
        }
        
        // persist in database
        Main.em.getTransaction().begin();
        Main.em.persist(dr);
        Main.em.flush();
        Main.em.getTransaction().commit();
        
        // add to JMS Queue
        TextMessage msg = Main.context.createTextMessage(id);
        Main.producer.send(Main.queue, msg);
        gui.showMessage("Request created with id: " + id);
    }
    
}
