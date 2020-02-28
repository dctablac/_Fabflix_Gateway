package edu.uci.ics.dtablac.service.gateway.threadpool;

import javax.ws.rs.core.UriInfo;

public class ClientRequest
{
    /* User Information */
    private String email;
    private String session_id;
    private String transaction_id;

    /* Target Service and Endpoint */
    private String URI;
    private String endpoint;
    private HTTPMethod method;
    private UriInfo uri_info;

    /*
     * So before when we wanted to get the request body
     * we would grab it as a String (String jsonText).
     *
     * The Gateway however does not need to see the body
     * but simply needs to pass it. So we save ourselves some
     * time and overhead by grabbing the request as a byte array
     * (byte[] jsonBytes).
     *
     * This way we can just act as a
     * messenger and just pass along the bytes to the target
     * service and it will do the rest.
     *
     * for example:
     *
     * where we used to do this:
     *
     *     @Path("hello")
     *     ...ect
     *     public Response hello(String jsonString) {
     *         ...ect
     *     }
     *
     * do:
     *
     *     @Path("hello")
     *     ...ect
     *     public Response hello(byte[] jsonBytes) {
     *         ...ect
     *     }
     *
     */
    private byte[] requestBytes; // TODO

    public ClientRequest(String newEmail, String newSession_id, String newTransaction_id,
                         String newURI, String newEndpoint, HTTPMethod newMethod, UriInfo uri_info, byte[] newRequestBytes)
    {
        this.email = newEmail;
        this.session_id = newSession_id;
        this.transaction_id = newTransaction_id;
        this.URI = newURI;
        this.endpoint = newEndpoint;
        this.method = newMethod;
        this.uri_info = uri_info;
        this.requestBytes = newRequestBytes;
    }

    public String getEmail() {
        return this.email;
    }

    public String getSession_id() {
        return this.session_id;
    }

    public String getTransaction_id() {
        return this.transaction_id;
    }

    public String getURI() {
        return this.URI;
    }

    public String getEndpoint() {
        return this.endpoint;
    }

    public HTTPMethod getMethod() {
        return this.method;
    }

    public UriInfo getUri_info() { return this.uri_info; };

    public byte[] getRequestBytes() {
        return this.requestBytes;
    }
}
