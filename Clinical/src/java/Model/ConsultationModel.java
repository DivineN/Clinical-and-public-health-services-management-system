/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Dao.ConsultationDao;
import Dao.DoctorsDao;
import Dao.LocationDao;
import Dao.UserDao;
import Domain.Consultation;
import Domain.Doctors;
import Domain.Location;
import Domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Rugwiro
 */
@ManagedBean
@SessionScoped
public class ConsultationModel {

    private Consultation consdetails = new Consultation();
    ConsultationDao consDao = new ConsultationDao();
    private List<Consultation> consList;
    private Consultation consultation = new Consultation();
    private Doctors load = new Doctors();
    List<Consultation> cons = new ArrayList<>();

    private List<Location> location = new LocationDao().findAll(Location.class);
    private List<Location> prov = new ArrayList<>();
    private List<Location> dis = new ArrayList<>();
    private String ProvId = new String();
    private String DistId = new String();

    Integer dropDiseases;
    

    @PostConstruct
    public void init() {

        prov = new ArrayList<>();
        for (Location loc : location) {
            if (loc.getLocationType().equals("PROVINCE")) {
                prov.add(loc);
            }
        }

        consList = new ConsultationDao().view("from Consultation a WHERE a.doctors.Id='" + loadName() + "'");
    }

    public Integer loadName() {
        User x = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
        load = x.getDoctors();
        Integer IdNum = load.getId();
        return IdNum;
    }

   
    public void distrPro() {
        dis = new ArrayList<>();
        for (Location loc : location) {
            if (loc.getLocation() != null && loc.getLocation().getLId().equals(ProvId)) {
                dis.add(loc);
            }
        }
    }

    public Location finalLoc() {
        for (Location loc : location) {
            if (loc.getLId().equals(DistId)) {
                return loc;
            }
        }
        return null;
    }

    public void createConsultation() {


        User x = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
        consultation.setLocation(finalLoc());
        consultation.setDoctors(x.getDoctors());
        new ConsultationDao().save(consultation);

        consultation = new Consultation();

        consList = new ConsultationDao().view("from Consultation a WHERE a.doctors.Id='" + loadName() + "'");
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Consultation Registered!", null));
    }

    public Consultation getConsdetails() {
        return consdetails;
    }

    public void setConsdetails(Consultation consdetails) {
        this.consdetails = consdetails;
    }

    public ConsultationDao getConsDao() {
        return consDao;
    }

    public void setConsDao(ConsultationDao consDao) {
        this.consDao = consDao;
    }

    public List<Consultation> getConsList() {
        return consList;
    }

    public void setConsList(List<Consultation> consList) {
        this.consList = consList;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public Doctors getLoad() {
        return load;
    }

    public void setLoad(Doctors load) {
        this.load = load;
    }

    public List<Location> getLocation() {
        return location;
    }

    public void setLocation(List<Location> location) {
        this.location = location;
    }

    public List<Location> getProv() {
        return prov;
    }

    public void setProv(List<Location> prov) {
        this.prov = prov;
    }

    public List<Location> getDis() {
        return dis;
    }

    public void setDis(List<Location> dis) {
        this.dis = dis;
    }

    public String getProvId() {
        return ProvId;
    }

    public void setProvId(String ProvId) {
        this.ProvId = ProvId;
    }

    public String getDistId() {
        return DistId;
    }

    public void setDistId(String DistId) {
        this.DistId = DistId;
    }

    public List<Consultation> getCons() {
        return cons;
    }

    public void setCons(List<Consultation> cons) {
        this.cons = cons;
    }

    public Integer getDropDiseases() {
        return dropDiseases;
    }

    public void setDropDiseases(Integer dropDiseases) {
        this.dropDiseases = dropDiseases;
    }

}
