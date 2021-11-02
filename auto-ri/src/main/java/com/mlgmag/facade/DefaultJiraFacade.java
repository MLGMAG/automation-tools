package com.mlgmag.facade;

import com.mlgmag.jira.api.data.jql_search.AttachmentData;
import com.mlgmag.jira.api.data.jql_search.CommentData;
import com.mlgmag.jira.api.data.jql_search.IssueData;
import com.mlgmag.service.*;
import com.mlgmag.utils.JiraUtils;
import com.mlgmag.utils.OptionalConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mlgmag.utils.JiraUtils.getIssuesWithReleaseComment;
import static com.mlgmag.utils.JiraUtils.getReleaseCommentsFromReleaseInstruction;

@Component
public class DefaultJiraFacade implements JiraFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultJiraFacade.class);

    @Resource
    private SearchService searchService;
    @Resource
    private FileService fileService;
    @Resource
    private ReleaseMessageService releaseMessageService;
    @Resource
    private TicketService ticketService;
    @Resource
    private FixVersionService fixVersionService;
    @Resource
    private CommentService commentService;
    @Resource
    private AttachmentService attachmentService;

    @Override
    public void transferAllInstructionsIntoReleaseInstruction(String fixVersion) {
        List<IssueData> issuesWithReleaseFixVersion = searchService.searchIssuesByFixVersion(fixVersion);
        List<IssueData> issuesWithReleaseComment = getIssuesWithReleaseComment(issuesWithReleaseFixVersion, fixVersion);
        logTicketsWithReleaseComment(issuesWithReleaseComment);
        IssueData releaseInstructionTicket = searchService.searchReleaseInstructionOrThrowException(fixVersion);
        String releaseInstructionTicketKey = releaseInstructionTicket.getKey();
        addAttachmentsToReleaseInstruction(issuesWithReleaseComment, releaseInstructionTicketKey);
        addMessagesToReleaseInstruction(issuesWithReleaseComment, releaseInstructionTicketKey);
        LOG.info("Instruction transfer is completed");
    }

    private void logTicketsWithReleaseComment(List<IssueData> issuesWithReleaseComment) {
        String issuesEnumeration = issuesWithReleaseComment.stream().map(IssueData::getKey).collect(Collectors.joining(", "));
        String messagePattern = "There are '%s' issues with release comment. Issue keys: %s";
        String message = String.format(messagePattern, issuesWithReleaseComment.size(), issuesEnumeration);
        LOG.info(message);
    }

    private void addAttachmentsToReleaseInstruction(List<IssueData> issuesWithReleaseComment, String releaseInstructionTicketKey) {
        Set<AttachmentData> releaseCommentAttachments = issuesWithReleaseComment.stream()
                .map(JiraUtils::extractAttachmentsData)
                .flatMap(List::stream).collect(Collectors.toSet());

        String attachmentsDownloadMessage = String.format("Downloading '%s' attachments...", releaseCommentAttachments.size());
        LOG.info(attachmentsDownloadMessage);

        Set<AttachmentData> downloadedAttachments = releaseCommentAttachments.stream()
                .filter(attachmentData -> attachmentService.downloadAttachment(attachmentData))
                .collect(Collectors.toSet());

        String downloadedAttachmentsMessage = String.format("'%s' attachments are downloaded successfully. Uploading them to the release instruction...", downloadedAttachments.size());
        LOG.info(downloadedAttachmentsMessage);

        downloadedAttachments.forEach(attachmentData -> attachmentService.uploadAttachment(attachmentData, releaseInstructionTicketKey));

        fileService.cleanUpTempDirectory();
    }

    private void addMessagesToReleaseInstruction(List<IssueData> issuesWithReleaseComment, String releaseInstructionTicketKey) {
        issuesWithReleaseComment.stream()
                .map(releaseMessageService::generateReleaseMessage)
                .forEach(releaseMessage -> commentService.addComment(releaseMessage, releaseInstructionTicketKey));
    }

    @Override
    public void cleanReleaseInstructionTicket(String fixVersion) {
        IssueData releaseInstruction = searchService.searchReleaseInstructionOrThrowException(fixVersion);
        cleanCommentsFromTicket(releaseInstruction);
        cleanAttachmentsFromTicket(releaseInstruction);
        LOG.info("Release Instruction cleanup is completed");
    }

    private void cleanCommentsFromTicket(IssueData ticket) {
        ticket.getFields()
                .getComment()
                .getComments().stream()
                .map(CommentData::getId)
                .forEach(commentId -> commentService.removeComment(commentId, ticket.getKey()));
    }

    private void cleanAttachmentsFromTicket(IssueData ticket) {
        ticket.getFields()
                .getAttachment().stream()
                .map(AttachmentData::getId)
                .forEach(attachmentId -> attachmentService.removeAttachment(attachmentId));
    }

    @Override
    public void processReleaseTicket(String fixVersion) {
        OptionalConsumer.of(searchService.searchReleaseInstruction(fixVersion))
                .ifPresent(this::cleanOldTicketFlow)
                .ifNotPresent(() -> createNewRelease(fixVersion));
    }

    private void createNewRelease(String fixVersion) {
        createFixVersionIfNotExists(fixVersion);
        String ticketKey = ticketService.createReleaseTicket(fixVersion);
        String messagePattern = "Ticket successfully created and can be found by key '%s'";
        String message = String.format(messagePattern, ticketKey);
        LOG.info(message);
    }

    private void createFixVersionIfNotExists(String fixVersion) {
        if (!fixVersionService.isFixVersionExists(fixVersion)) {
            String messagePattern = "Fix version '%s' does not exist. Process fix version creation...";
            String message = String.format(messagePattern, fixVersion);
            LOG.info(message);
            fixVersionService.createFixVersion(fixVersion);
        }
    }

    private void cleanOldTicketFlow(IssueData releaseInstruction) {
        String messagePatter = "Release instruction is already created and has key '%s'. Starting cleanup...";
        String message = String.format(messagePatter, releaseInstruction.getKey());
        LOG.info(message);
        cleanInstructionsForReleaseInstructionTicket(releaseInstruction);
    }

    @Override
    public void cleanInstructionsForReleaseInstructionTicket(IssueData releaseInstruction) {
        Set<CommentData> commentsWithInstructions = getReleaseCommentsFromReleaseInstruction(releaseInstruction);

        Set<String> commentsIds = commentsWithInstructions.stream()
                .map(CommentData::getId)
                .collect(Collectors.toSet());

        String commentsCleanMessage = String.format("There are '%s' old release comments. Removing them...", commentsIds.size());
        LOG.info(commentsCleanMessage);

        commentsIds.forEach(commentId -> commentService.removeComment(commentId, releaseInstruction.getKey()));

        Set<String> commentsAttachmentsFilenames = commentsWithInstructions.stream()
                .map(CommentData::getBody)
                .map(JiraUtils::extractAttachmentsFilenamesFromReleaseComment)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        Set<String> attachmentsIds = releaseInstruction.getFields()
                .getAttachment().stream()
                .filter(attachmentData -> commentsAttachmentsFilenames.contains(attachmentData.getFilename()))
                .map(AttachmentData::getId).collect(Collectors.toSet());

        String attachmentsCleanMessage = String.format("There are '%s' old attachments. Removing them...", attachmentsIds.size());
        LOG.info(attachmentsCleanMessage);
        attachmentsIds.forEach(attachmentService::removeAttachment);

        String cleanUpEndMessage = String.format("Release Instruction '%s' cleanup is completed", releaseInstruction.getKey());
        LOG.info(cleanUpEndMessage);
    }
}
