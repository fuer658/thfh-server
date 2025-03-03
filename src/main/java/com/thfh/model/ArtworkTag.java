package com.thfh.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "artwork_tags")
public class ArtworkTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    private Boolean enabled = true;
}