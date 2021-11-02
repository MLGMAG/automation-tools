package com.mlgmag.facade;

import com.mlgmag.jira.api.data.jql_search.IssueData;

public interface JiraFacade {
    /**
     * Finds all tickets with input fix version.
     * <p>
     * Filters tickets with release comments.
     * Release comment — comment, that contains "[Release]:" substring.
     * <p>
     * Finds release instruction with input fix version or throw an exception.
     * Release instruction — ticket that have 'Release Instruction' label and input fix version.
     * <p>
     * Gets attachments from release comment. Download them, upload to release instruction and clean temp directory.
     * Generate release messages base on release comments. Add release messages to the release instruction.
     */
    void transferAllInstructionsIntoReleaseInstruction(String fixVersion);

    /**
     * Removes all comments from release instruction and attachments.
     */
    void cleanReleaseInstructionTicket(String fixVersion);

    /**
     * Remove release instruction messages from release instruction comments.
     * Remove release instruction messages attachments from release instruction attachments.
     */
    void cleanInstructionsForReleaseInstructionTicket(IssueData releaseInstruction);


    /**
     * Searches for releaseInstruction with input fix version.
     * If it finds process cleanup {@link JiraFacade#cleanInstructionsForReleaseInstructionTicket(IssueData)}.
     * If it does not find, it creates new fix version, if input fix version does not exist,
     * and then it creates new release ticket.
     */
    void processReleaseTicket(String fixVersion);
}
