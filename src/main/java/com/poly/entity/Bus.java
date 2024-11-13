package com.poly.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name",columnDefinition = "nvarchar(255)")
    private String name;
    
    @Column(name = "license_plate",columnDefinition = "nvarchar(255)")
    private String licensePlate;

    @Column(name = "capacity")
    private int capacity;
    
    @Column(name = "avatar",columnDefinition = "nvarchar(255)")
    private String avatar;
    


    public String getDisplayName() {
        return name + " - sức chứa : " + capacity;
    }
    // ...
}
