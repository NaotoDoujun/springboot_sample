package org.acme.backend.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.acme.backend.model.Fruit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FruitService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FruitService.class);

    @Autowired
    RestTemplate restTemplate;

    private final String endpointUrl;

    public FruitService(RestDbProperties properties) {
        if (properties.getEndpoint().trim().length() == 0) {
            throw new RuntimeException("Endpoint url can not be Empty.");
        }
        this.endpointUrl = properties.getEndpoint();
    }

    public List<Fruit> findAll() throws Exception {
        LOGGER.info("findAll called.");
        ResponseEntity<List<Fruit>> responseEntity = restTemplate.exchange(endpointUrl + "/api/fruits", HttpMethod.GET, null, new ParameterizedTypeReference<List<Fruit>>(){});
        return responseEntity.getBody();
    }

    public Optional<Fruit> findById(long id) throws Exception {
        LOGGER.info("findById called.");
        ResponseEntity<Fruit> responseEntity = restTemplate.exchange(endpointUrl + "/api/fruits/{id}", HttpMethod.GET, null, Fruit.class, id);
        return Optional.ofNullable(responseEntity.getBody());
    }
}