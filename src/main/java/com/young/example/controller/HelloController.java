package com.young.example.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @RequestMapping(value = "/hello", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> hello(HttpServletRequest request) {
        logger.debug("contextPath: {}", request.getContextPath());
        logger.debug("requestURI: {}", request.getRequestURI());

        final Map<String, Object> map = new HashMap<>(1);
        map.put("name", "delegacy");
        return map;
    }
}
