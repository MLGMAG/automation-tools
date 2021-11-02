package com.mlgmag.jira.api.data.ticket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketCreationResponse {
    private String id;
    private String key;
}
