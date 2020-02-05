package idcenter;


import entities.Documentrequest;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.*;
import javax.persistence.*;


public class Interface extends Frame {
        
    TextField jmbg,ime, prezime, imeMajke, imeOca, prezimeMajke, prezimeOca, 
            nacionalnost, profesija, opstina, ulica, broj;
    Choice brak, godina, mesec, dan;
    Checkbox muski, zenski;
    Button kreirajZahtev;
    
    TextField reqId;
    Label status, selectedId;
    Button traziStatus, uruciDokument;
    int ID = 0;
    public Interface(){
        super("IDCenter");
        
        Panel reqPan = new Panel(new FlowLayout(FlowLayout.CENTER));
        jmbg = new TextField(30);
        ime = new TextField(30);
        prezime = new TextField(30);
        imeMajke = new TextField(30);
        imeOca = new TextField(30);
        prezimeMajke = new TextField(30);
        prezimeOca = new TextField(30);
        nacionalnost = new TextField(30);
        profesija = new TextField(30);
        opstina = new TextField(30);
        ulica = new TextField(30);
        broj = new TextField(30);
        reqId = new TextField(30);
        muski = new Checkbox();
        muski.setState(true);
        zenski = new Checkbox();
        brak = new Choice();
        godina = new Choice();
        mesec = new Choice();
        dan = new Choice();
        kreirajZahtev = new Button("Kreiraj Zahtev");
        traziStatus = new Button("Trazi Status");
        uruciDokument = new Button("Uruci");
        uruciDokument.setEnabled(false);
        selectedId = new Label("");
        status = new Label("");
        
        
        Panel grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("JMBG:"));
        grid.add(jmbg);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("ime: "));
        grid.add(ime);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("prezime: "));
        grid.add(prezime);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("ime majke: "));
        grid.add(imeMajke);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("ime oca: "));
        grid.add(imeOca);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("prezime majke: "));
        grid.add(prezimeMajke);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("prezime oca: "));
        grid.add(prezimeOca);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("nacionalnost: "));
        grid.add(nacionalnost);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("profesija: "));
        grid.add(profesija);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("opstina prebivalista: "));
        grid.add(opstina);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("ulica prebivalista: "));
        grid.add(ulica);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("broj prebivalista: "));
        grid.add(broj);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("pol: "));
        Panel polPan = new Panel(new FlowLayout());
        polPan.add(new Label("M"));
        polPan.add(muski);
        polPan.add(new Label("Z"));
        polPan.add(zenski);
        CheckboxGroup cbg = new CheckboxGroup();
        muski.setCheckboxGroup(cbg);
        zenski.setCheckboxGroup(cbg);
        grid.add(polPan);
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("bracno stanje: "));
        grid.add(brak);
        brak.add("neozenjen/a");
        brak.add("ozenjen/udata");
        brak.add("razveden/a");
        brak.add("udovac/udovica");
        reqPan.add(grid);
        
        grid = new Panel(new FlowLayout(FlowLayout.CENTER));
        grid.add(new Label("datum rodjenja: "));
        Panel datPan = new Panel(new FlowLayout());
        datPan.add(new Label("godina: "));
        datPan.add(godina);
        for(int i = 1850; i <= LocalDate.now().getYear(); i++){
            godina.add(""+i);
        }
        datPan.add(new Label("mesec: "));
        datPan.add(mesec);
        for(int i = 1; i <= 12; i++){
            mesec.add(""+i);
        }
        datPan.add(new Label("dan: "));
        datPan.add(dan);
        for(int i = 1; i <= 31; i++){
            dan.add(""+i);
        }
        grid.add(datPan);
        reqPan.add(grid);
        
        reqPan.add(kreirajZahtev);
        add(reqPan, BorderLayout.CENTER);
        
        Panel chkPan = new Panel(new GridLayout(4,2));
        chkPan.add(new Label("Trenutno se gleda status za: "));
        chkPan.add(selectedId);
        chkPan.add(new Label("Status: "));
        chkPan.add(status);
        chkPan.add(new Label("ID: "));
        chkPan.add(reqId);
        chkPan.add(traziStatus);
        chkPan.add(uruciDokument);
        add(chkPan, BorderLayout.SOUTH);
        
        kreirajZahtev.addActionListener(new CreateRequest(this));
        traziStatus.addActionListener(new CheckStatus(this));
        uruciDokument.addActionListener(new HandOut(this));
        
        setVisible(true);
        setSize(455, 800);
        
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
            
        });
        
        java.util.List<Documentrequest> lst = Main.em.createNamedQuery("Documentrequest.findAll").getResultList();
        int curr = 0;
        for(Documentrequest dr: lst){
            curr = Integer.parseInt(dr.getId().substring(5));
            if(curr >= ID){
                ID = curr+1;
            }
        }
        
    }

    void showMessage(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
