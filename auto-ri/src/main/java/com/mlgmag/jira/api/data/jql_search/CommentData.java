package com.mlgmag.jira.api.data.jql_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentData {
    private String id;
    private String body;
    private String created;
}
