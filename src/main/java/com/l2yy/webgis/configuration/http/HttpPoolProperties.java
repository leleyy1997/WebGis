package com.l2yy.webgis.configuration.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "http-pool")
@Data
public class HttpPoolProperties {

    public static final int DEFAULT_MAX_TOTAL = 200;

    public static final int DEFAULT_MAX_PER_ROUTE = 100;

    public static final int DEFAULT_CONNECT_TIMEOUT = 1000;

    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 1000;

    public static final int DEFAULT_SOCKET_TIMEOUT = 3000;

    private int maxTotal = DEFAULT_MAX_TOTAL;

    private int defaultMaxPerRoute = DEFAULT_MAX_PER_ROUTE;

    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    private int connectionRequestTimeout = DEFAULT_CONNECTION_REQUEST_TIMEOUT;

    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;
}
