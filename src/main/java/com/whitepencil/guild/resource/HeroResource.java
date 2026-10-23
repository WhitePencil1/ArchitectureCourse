package com.whitepencil.guild.resource;


import com.whitepencil.guild.dto.HeroResponse;
import com.whitepencil.guild.model.Hero;
import com.whitepencil.guild.service.HeroService;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/heroes")
@RequestScoped // управление жизненным циклом объекта в рамках HTTP-запроса
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeroResource {

    @EJB
    private HeroService service;

    @GET
    public List<HeroResponse> getAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Hero hero = service.findById(id);

        if (hero == null) {
           return Response.status(404).build();
        }

        return Response.ok(hero).build();
    }

    @POST
    public Response createHero(Hero request) throws SQLException {
        if (request == null) {
            return Response.status(400).build();
        }

        try {
            HeroResponse hero = service.create(
                    request.getName(),
                    request.getLevel(),
                    request.getHealth()
            );
            return Response.status(201).entity(hero).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity(java.util.Map.of(
                            "error", e.getMessage()
                    ))
                    .build();
        }
    }

    @PUT
    @Path("/{heroId}/guild/{guildId}")
    public Response joinGuild(
            @PathParam("heroId") Long heroId,
            @PathParam("guildId") Long guildId
    ) {
        service.changeGuild(heroId, guildId);
        return Response.noContent().build();
    }


}
