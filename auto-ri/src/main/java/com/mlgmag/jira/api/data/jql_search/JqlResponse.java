package com.mlgmag.jira.api.data.jql_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JqlResponse {
    @JsonUnwrapped
    private List<IssueData> issues;
}
