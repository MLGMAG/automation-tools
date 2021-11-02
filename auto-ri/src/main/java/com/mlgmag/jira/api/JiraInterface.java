package com.mlgmag.jira.api;

import com.mlgmag.jira.api.data.fix_version.CreateFixVersionRequest;
import com.mlgmag.jira.api.data.jql_search.CommentRequest;
import com.mlgmag.jira.api.data.jql_search.JqlRequest;
import com.mlgmag.jira.api.data.jql_search.JqlResponse;
import com.mlgmag.jira.api.data.ticket.FixVersion;
import com.mlgmag.jira.api.data.ticket.TicketCreationRequest;
import com.mlgmag.jira.api.data.ticket.TicketCreationResponse;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface JiraInterface {

    @POST("/rest/api/latest/search")
    Call<JqlResponse> search(@Body JqlRequest request);

    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @POST("rest/api/latest/issue/{issueUid}/comment")
    Call<ResponseBody> addComment(@Path("issueUid") String issueUid, @Body CommentRequest commentRequest);

    @DELETE("rest/api/latest/issue/{issueUid}/comment/{commentId}")
    Call<ResponseBody> removeComment(@Path("issueUid") String issueUid, @Path("commentId") String commentId);

    @Headers({"Accept: application/json", "X-Atlassian-Token: no-check"})
    @Multipart
    @POST("rest/api/latest/issue/{issueUid}/attachments")
    Call<ResponseBody> uploadAttachments(@Path("issueUid") String issueUid, @Part MultipartBody.Part filePart);

    @DELETE("rest/api/latest/attachment/{attachmentId}")
    Call<ResponseBody> removeAttachment(@Path("attachmentId") String attachmentId);

    @POST("rest/api/latest/issue")
    Call<TicketCreationResponse> createReleaseTicket(@Body TicketCreationRequest request);

    @POST("rest/api/latest/version")
    Call<ResponseBody> createFixVersion(@Body CreateFixVersionRequest request);

    @GET("rest/api/latest/project/{id}/versions")
    Call<List<FixVersion>> getAllProjectFixVersion(@Path("id") String projectKey);

}
