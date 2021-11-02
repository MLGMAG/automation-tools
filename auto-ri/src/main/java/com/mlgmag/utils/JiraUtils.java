package com.mlgmag.utils;

import com.mlgmag.jira.api.data.jql_search.AttachmentData;
import com.mlgmag.jira.api.data.jql_search.CommentData;
import com.mlgmag.jira.api.data.jql_search.IssueData;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mlgmag.constatants.JiraConstants.*;

public final class JiraUtils {

    private static final String FILENAME_LEFT_CORNER = "[^";
    private static final String KEYWORD_LEFT_CORNER = "[";
    private static final String KEYWORD_RIGHT_CORNER = "]";
    private static final String EMPTY_STRING = "";
    private static final String NEW_LINE = "\n";
    private static final String SPACE = " ";

    private JiraUtils() {
    }

    public static String getLastCreatedReleaseComment(IssueData issueData) {

        Comparator<CommentData> commentDataComparator = (CommentData cd1, CommentData cd2) -> {
            LocalDateTime cdDate1 = LocalDateTime.parse(cd1.getCreated().substring(0, 19));
            LocalDateTime cdDate2 = LocalDateTime.parse(cd2.getCreated().substring(0, 19));
            return cdDate2.compareTo(cdDate1);
        };

        return issueData.getFields()
                .getComment()
                .getComments().stream()
                .filter(commentData -> commentData.getBody().contains(RELEASE_KEY_WORD))
                .sorted(commentDataComparator)
                .map(CommentData::getBody)
                .findFirst()
                .orElse(EMPTY_STRING);
    }

    /**
     * Extract attachments filenames from release comment.
     * Or returns empty list on no attachments.
     */
    public static List<String> extractAttachmentsFilenamesFromReleaseComment(String commentBody) {
        if (!commentBody.contains(FILENAME_LEFT_CORNER)) {
            return Collections.emptyList();
        }

        String processString = commentBody;

        List<String> filenames = new ArrayList<>();

        while (processString.contains(FILENAME_LEFT_CORNER)) {
            String filenameWithTail = processString.substring(processString.indexOf(FILENAME_LEFT_CORNER) + FILENAME_LEFT_CORNER.length());
            String filename = filenameWithTail.substring(0, filenameWithTail.indexOf(KEYWORD_RIGHT_CORNER));
            filenames.add(filename);
            processString = processString.substring(processString.indexOf(filename) + filename.length() + 1);
        }

        return filenames;
    }

    public static String convertFilenameIntoJiraFormat(String filename) {
        return FILENAME_LEFT_CORNER + filename + KEYWORD_RIGHT_CORNER;
    }

    public static List<IssueData> getIssuesWithReleaseComment(List<IssueData> issuesWithReleaseFixVersion, String fixVersion) {
        return issuesWithReleaseFixVersion.stream()
                .filter(issueData -> isIssueContainsReleaseComment(issueData, fixVersion))
                .collect(Collectors.toList());
    }

    private static boolean isIssueContainsReleaseComment(IssueData issue, String fixVersion) {
        return issue.getFields()
                .getComment()
                .getComments().stream()
                .anyMatch(commentData -> isCommentDataValid(commentData, fixVersion));
    }

    private static boolean isCommentDataValid(CommentData commentData, String fixVersion) {
        String body = commentData.getBody();
        if (body.contains(RELEASE_KEY_WORD)) {
            String releaseReleaseField = extractReleaseFieldFromReleaseComment(body);
            return releaseReleaseField.equals(fixVersion);
        }
        return false;
    }

    /**
     * Extracts value on message field "[Message]:" from release comment.
     * Or returns empty string.
     */
    public static String extractMessageFieldFromReleaseComment(String commentBody) {

        if (!commentBody.contains(MESSAGE_KEY_WORD)) {
            return EMPTY_STRING;
        }

        String messageWithTail = commentBody.substring(commentBody.indexOf(MESSAGE_KEY_WORD) + MESSAGE_KEY_WORD.length());

        if (!messageWithTail.contains(KEYWORD_LEFT_CORNER)) {
            return messageWithTail;
        }

        if (messageWithTail.contains("\\[")) {
            return messageWithTail.substring(0, messageWithTail.indexOf("\\["));
        }

        return messageWithTail.substring(0, messageWithTail.indexOf(KEYWORD_LEFT_CORNER));
    }

    /**
     * Removes sequential new line symbols from input string and leaves only one "\n" or two sequential new lines "\n\n".
     */
    public static String removeExtraNewLines(String formattedMessage) {
        String extraNewLinePattern = "\n\n+";
        return formattedMessage.replaceAll(extraNewLinePattern, "\n\n");
    }

    /**
     * Extracts release field "[Release]:" value from comment body.
     */
    public static String extractReleaseFieldFromReleaseComment(String commentBody) {
        String filteredCommentBody = commentBody.replace(NEW_LINE, EMPTY_STRING).replace(SPACE, EMPTY_STRING);
        String releaseWithTail = filteredCommentBody.substring(filteredCommentBody.indexOf(RELEASE_KEY_WORD) + RELEASE_KEY_WORD.length());

        if (!releaseWithTail.contains(KEYWORD_LEFT_CORNER)) {
            return releaseWithTail;
        }

        return releaseWithTail.substring(0, releaseWithTail.indexOf("\\["));
    }

    public static Set<CommentData> getReleaseCommentsFromReleaseInstruction(IssueData releaseInstruction) {
        return releaseInstruction.getFields()
                .getComment()
                .getComments().stream()
                .filter(commentData -> commentData.getBody().contains(RELEASE_INSTRUCTION_KEY_WORD))
                .collect(Collectors.toSet());
    }

    public static List<AttachmentData> extractAttachmentsData(IssueData releaseTicket) {
        String releaseComment = getLastCreatedReleaseComment(releaseTicket);
        List<String> attachmentsFilenames = extractAttachmentsFilenamesFromReleaseComment(releaseComment);
        return getAttachmentsThatExistInAttachmentsField(releaseTicket, attachmentsFilenames);
    }

    public static List<AttachmentData> getAttachmentsThatExistInAttachmentsField(IssueData releaseTicket,
                                                                                 List<String> releaseCommentAttachmentsFilenames) {
        return releaseTicket.getFields()
                .getAttachment().stream()
                .filter(attachmentData -> releaseCommentAttachmentsFilenames.contains(attachmentData.getFilename()))
                .collect(Collectors.toList());
    }
}
