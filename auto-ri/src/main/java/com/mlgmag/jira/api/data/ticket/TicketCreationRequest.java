package com.mlgmag.jira.api.data.ticket;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketCreationRequest {
    private TicketCreationFields fields;
}
