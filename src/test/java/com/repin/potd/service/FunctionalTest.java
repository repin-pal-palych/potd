package com.repin.potd.service;

import com.repin.potd.Application;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {Application.class})
@ActiveProfiles("test")
public class FunctionalTest {
}
