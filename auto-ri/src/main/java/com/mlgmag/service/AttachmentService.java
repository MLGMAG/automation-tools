package com.mlgmag.service;

import com.mlgmag.jira.api.JiraInterface;
import com.mlgmag.jira.api.data.jql_search.AttachmentData;
import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import javax.annotation.Resource;
import java.io.File;
import java.util.Objects;

import static com.mlgmag.utils.ResponseUtils.logResponse;

@Service
public class AttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentService.class);

    private static final String FILE_CONTENT_TYPE = "multipart/form-data";

    @Resource
    private JiraInterface jiraInterface;
    @Resource
    private FileService fileService;

    @SneakyThrows
    public boolean downloadAttachment(AttachmentData attachmentData) {
        String fileUrl = attachmentData.getContent();
        Response<ResponseBody> response = jiraInterface.downloadFile(fileUrl).execute();
        logResponse(response, LOG);

        if (Objects.nonNull(response.body())) {
            String targetFileName = attachmentData.getFilename();
            fileService.writeResponseInputIntoTempDirectory(response, targetFileName);
            return true;
        } else {
            return false;
        }
    }

    public void uploadAttachment(AttachmentData attachmentData, String issueUid) {
        String fileName = attachmentData.getFilename();
        fileService.getFileFromTempDir(fileName).ifPresent(file -> processAttachmentUpload(file, issueUid));
    }

    @SneakyThrows
    private void processAttachmentUpload(File file, String issueUid) {
        RequestBody requestFile = RequestBody.create(MediaType.parse(FILE_CONTENT_TYPE), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Response<ResponseBody> response = jiraInterface.uploadAttachments(issueUid, body).execute();
        logResponse(response, LOG);
    }

    @SneakyThrows
    public void removeAttachment(String attachmentId) {
        Response<ResponseBody> response = jiraInterface.removeAttachment(attachmentId).execute();
        logResponse(response, LOG);
    }
}
