package com.whitepencil.guild.service;


import com.whitepencil.guild.dto.HeroResponse;
import com.whitepencil.guild.exception.ApiException;
import com.whitepencil.guild.model.Guild;
import com.whitepencil.guild.model.Hero;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;


// сообщает GlassFish, что это управляемый компонент бизнес слоя
@Stateless
public class HeroService {

    @PersistenceContext(unitName = "guildPU")
    private EntityManager em; // основной интерфейс Jakarta Persistence для работы с сущностями

    public List<HeroResponse> findAll() {
        return em.createQuery(
                "select h from Hero h order by h.id",
                Hero.class
                )
                .getResultList()
                .stream()
                .map(HeroResponse::new)
                .toList();
    }

    public Hero findById(Long id) {
        return em.find(Hero.class, id);
    }

    public HeroResponse create(String name, int level, int health) {
        if (name == null || name.isBlank()
                || level < 1 || health < 0) {
            throw new ApiException(400, "Некорректные характеристики героя");
        }
        Hero hero = new Hero();
        hero.setName(name);
        hero.setLevel(level);
        hero.setHealth(health);

        em.persist(hero);
        em.flush();

        return new HeroResponse(hero);
    }

    public void changeGuild(Long heroId, Long guildId) {
        Hero hero = em.find(Hero.class, heroId);
        if (hero == null) {
            throw new ApiException(404, "Герой не найден");
        }

        Guild guild = em.find(Guild.class, guildId);
        if (guild == null) {
            throw new ApiException(404, "Гильдия не найдена");
        }

        hero.setGuild(guild);
    }


}
