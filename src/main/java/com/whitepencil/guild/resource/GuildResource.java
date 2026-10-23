package com.whitepencil.guild.resource;

import com.whitepencil.guild.dto.GuildResponse;
import com.whitepencil.guild.dto.GuildUpdateRequest;
import com.whitepencil.guild.dto.HeroResponse;
import com.whitepencil.guild.exception.ApiException;
import com.whitepencil.guild.model.Guild;
import com.whitepencil.guild.service.GuildService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;


@Path("/guilds")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuildResource {

    @EJB
    private GuildService service;

    @GET
    public List<GuildResponse> getAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        GuildResponse guild = service.getById(id);
        return Response.ok(guild).build();
    }

    @POST
    public Response createGuild(Guild request) {
        if (request == null) {
            throw new ApiException(400, "Отсутствует тело запроса");
        }

        GuildResponse guild = service.create(
                request.getName(),
                request.getDescription()
        );
        return Response.status(201).entity(guild).build();
    }

    @GET
    @Path("/{guildId}/heroes")
    public List<HeroResponse> getGuildHeroes(
            @PathParam("guildId") Long guildId
    ) {
        return service.findAllGuildHeroes(guildId);
    }

    @DELETE
    @Path("/{guildId}")
    public Response deleteGuild(@PathParam("guildId") Long guildId) {
        service.delete(guildId);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{guildId}")
    public Response updateGuild(@PathParam("guildId") Long guildId, GuildUpdateRequest request) {
        if (request == null) {
            throw new ApiException(400, "Тело запроса обязательно");
        }
        GuildResponse updatedGuild = service.update(guildId, request.name, request.description);
        return Response.ok(updatedGuild).build();
    }

}
