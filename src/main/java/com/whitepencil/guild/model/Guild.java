package com.whitepencil.guild.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guilds")
public class Guild {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 600)
    private String description;

    @OneToMany(mappedBy = "guild")
    private List<Hero> heroes = new ArrayList<>();


    public Guild() {

    }

    public Guild(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Hero> getHeroes() {
        return heroes;
    }

    public void setHeroes(List<Hero> heroes) {
        this.heroes = heroes;
    }

    public Long getId() {
        return id;
    }
}
