package edu.uci.ics.dtablac.service.gateway.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseModel {
    @JsonProperty(value = "resultCode", required = true)
    private int RESULTCODE;
    @JsonProperty(value = "message", required = true)
    private String MESSAGE;

    @JsonCreator
    public ResponseModel(@JsonProperty(value = "resultCode", required = true) int newRESULTCODE,
                         @JsonProperty(value = "message", required = true) String newMESSAGE) {
        this.RESULTCODE = newRESULTCODE;
        this.MESSAGE = newMESSAGE;
    }

    @JsonProperty(value = "resultCode")
    public int getRESULTCODE() { return RESULTCODE; }
    @JsonProperty(value = "message")
    public String getMESSAGE() { return MESSAGE; }
}
