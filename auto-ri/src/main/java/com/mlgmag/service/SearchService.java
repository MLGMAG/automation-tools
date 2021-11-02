package com.mlgmag.service;

import com.mlgmag.exception.ReleaseInstructionNotFoundException;
import com.mlgmag.jira.api.JiraInterface;
import com.mlgmag.jira.api.data.jql_search.IssueData;
import com.mlgmag.jira.api.data.jql_search.JqlRequest;
import com.mlgmag.jira.api.data.jql_search.JqlResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import javax.annotation.Resource;
import java.util.*;

import static com.mlgmag.utils.ResponseUtils.logResponse;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;

@Service
public class SearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchService.class);

    private static final String RELEASE_INSTRUCTION_JQL_PATTERN = "fixVersion = '%s' AND labels in ('ReleaseInstruction') ORDER BY created DESC";
    private static final String TICKETS_WITH_FIX_VERSION_JQL_PATTERN = "fixVersion = '%s' AND (labels not in ('ReleaseInstruction') OR labels is EMPTY) ORDER BY created DESC";

    private final Set<String> issueFields = new HashSet<>(asList("summary", "attachment", "comment"));

    private static final String NO_INSTRUCTION_WITH_FIX_VER_PATTERN = "No release instruction with fixVersion '%s' found!";

    @Resource
    private JiraInterface jiraInterface;

    @SneakyThrows
    public List<IssueData> searchIssuesByFixVersion(String fixVersion) {
        String jql = String.format(TICKETS_WITH_FIX_VERSION_JQL_PATTERN, fixVersion);

        JqlRequest request = JqlRequest.builder()
                .maxResults(500)
                .startAt(0)
                .fieldsByKeys(false)
                .fields(issueFields)
                .jql(jql)
                .build();

        Response<JqlResponse> response = jiraInterface.search(request).execute();
        logResponse(response, LOG);

        return isNull(response.body()) ? Collections.emptyList() : response.body().getIssues();
    }

    @SneakyThrows
    public Optional<IssueData> searchReleaseInstruction(String fixVersion) {
        String jql = String.format(RELEASE_INSTRUCTION_JQL_PATTERN, fixVersion);

        JqlRequest request = JqlRequest.builder()
                .maxResults(1)
                .startAt(0)
                .fieldsByKeys(false)
                .fields(issueFields)
                .jql(jql)
                .build();

        Response<JqlResponse> response = jiraInterface.search(request).execute();
        logResponse(response, LOG);

        return isNull(response.body()) ? Optional.empty() : response.body().getIssues().stream().findFirst();
    }

    public IssueData searchReleaseInstructionOrThrowException(String fixVersion) {
        return searchReleaseInstruction(fixVersion).orElseThrow(() -> {
            String errorMessage = String.format(NO_INSTRUCTION_WITH_FIX_VER_PATTERN, fixVersion);
            return new ReleaseInstructionNotFoundException(errorMessage);
        });
    }
}
