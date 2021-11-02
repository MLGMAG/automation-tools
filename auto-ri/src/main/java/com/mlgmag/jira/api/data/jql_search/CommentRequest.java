package com.mlgmag.jira.api.data.jql_search;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentRequest {
    private String body;
}
