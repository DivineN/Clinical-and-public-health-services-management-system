/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;

/**
 *
 * @author user
 */
@Entity
public class Appointment {

    @Id
    @GeneratedValue
    private Integer Id;
    private String Status="Pending";
    @OneToOne
    private Doctors doctors;
    @OneToOne
    private Patients patients;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date date = new Date();
//   the date when a patient want an appointment with doctor
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date app_date;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public Doctors getDoctors() {
        return doctors;
    }

    public void setDoctors(Doctors doctors) {
        this.doctors = doctors;
    }

    public Patients getPatients() {
        return patients;
    }

    public void setPatients(Patients patients) {
        this.patients = patients;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getApp_date() {
        return app_date;
    }

    public void setApp_date(Date app_date) {
        this.app_date = app_date;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }
    
    
}
