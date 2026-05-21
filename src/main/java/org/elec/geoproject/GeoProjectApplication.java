package org.elec.geoproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class GeoProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(GeoProjectApplication.class, args);
  }
}
