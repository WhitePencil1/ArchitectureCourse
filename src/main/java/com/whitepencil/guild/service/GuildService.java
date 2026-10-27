package com.whitepencil.guild.service;

import com.whitepencil.guild.dto.GuildResponse;
import com.whitepencil.guild.dto.GuildUpdateRequest;
import com.whitepencil.guild.dto.HeroResponse;
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

    public List<GuildResponse> findAll() {
        return em.createQuery(
                "select g from Guild g order by g.id",
                Guild.class
        )
                .getResultList()
                .stream()
                .map(GuildResponse::new)
                .toList();
    }

    public Guild findById(Long id) {
        Guild guild = em.find(Guild.class, id);
        if (guild == null) {
            throw new ApiException(404, "Гильдия не найдена");
        }
        return guild;
    }

    public GuildResponse getById(Long id) {
        Guild guild = findById(id);
        return new GuildResponse(guild);
    }

    public boolean nameExists(String name) {
        Long existingNamesCount = em.createQuery(
                "select count(g) from Guild g where g.name = :name",
                Long.class
        )
                .setParameter("name", name)
                .getSingleResult();
        return existingNamesCount > 0;
    }

    public GuildResponse create(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new ApiException(400, "Название гильдии обязательно");
        }

        name = name.strip();
        if (name.length() > 100) {
            throw new ApiException(400, "Название слишком длинное");
        }
        if (nameExists(name)) {
            throw new ApiException(409, "Гильдия с таким именем уже существует");
        }

        Guild guild = new Guild();
        guild.setName(name);
        guild.setDescription(description);

        em.persist(guild);
        em.flush();
        return new GuildResponse(guild);
    }

    public GuildResponse update(Long guildId, String name, String description) {
        if (name == null || name.isBlank()) {
            throw new ApiException(400, "Некорректные значения полей");
        }

        name = name.strip();
        if (name.length() > 100) {
            throw new ApiException(400, "Слишком длинное имя гильдии");
        }

        Guild guild = findById(guildId);
        Long count = em.createQuery(
                        """
                        select count(g) from Guild g
                        where g.name = :name and g.id <> :id
                        """,
                        Long.class
                )
                .setParameter("name", name)
                .setParameter("id", guildId)
                .getSingleResult();
        if (count > 0) {
            throw new ApiException(409, "Гильдия с таким именем уже существует");
        }
        guild.setName(name);
        guild.setDescription(description);
        return new GuildResponse(guild);
    }

    public List<HeroResponse> findAllGuildHeroes(Long guildId) {
        Guild guild = findById(guildId);
        return guild.getHeroes().stream().map(HeroResponse::new).toList();
    }

    public void delete(Long guildId) {
        Guild guild = findById(guildId);
        em.remove(guild);
    }
}
