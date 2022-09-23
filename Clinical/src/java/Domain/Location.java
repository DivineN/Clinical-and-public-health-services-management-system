/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Domain;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Rugwiro
 */
@Entity
public class Location {
 @Id
private String LId=UUID.randomUUID().toString();
private String LocationName;
private String LocationType;
@ManyToOne
private Location location;




    @Override
    public String toString() {
        return LocationName;
    }

    public String getLId() {
        return LId;
    }

    public void setLId(String LId) {
        this.LId = LId;
    }

    public String getLocationName() {
        return LocationName;
    }

    public void setLocationName(String LocationName) {
        this.LocationName = LocationName;
    }

    public String getLocationType() {
        return LocationType;
    }

    public void setLocationType(String LocationType) {
        this.LocationType = LocationType;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }


}
