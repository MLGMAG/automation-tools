package com.mlgmag.utils;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import retrofit2.Response;

import java.util.Objects;

public final class ResponseUtils {

    @SneakyThrows
    public static <T> String processResponse(Response<T> response) {
        if (response.isSuccessful() && Objects.nonNull(response.body())) {
            T responseBody = response.body();
            String messagePattern = "Request is successful. Response body: %s";
            return String.format(messagePattern, responseBody);
        }

        if (response.isSuccessful()) {
            return "Request is successful. Response body is empty";
        }

        if (Objects.nonNull(response.errorBody())) {
            String errorMessagePattern = "Request is failed. HTTP status '%s'. Error body: %s";
            String errorBody = response.errorBody().string();
            return String.format(errorMessagePattern, response.code(), errorBody);
        }

        String errorMessagePattern = "Request is failed. HTTP status '%s'. Error body: is empty";
        return String.format(errorMessagePattern, response.code());
    }

    public static <T> void logResponse(Response<T> response, Logger logger) {
        String responseMessage = processResponse(response);
        if (response.isSuccessful()) {
            logger.debug(responseMessage);
        } else {
            logger.warn(responseMessage);
        }
    }

    private ResponseUtils() {
    }
}
