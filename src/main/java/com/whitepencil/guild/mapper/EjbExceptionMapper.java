package com.whitepencil.guild.mapper;

import jakarta.ejb.EJBException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@Provider
public class EjbExceptionMapper // Обработчик для внутренних ошибок сервисов (соединение с БД, сеть и т.д.)
        implements ExceptionMapper<EJBException> {

    private static final Logger LOG =
            Logger.getLogger(EjbExceptionMapper.class.getName());

    @Override
    public Response toResponse(EJBException exception) {
        LOG.log(Level.SEVERE, "Ошибка выполнения EJB", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(Map.of(
                        "error", "Внутренняя ошибка сервера"
                ))
                .build();
    }
}