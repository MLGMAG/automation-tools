package com.mlgmag.jira.api.data.comment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentContentData {
    private String type;
    private String text;
    private Set<CommentContentData> content;
}
