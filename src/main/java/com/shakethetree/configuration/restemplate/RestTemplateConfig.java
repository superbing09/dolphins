package com.shakethetree.configuration.restemplate;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.charset.Charset;

/**
 * @author xubing
 * @description //TODO 设计说明
 * @date 19-8-20
 * @copyright 中网易企秀
 */
@Configuration
public class RestTemplateConfig {


    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        StringHttpMessageConverter m = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        RestTemplate restTemplate = new RestTemplateBuilder().additionalMessageConverters(m).build();
        restTemplate.setRequestFactory(factory);
        return new RestTemplate(factory);
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        SocketAddress address = new InetSocketAddress("192.168.0.10", 3128);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, address);
        factory.setProxy(proxy);
        factory.setConnectTimeout(15000);
        factory.setReadTimeout(5000);
        return factory;
    }
}
