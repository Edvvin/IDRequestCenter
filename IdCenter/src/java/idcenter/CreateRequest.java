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
        
        
        // creating an instance
        String newId = String.format("%07d", gui.ID++);
        Documentrequest dr = new Documentrequest(Main.centerId+newId);
        String temp;
        temp = gui.jmbg.getText();
        dr.setJmbg(temp);
        temp = gui.ime.getText();
        dr.setIme(temp);
        temp = gui.prezime.getText();
        dr.setPrezime(temp);
        temp = gui.imeMajke.getText();
        dr.setImeMajke(temp);
        temp = gui.imeOca.getText();
        dr.setImeOca(temp);
        temp = gui.prezimeMajke.getText();
        dr.setPrezimeMajke(temp);
        temp = gui.prezimeOca.getText();
        dr.setPrezimeOca(temp);
        temp = gui.nacionalnost.getText();
        dr.setNacionalnost(temp);
        temp = gui.profesija.getText();
        dr.setProfesija(temp);
        temp = gui.opstina.getText();
        dr.setOpstinaPrebivalista(temp);
        temp = gui.ulica.getText();
        dr.setUlicaPrebivalista(temp);
        temp = gui.broj.getText();
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
        
        // persist in database
        Main.em.getTransaction().begin();
        Main.em.persist(dr);
        Main.em.flush();
        Main.em.getTransaction().commit();
        
        // add to JMS Queue
        TextMessage msg = Main.context.createTextMessage(newId);
        Main.producer.send(Main.queue, msg);
        gui.showMessage("Request created with id: " + Main.centerId + newId);
    }
    
}
