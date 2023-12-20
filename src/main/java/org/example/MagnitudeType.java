package org.example;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "magnitudeType")
public class MagnitudeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private int id;

    @Column(name = "MagnitudeType")
    private String magnitudeType;

    @OneToMany(mappedBy = "magnitudeType")
    private List<Earthquake> earthquakes = new ArrayList<>();
    public MagnitudeType(){}
    public MagnitudeType(String magnitudeType){
        this.magnitudeType = magnitudeType;
    }
    public String getMagnitudeType() {
        return magnitudeType;
    }

    public void setMagnitudeType(String magnitudeType) {
        this.magnitudeType = magnitudeType;
    }
}
