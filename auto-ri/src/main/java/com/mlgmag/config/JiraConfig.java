package com.mlgmag.config;

import com.mlgmag.jira.api.JiraInterface;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class JiraConfig {

    @Value("${jira.username}")
    private String username;

    @Value("${jira.password}")
    private String password;

    @Value("${jira.baseUrl}")
    private String baseUrl;

    @Bean
    public Retrofit retrofit() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(httpClient())
                .build();
    }

    @Bean
    public JiraInterface jiraInterface(Retrofit retrofit) {
        return retrofit.create(JiraInterface.class);
    }

    private OkHttpClient httpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(this::appendAuth)
                .readTimeout(15, SECONDS)
                .connectTimeout(15, SECONDS)
                .build();
    }

    private Response appendAuth(Interceptor.Chain chain) throws IOException {
        Request original = chain.request();
        Request request = buildAuthRequest(original);
        return chain.proceed(request);
    }

    private Request buildAuthRequest(Request original) {
        return original.newBuilder()
                .header("Authorization", Credentials.basic(username, password))
                .build();
    }
}
