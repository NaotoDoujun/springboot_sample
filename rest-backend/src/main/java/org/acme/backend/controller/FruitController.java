package org.acme.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.acme.backend.model.Fruit;
import org.acme.backend.service.FruitService;

@RestController
@RequestMapping("/api")
public class FruitController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FruitController.class);
    
    @Autowired
    FruitService fruitService;

    @GetMapping("/fruits")
    public ResponseEntity<List<Fruit>> getAllFruits() {
        try {
            List<Fruit> fruits = new ArrayList<Fruit>();
            fruitService.findAll().forEach(fruits::add);
            if (fruits.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return new ResponseEntity<>(fruits, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.toString());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/fruits/{id}")
    public ResponseEntity<Fruit> getFruitById(@PathVariable("id") long id) {
        try{
            Optional<Fruit> fruitData = fruitService.findById(id);
            if (fruitData.isPresent()) {
                return new ResponseEntity<>(fruitData.get(), HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        }catch(Exception e) {
            LOGGER.error(e.toString());
            return ResponseEntity.internalServerError().build();
        }
    }

}
