package com.repin.potd;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.springframework.boot.SpringApplication.run;

/**
 * Главный класс для запуска приложения.
 */
@SpringBootApplication(scanBasePackages = {"com.repin"})
public class Application {

    public static void main(String[] args) {
        run(Application.class, args);
    }
}
