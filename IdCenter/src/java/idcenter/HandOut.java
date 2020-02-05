package idcenter;

import entities.Documentrequest;
import java.awt.event.*;
import java.awt.*;

public class HandOut implements ActionListener {
        private Interface gui;
    public HandOut(Interface gui){
        this.gui = gui;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String id = gui.selectedId.getText();
        java.util.List<Documentrequest> lst = Main.em.createNamedQuery("Documentrequest.findById")
                .setParameter("id", id).getResultList();
        if(lst.size() == 0){
            gui.showMessage("No such request in database");
            return;
        }
        Documentrequest dr = lst.get(0);
        dr.setStanje("urucen");
        Main.em.getTransaction().begin();
        Main.em.persist(dr);
        Main.em.flush();
        Main.em.getTransaction().commit();
        gui.status.setText("urucen");
    }
}
