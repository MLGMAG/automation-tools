package com.mlgmag.jira.api.data.jql_search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AttachmentData {
    private String id;
    private String filename;
    private String content;
}
