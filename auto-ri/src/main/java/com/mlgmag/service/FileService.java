package com.mlgmag.service;

import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class FileService {

    private static final Logger LOG = LoggerFactory.getLogger(FileService.class);

    @Value("${file.tempDirectory}")
    private String tempDirectory;

    @SneakyThrows
    public void cleanUpTempDirectory() {
        LOG.info("Cleanup temp directory...");
        FileUtils.cleanDirectory(new File(tempDirectory));
    }

    @SneakyThrows
    public void writeResponseInputIntoTempDirectory(Response<ResponseBody> response, String targetFileName) {
        if (response.body() == null) {
            String errorMessagePatter = "Response body for file '%s' is empty!";
            String errorMessage = String.format(errorMessagePatter, targetFileName);
            LOG.warn(errorMessage);
            return;
        }

        InputStream inputStream = response.body().byteStream();
        Path path = Paths.get(tempDirectory, targetFileName);
        File targetFile = new File(path.toString());
        FileUtils.copyInputStreamToFile(inputStream, targetFile);
    }

    public Optional<File> getFileFromTempDir(String fileName) {
        Path path = Paths.get(tempDirectory, fileName);
        File file = path.toFile();

        if (!file.exists()) {
            String errorMessagePatter = "File '%s' does not exists in temp directory!";
            String errorMessage = String.format(errorMessagePatter, fileName);
            LOG.warn(errorMessage);
            return Optional.empty();
        }

        return Optional.of(file);
    }

}
