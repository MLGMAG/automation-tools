package com.mlgmag.jira.api.data.jql_search;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class JqlRequest {
    private String jql;
    private int maxResults;
    private boolean fieldsByKeys;
    private Set<String> fields;
    private int startAt;
}
