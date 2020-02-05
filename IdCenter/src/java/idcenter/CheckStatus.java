package idcenter;

import entities.Documentrequest;
import java.awt.event.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CheckStatus implements ActionListener{
    private Interface gui;
    public CheckStatus(Interface gui){
        this.gui = gui;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String id = gui.reqId.getText();
        if(id.length() != 12){
            gui.showMessage("Invalid ID");
            return;
        }
        
        java.util.List<Documentrequest> lst = Main.em.createNamedQuery("Documentrequest.findById")
                .setParameter("id", id).getResultList();
        if(lst.size() == 0){
            gui.showMessage("No such request in database");
            return;
        }
        Documentrequest dr = lst.get(0);
        
        String persoCheck = Main.checkPerso + "/" + id;
        JSONParser par = new JSONParser();
        BufferedReader input;
        try {
            URL url = new URL(persoCheck);
            HttpURLConnection persoConnection = (HttpURLConnection) url.openConnection();
            persoConnection.setRequestMethod("GET");
            int rcode = persoConnection.getResponseCode();
            if(rcode != 200){
                gui.showMessage("No such request in perso database");
                return;
            }
            input = new BufferedReader(new InputStreamReader(persoConnection.getInputStream()));
            JSONObject response = (JSONObject) par.parse(input);
            String stanje = (String) response.get("status");
            if(stanje.equals("proizveden")){
                dr.setStanje(stanje);
                Main.em.getTransaction().begin();
                Main.em.persist(dr);
                Main.em.flush();
                Main.em.getTransaction().commit();
                gui.uruciDokument.setEnabled(true);
            }else{
                gui.uruciDokument.setEnabled(false);
            }
            gui.selectedId.setText(id);
            gui.status.setText(dr.getStanje());
        } catch (MalformedURLException ex) {
            Logger.getLogger(CheckStatus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CheckStatus.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(CheckStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
