package com.mlgmag.jira.api.data.jql_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentsWrapper {
    private Set<CommentData> comments;
}
