package com.example.statistic_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Value("${tracking.rabbit.exchange:tracking.events}")
    private String trackingExchangeName;

    @Value("${tracking.rabbit.queue.impression:statistics.impression.queue}")
    private String impressionQueueName;

    @Value("${tracking.rabbit.queue.click:statistics.click.queue}")
    private String clickQueueName;

    @Value("${tracking.rabbit.queue.order:statistics.order.queue}")
    private String orderQueueName;

    @Value("${tracking.rabbit.routing.impression:tracking.impression}")
    private String impressionRoutingKey;

    @Value("${tracking.rabbit.routing.click:tracking.click}")
    private String clickRoutingKey;

    @Value("${tracking.rabbit.routing.order:tracking.order}")
    private String orderRoutingKey;

    @Bean
    public TopicExchange trackingExchange() {
        return new TopicExchange(trackingExchangeName, true, false);
    }

    @Bean
    public Queue impressionQueue() {
        return new Queue(impressionQueueName, true);
    }

    @Bean
    public Queue clickQueue() {
        return new Queue(clickQueueName, true);
    }

    @Bean
    public Queue orderQueue() {
        return new Queue(orderQueueName, true);
    }

    @Bean
    public Binding impressionBinding(Queue impressionQueue, TopicExchange trackingExchange) {
        return BindingBuilder.bind(impressionQueue).to(trackingExchange).with(impressionRoutingKey);
    }

    @Bean
    public Binding clickBinding(Queue clickQueue, TopicExchange trackingExchange) {
        return BindingBuilder.bind(clickQueue).to(trackingExchange).with(clickRoutingKey);
    }

    @Bean
    public Binding orderBinding(Queue orderQueue, TopicExchange trackingExchange) {
        return BindingBuilder.bind(orderQueue).to(trackingExchange).with(orderRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }
}
