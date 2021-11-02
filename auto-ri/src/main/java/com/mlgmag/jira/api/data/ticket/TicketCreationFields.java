package com.mlgmag.jira.api.data.ticket;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TicketCreationFields {
    private ProjectField project;
    private IssueType issuetype;
    private String summary;
    private List<FixVersion> fixVersions;
    private List<String> labels;
}
