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
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;

/**
 *
 * @author Rugwiro
 */
@Entity
public class Consultation {

    @Id
    @GeneratedValue
    private Integer Id;
    private String Description, Patients_Name, Gender, Phone;
    @OneToOne
    private Doctors doctors;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date date = new Date();
    @ManyToOne
    private Location location;
    @Transient
    private String checkhospital;

    public Integer getId() {
        return Id;
    }

    public void setId(Integer Id) {
        this.Id = Id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public Doctors getDoctors() {
        return doctors;
    }

    public void setDoctors(Doctors doctors) {
        this.doctors = doctors;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPatients_Name() {
        return Patients_Name;
    }

    public void setPatients_Name(String Patients_Name) {
        this.Patients_Name = Patients_Name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String Gender) {
        this.Gender = Gender;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getCheckhospital() {
        return checkhospital;
    }

    public void setCheckhospital(String checkhospital) {
        this.checkhospital = checkhospital;
    }

}
