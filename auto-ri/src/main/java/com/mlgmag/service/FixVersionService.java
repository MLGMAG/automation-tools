package com.mlgmag.service;

import com.mlgmag.jira.api.JiraInterface;
import com.mlgmag.jira.api.data.fix_version.CreateFixVersionRequest;
import com.mlgmag.jira.api.data.ticket.FixVersion;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.mlgmag.utils.ResponseUtils.logResponse;

@Service
public class FixVersionService {

    private static final Logger LOG = LoggerFactory.getLogger(FixVersionService.class);

    @Value("${jira.releaseInstructionTemplateData.projectKey}")
    private String projectKey;

    @Resource
    private JiraInterface jiraInterface;

    @SneakyThrows
    public boolean isFixVersionExists(String fixVersion) {
        Call<List<FixVersion>> allProjectFixVersion = jiraInterface.getAllProjectFixVersion(projectKey);
        Response<List<FixVersion>> response = allProjectFixVersion.execute();
        logResponse(response, LOG);

        if (!response.isSuccessful() || Objects.isNull(response.body())) {
            throw new IllegalAccessException("Can not access fix versions");
        }

        return response.body().stream()
                .map(FixVersion::getName)
                .anyMatch(version -> version.equals(fixVersion));
    }

    @SneakyThrows
    public void createFixVersion(String fixVersion) {
        CreateFixVersionRequest request = CreateFixVersionRequest.builder()
                .name(fixVersion)
                .project(projectKey).build();

        Response<ResponseBody> response = jiraInterface.createFixVersion(request).execute();
        logResponse(response, LOG);
    }
}
