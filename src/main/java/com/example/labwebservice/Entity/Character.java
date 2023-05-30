package com.example.labwebservice.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "characters")
public class Character {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private Integer id;


    @Basic
    @Column(name = "name")
    private String name;

    @Basic
    @Column(name = "url")
    private String url;

    @Basic
    @Column(name = "identity")
    private String identity;


    @Basic
    @Column(name = "align")
    private String align;


    @Basic
    @Column(name = "eye")
    private String eye;


    @Basic
    @Column(name = "hair")
    private String hair;

    @Basic
    @Column(name = "sex")
    private String sex;

    @Basic
    @Column(name = "gsm")
    private String gsm;


    @Basic
    @Column(name = "alive")
    private String alive;


    @Basic
    @Column(name = "appearances")
    private Integer appearances;


    @Basic
    @Column(name = "first_appearance")
    private String firstAppearance;

    @Basic
    @Column(name = "year")
    private Integer year;

}
