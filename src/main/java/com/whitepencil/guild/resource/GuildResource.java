package com.whitepencil.guild.resource;

import com.whitepencil.guild.model.Guild;
import com.whitepencil.guild.model.Hero;
import com.whitepencil.guild.service.GuildService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

@Path("/guilds")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuildResource {

    @EJB
    private GuildService service;

    @GET
    public List<Guild> getAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        Guild guild = service.findById(id);
        if (guild == null) {
            return Response.status(404).build();
        }
        return Response.ok(guild).build();
    }

    @POST
    public Response createGuild(Guild request) throws SQLException {
        if (request == null) {
            return Response.status(400).build();
        }

        try {
            Guild guild = service.create(
                    request.getName(),
                    request.getDescription()
            );
            return Response.status(201).entity(guild).build();
        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity(java.util.Map.of(
                            "error", e.getMessage()
                    ))
                    .build();
        }
    }

    @GET
    @Path("/{guildId}/heroes")
    public List<Hero> getGuildHeroes(
            @PathParam("guildId") Long guildId
    ) {
        return service.findAllGuildHeroes(guildId);
    }

}
