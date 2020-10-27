package edu.uci.ics.dtablac.service.gateway.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionResponseModel {
    @JsonProperty(value = "resultCode")
    private int RESULTCODE;
    @JsonProperty(value = "message")
    private String MESSAGE;
    @JsonProperty(value = "session_id")
    private String SESSION_ID;

    @JsonCreator
    public SessionResponseModel(@JsonProperty(value = "resultCode", required = true) int newRESULTCODE,
                                @JsonProperty(value = "message", required = true) String newMESSAGE,
                                @JsonProperty(value = "session_id") String newSESSION_ID) {
        this.RESULTCODE = newRESULTCODE;
        this.MESSAGE = newMESSAGE;
        this.SESSION_ID = newSESSION_ID;
    }

    @JsonProperty(value = "resultCode")
    public int getRESULTCODE() { return RESULTCODE; }
    @JsonProperty(value = "message")
    public String getMESSAGE() { return MESSAGE; }
    @JsonProperty(value = "session_id")
    public String getSESSION_ID() { return SESSION_ID; }
}
