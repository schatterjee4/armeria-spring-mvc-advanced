package com.young.example;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.server.ServerBuilder;

public class AppTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        App.startServer(new ServerBuilder().port(0, SessionProtocol.HTTP));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        App.stopServer();
    }

    @Test
    public void testOk() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpGet req = new HttpGet("http://localhost:" + App.httpPort() + '/');

            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(res.getStatusLine().toString(), is("HTTP/1.1 200 OK"));
            }
        }
    }

    @Test
    public void testNotFound() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpGet req = new HttpGet("http://localhost:" + App.httpPort() + "/notFound");

            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(res.getStatusLine().toString(), is("HTTP/1.1 404 Not Found"));
            }
        }
    }

    @Test
    public void testApiOk() throws Exception {
        try (CloseableHttpClient hc = HttpClients.createMinimal()) {
            final HttpGet req = new HttpGet("http://localhost:" + App.httpPort() + "/api/hello");

            try (CloseableHttpResponse res = hc.execute(req)) {
                assertThat(res.getStatusLine().toString(), is("HTTP/1.1 200 OK"));
            }
        }
    }
}
