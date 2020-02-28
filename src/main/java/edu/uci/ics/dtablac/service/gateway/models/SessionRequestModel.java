package edu.uci.ics.dtablac.service.gateway.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionRequestModel {
    @JsonProperty(value = "email", required = true)
    private String EMAIL;
    @JsonProperty(value = "session_id", required = true)
    private String SESSION_ID;

    @JsonCreator
    public SessionRequestModel(@JsonProperty(value = "email", required = true) String newEMAIL,
                               @JsonProperty(value = "session_id", required = true) String newSESSION_ID) {
        this.EMAIL = newEMAIL;
        this.SESSION_ID = newSESSION_ID;
    }

    @JsonProperty(value = "email")
    public String getEMAIL() { return EMAIL; }
    @JsonProperty(value = "session_id")
    public String getSESSION_ID() { return SESSION_ID; }
}
