package com.example.recommend_service.config;

import org.example.common.model.ClickEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitConfig {

    public static final String TRACKING_EXCHANGE   = "tracking.events";
    public static final String CLICK_QUEUE         = "recommend.click.queue";
    public static final String CLICK_ROUTING_KEY   = "tracking.click";

    @Bean
    public TopicExchange trackingExchange() {
        return new TopicExchange(TRACKING_EXCHANGE, true, false);
    }

    @Bean
    public Queue clickQueue() {
        return QueueBuilder.durable(CLICK_QUEUE).build();
    }

    @Bean
    public Binding clickBinding(Queue clickQueue, TopicExchange trackingExchange) {
        return BindingBuilder.bind(clickQueue).to(trackingExchange).with(CLICK_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        // Map class header từ tracking_service sang common model
        typeMapper.setIdClassMapping(Map.of(
                "org.example.common.model.ClickEvent", ClickEvent.class
        ));
        typeMapper.setTrustedPackages("org.example.common.model", "com.example");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        return factory;
    }
}
