package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.Config;
import models.LoginRequestViewModel;
import models.LoginResponseViewModel;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AuthService {

    private final Config config;
    private final HttpClient client;
    private final ObjectMapper objectMapper;

    public AuthService(Config config, ObjectMapper objectMapper, HttpClient httpClient) {
        this.config = config;
        this.client = httpClient;
        this.objectMapper = objectMapper;
    }

    public String token() throws IOException {

        HttpPost request = new HttpPost(this.config.getUrl() + "/api/account/login");
        LoginRequestViewModel loginRequestViewModel = new LoginRequestViewModel();
        loginRequestViewModel.setUsername(config.getUsername());
        loginRequestViewModel.setPassword(config.getPassword());

        String jsonPayload = this.objectMapper.writeValueAsString(loginRequestViewModel);

        StringEntity payload = new StringEntity(jsonPayload, ContentType.create("application/json", "UTF-8"));

        request.setEntity(payload);
        HttpResponse response = client.execute(request);

        LoginResponseViewModel loginResponseViewModel = this.objectMapper.readValue(response.getEntity().getContent(), LoginResponseViewModel.class);

        return loginResponseViewModel.getToken();
    }
}
