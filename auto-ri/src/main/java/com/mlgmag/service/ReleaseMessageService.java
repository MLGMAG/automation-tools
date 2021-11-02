package com.mlgmag.service;

import com.mlgmag.jira.api.data.jql_search.IssueData;
import com.mlgmag.utils.JiraUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.mlgmag.constatants.JiraConstants.RELEASE_INSTRUCTION_KEY_WORD;
import static com.mlgmag.utils.JiraUtils.*;

@Service
public class ReleaseMessageService {

    private static final String RELEASE_MESSAGE_PATTERN = "%s\n\n%s\n\n%s\n\n%s\n\n";

    public String generateReleaseMessage(IssueData issueWithReleaseComment) {
        String title = extractTitle(issueWithReleaseComment);

        String releaseComment = getLastCreatedReleaseComment(issueWithReleaseComment);
        String attachments = extractAttachments(releaseComment);
        String message = extractMessageFieldFromReleaseComment(releaseComment);

        String formattedMessage = String.format(RELEASE_MESSAGE_PATTERN, RELEASE_INSTRUCTION_KEY_WORD, title, message, attachments);
        return removeExtraNewLines(formattedMessage);
    }

    private String extractTitle(IssueData releaseIssue) {
        String titleFormat = "%s. %s.";
        return String.format(titleFormat, releaseIssue.getKey(), releaseIssue.getFields().getSummary());
    }

    private String extractAttachments(String releaseComment) {
        List<String> attachmentsFilenames = extractAttachmentsFilenamesFromReleaseComment(releaseComment);
        return attachmentsFilenames.stream()
                .map(JiraUtils::convertFilenameIntoJiraFormat)
                .collect(Collectors.joining("\n"));
    }
}
