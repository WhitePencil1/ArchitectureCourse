package com.whitepencil.guild.dto;

import com.whitepencil.guild.model.Hero;

public class HeroResponse {
    public Long id;
    public String name;
    public int level;
    public int health;
    public Long guildId;

    public HeroResponse(Hero hero) {
        id = hero.getId();
        name = hero.getName();
        level = hero.getLevel();
        health = hero.getHealth();

        guildId = hero.getGuild() == null
                ? null
                : hero.getGuild().getId();
    }
}
