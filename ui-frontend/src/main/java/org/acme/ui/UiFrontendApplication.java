package org.acme.ui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class UiFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(UiFrontendApplication.class, args);
	}

}
