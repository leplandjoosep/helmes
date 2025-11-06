package com.helmes.task.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sector")
@Getter
@Setter
public class Sector {

    @Id
    private Long id;
    private String name;
    private Long parentId;

    @Transient
    private int depth;
    @Transient
    private List<Sector> children = new ArrayList<>();
}
