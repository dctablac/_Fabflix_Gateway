package edu.uci.ics.dtablac.service.gateway.resources;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.util.movies;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("movies")
public class MoviesPage {

    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response search(@Context HttpHeaders headers, @Context UriInfo uri_info) {
        return movies.callMovies(GatewayService.getMoviesConfigs().getSearchPath(), headers, uri_info);
    }

    @Path("browse/{phrase: .*}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response browse(@Context HttpHeaders headers, @Context UriInfo uri_info) {
        return movies.callMovies(GatewayService.getMoviesConfigs().getBrowsePath(), headers, uri_info);
    }

    @Path("get/{movie_id: .*}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMovieId(@Context HttpHeaders headers, @Context UriInfo uri_info) {
        return movies.callMovies(GatewayService.getMoviesConfigs().getGetPath(), headers, uri_info);
    }

    @Path("thumbnail")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response thumbnail(@Context HttpHeaders headers, byte[] jsonString) {
        return movies.callThumbnail(GatewayService.getMoviesConfigs().getThumbnailPath(), headers, jsonString);
    }

    @Path("people/get/{person_id: .*}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPersonId(@Context HttpHeaders headers, @Context UriInfo uri_info) {
        return movies.callMovies(GatewayService.getMoviesConfigs().getPeopleGetPath(), headers, uri_info);
    }

    @Path("people/search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response peopleSearch(@Context HttpHeaders headers, @Context UriInfo uri_info) {
        return movies.callMovies(GatewayService.getMoviesConfigs().getPeopleSearchPath(), headers, uri_info);
    }

    @Path("people")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response people(@Context HttpHeaders headers, @Context UriInfo uri_info) {
        return movies.callMovies(GatewayService.getMoviesConfigs().getPeoplePath(), headers, uri_info);
    }
}
