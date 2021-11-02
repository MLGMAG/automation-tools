package com.mlgmag.service;

import com.mlgmag.jira.api.JiraInterface;
import com.mlgmag.jira.api.data.jql_search.CommentRequest;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import javax.annotation.Resource;

import static com.mlgmag.utils.ResponseUtils.logResponse;

@Service
public class CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentService.class);

    @Resource
    private JiraInterface jiraInterface;

    @SneakyThrows
    public void addComment(String comment, String issueUid) {
        CommentRequest commentRequest = new CommentRequest(comment);
        Response<ResponseBody> response = jiraInterface.addComment(issueUid, commentRequest).execute();
        logResponse(response, LOG);
    }

    @SneakyThrows
    public void removeComment(String commentId, String issueUid) {
        Response<ResponseBody> response = jiraInterface.removeComment(issueUid, commentId).execute();
        logResponse(response, LOG);
    }
}
