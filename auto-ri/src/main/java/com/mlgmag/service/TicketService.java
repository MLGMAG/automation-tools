package com.mlgmag.service;

import com.mlgmag.exception.TicketCreationException;
import com.mlgmag.jira.api.JiraInterface;
import com.mlgmag.jira.api.data.ticket.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Objects;

import static com.mlgmag.utils.ResponseUtils.logResponse;

@Service
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Value("${jira.releaseInstructionTemplateData.projectKey}")
    private String releaseTicketProjectKey;

    @Value("${jira.releaseInstructionTemplateData.issueType}")
    private String releaseTicketIssueType;

    @Value("${jira.releaseInstructionTemplateData.summary}")
    private String releaseTicketSummary;

    @Value("${jira.releaseInstructionTemplateData.label}")
    private String releaseTicketLabel;

    @Resource
    private JiraInterface jiraInterface;

    @SneakyThrows
    public String createReleaseTicket(String fixVersion) {
        ProjectField projectField = new ProjectField(releaseTicketProjectKey);
        FixVersion requestFixVersion = new FixVersion(fixVersion);
        IssueType issueType = new IssueType(releaseTicketIssueType);

        TicketCreationFields ticketCreationFields = TicketCreationFields.builder()
                .project(projectField)
                .issuetype(issueType)
                .summary(releaseTicketSummary)
                .fixVersions(Collections.singletonList(requestFixVersion))
                .labels(Collections.singletonList(releaseTicketLabel)).build();

        TicketCreationRequest request = TicketCreationRequest.builder().fields(ticketCreationFields).build();

        Response<TicketCreationResponse> response = jiraInterface.createReleaseTicket(request).execute();
        logResponse(response, LOG);

        if (Objects.isNull(response.body())) {
            throw new TicketCreationException("Ticket creation is failed!");
        }

        return response.body().getKey();
    }

}
