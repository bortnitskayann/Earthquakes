package org.example;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "earthquakes")
public class Earthquake {
    @Id
    @Column(name = "Id")
    private String id;

    @Column(name = "Depth")
    private int depth;

    @ManyToOne
    @JoinColumn(name = "MagnitudeTypeId")
    private MagnitudeType magnitudeType;

    @Column(name = "Magnitude")
    private float magnitude;

    @Column(name = "State")
    private String state;

    @Column(name = "DateTime")
    private Date dateTime;

    public Earthquake(){}

    public Earthquake(String id, int depth, MagnitudeType magnitudeType, float magnitude, String state, Date dateTime){
        this.id = id;
        this.depth = depth;
        this.magnitudeType = magnitudeType;
        this.magnitude = magnitude;
        this.state = state;
        this.dateTime = dateTime;
    }
    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public float getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }
}
