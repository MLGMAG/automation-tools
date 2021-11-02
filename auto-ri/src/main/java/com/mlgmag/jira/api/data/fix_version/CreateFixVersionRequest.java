package com.mlgmag.jira.api.data.fix_version;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateFixVersionRequest {
    private String name;
    private String project;
}
