package org.acme.backend.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);
    
    @Autowired
    RestTemplate restTemplate;
    
    private final String endpointUrl;

    public GitService(RestGitProperties properties) {
        if (properties.getEndpoint().trim().length() == 0) {
            throw new RuntimeException("Endpoint url can not be Empty.");
        }
        this.endpointUrl = properties.getEndpoint();
    }

    public List<String> findAll() throws Exception {
        LOGGER.info("findAll called.");
        ResponseEntity<List<String>> response = restTemplate.exchange(endpointUrl + "/api/git", HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>(){});
        return response.getBody();
    }

    public void store(MultipartFile[] files) throws Exception {
        LOGGER.info("store called.");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Arrays.asList(files).stream().forEach(file -> {
            try{
                body.add("mame", file.getOriginalFilename());
                body.add("filename", file.getOriginalFilename());
                ByteArrayResource contentsAsResource = new ByteArrayResource(file.getBytes()){
                    @Override
                    public String getFilename() {
                        return file.getOriginalFilename();
                    }
                };
                body.add("files", contentsAsResource);
            }catch(Exception e) {
                throw new RuntimeException(e);
            }
        });
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(endpointUrl + "/api/git", request, String.class);
    }

    public void commit(String message) throws Exception {
        LOGGER.info("commit called.");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("commit", message);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(endpointUrl + "/api/git/commit", request, String.class);
    }
}
