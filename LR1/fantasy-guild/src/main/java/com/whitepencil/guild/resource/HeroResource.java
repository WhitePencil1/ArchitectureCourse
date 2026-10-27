package com.whitepencil.guild.resource;

import com.whitepencil.guild.dto.HeroResponse;
import com.whitepencil.guild.dto.HeroUpdateRequest;
import com.whitepencil.guild.exception.ApiException;
import com.whitepencil.guild.model.Hero;
import com.whitepencil.guild.service.HeroService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
        HeroResponse hero = service.getById(id);
        return Response.ok(hero).build();
    }

    @POST
    public Response createHero(Hero request) {
        if (request == null) {
            throw new ApiException(400, "Отсутствует тело запроса");
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

    @DELETE
    @Path("/{heroId}/guild")
    public Response leaveGuild(
            @PathParam("heroId") Long heroId
    ) {
        service.leaveGuild(heroId);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{heroId}")
    public Response deleteHero(
            @PathParam("heroId") Long heroId
    ) {
        service.delete(heroId);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{heroId}")
    public Response heroUpdate(@PathParam("heroId") Long heroId, HeroUpdateRequest request) {
        if (request == null) {
            throw new ApiException(400, "Тело запроса обязательно");
        }
        HeroResponse hero = service.update(
                heroId,
                request.name,
                request.level,
                request.health
        );

        return Response.ok(hero).build();
    }
}
