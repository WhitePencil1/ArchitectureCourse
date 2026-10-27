package com.whitepencil.guild.dto;

import com.whitepencil.guild.model.Guild;
import com.whitepencil.guild.model.Hero;

import java.util.List;
import java.util.Set;

public class GuildResponse {
    public Long id;
    public String name;
    public String description;

    public GuildResponse(Guild guild) {
        id = guild.getId();
        name = guild.getName();
        description = guild.getDescription();
    }
}
