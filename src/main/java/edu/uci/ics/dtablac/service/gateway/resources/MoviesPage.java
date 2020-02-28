package edu.uci.ics.dtablac.service.gateway.resources;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.logger.ServiceLogger;
import edu.uci.ics.dtablac.service.gateway.util.movies;
import edu.uci.ics.dtablac.service.gateway.util.utility;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("movies")
public class MoviesPage {

    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@Context HttpHeaders headers, @Context UriInfo uri_info) {
        // Validate session
        Response response = utility.sendSessionVerification(headers);
        if (response != null) {
            return response;
        }
        return movies.callMovies(GatewayService.getMoviesConfigs().getSearchPath(), headers, uri_info);
    }

    @Path("browse/{phrase}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response browse(@Context HttpHeaders headers) {
        // call browse
        return null;
    }

    @Path("get/{movie_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieId(@Context HttpHeaders headers) {

        return null;
    }

    @Path("thumbnail")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response thumbnail(@Context HttpHeaders headers, byte[] jsonString) {
        // call thumbnail
        return null;
    }

    @Path("people/get/{person_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonId(@Context HttpHeaders headers) {
        // call get/person_id
        return null;
    }

    @Path("people/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleSearch(@Context HttpHeaders headers) {
        // call people/search
        return null;
    }

    @Path("people")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response people(@Context HttpHeaders headers) {

        // call people
        return null;
    }
}
