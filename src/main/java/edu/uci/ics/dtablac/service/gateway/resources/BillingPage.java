package edu.uci.ics.dtablac.service.gateway.resources;

import edu.uci.ics.dtablac.service.gateway.GatewayService;
import edu.uci.ics.dtablac.service.gateway.util.billing;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("billing")
public class BillingPage {

    @Path("cart/insert")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cartInsert(@Context HttpHeaders headers, byte[] jsonBytes) {
        return billing.callBilling(GatewayService.getBillingConfigs().getCartInsertPath(), headers, jsonBytes);
    }

    @Path("cart/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cartUpdate(@Context HttpHeaders headers, byte[] jsonBytes) {
        return billing.callBilling(GatewayService.getBillingConfigs().getCartUpdatePath(), headers, jsonBytes);
    }


    @Path("cart/delete")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cartDelete(@Context HttpHeaders headers, byte[] jsonBytes) {
        return billing.callBilling(GatewayService.getBillingConfigs().getCartDeletePath(), headers, jsonBytes);
    }


    @Path("cart/retrieve")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cartRetrieve(@Context HttpHeaders headers, byte[] jsonBytes) {
        return billing.callBilling(GatewayService.getBillingConfigs().getCartRetrievePath(), headers, jsonBytes);
    }


    @Path("cart/clear")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response cartClear(@Context HttpHeaders headers, byte[] jsonBytes) {
        return billing.callBilling(GatewayService.getBillingConfigs().getCartClearPath(), headers, jsonBytes);
    }


    @Path("order/place")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response orderPlace(@Context HttpHeaders headers, byte[] jsonBytes) {
        return billing.callBilling(GatewayService.getBillingConfigs().getOrderPlacePath(), headers, jsonBytes);
    }


    @Path("order/retrieve")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response orderRetrieve(@Context HttpHeaders headers, byte[] jsonBytes) {
        return billing.callBilling(GatewayService.getBillingConfigs().getOrderRetrievePath(), headers, jsonBytes);
    }


    @Path("order/complete")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response orderComplete(@Context HttpHeaders headers, @Context UriInfo uri_info) {
        return billing.callComplete(GatewayService.getBillingConfigs().getOrderPlacePath(), headers, uri_info);
    }
}
