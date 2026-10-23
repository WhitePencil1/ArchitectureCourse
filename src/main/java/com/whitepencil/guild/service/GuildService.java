package com.whitepencil.guild.service;

import com.whitepencil.guild.exception.ApiException;
import com.whitepencil.guild.model.Guild;
import com.whitepencil.guild.model.Hero;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class GuildService {

    @PersistenceContext(unitName = "guildPU")
    private EntityManager em;

    public List<Guild> findAll() {
        return em.createQuery(
                "select g from Guild g order by g.id",
                Guild.class
        ).getResultList();
    }

    public Guild findById(Long id) {
        Guild guild = em.find(Guild.class, id);
        if (guild == null) {
            throw new ApiException(404, "Гильдия не найдена");
        }
        return guild;
    }

    public Guild create(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid guild properties"
            );
        }
        Guild guild = new Guild();
        guild.setName(name);
        guild.setDescription(description);

        em.persist(guild);
        em.flush();
        return guild;
    }

    public List<Hero> findAllGuildHeroes(Long guildId) {
        Guild guild = findById(guildId);
        return guild.getHeroes();
    }

}
