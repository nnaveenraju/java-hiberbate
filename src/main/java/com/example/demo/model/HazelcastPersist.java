package com.example.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "HazelcastPersist") //, uniqueConstraints = @UniqueConstraint(columnNames = {"MapName","MapKey"}))
public class HazelcastPersist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PersistID")
    private Long persistID;

    @Column(name = "PersistenceLocation")
    private String persistenceLocation;

    @Column(name = "MapKey")
    private String mapKey;

    @Column(name = "MapValue", columnDefinition="TEXT" )
    private String mapValue;

    public Long getPersistID() {
        return persistID;
    }

    public void setPersistID(Long persistID) {
        this.persistID = persistID;
    }

    public String getPersistenceLocation() {
        return persistenceLocation;
    }

    public void setPersistenceLocation(String persistenceLocation) {
        this.persistenceLocation = persistenceLocation;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

    public String getMapValue() {
        return mapValue;
    }

    public void setMapValue(String mapValue) {
        this.mapValue = mapValue;
    }

    @Override
    public String toString() {
        return "HazelcastPersist{" +
                "persistID=" + persistID +
                ", persistenceLocation='" + persistenceLocation + '\'' +
                ", mapKey='" + mapKey + '\'' +
                ", mapValue='" + mapValue + '\'' +
                '}';
    }
}
