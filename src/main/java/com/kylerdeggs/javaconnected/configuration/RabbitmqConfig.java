package com.kylerdeggs.javaconnected.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for RabbitMQ that declares the exchange/queues and binds it together.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@Configuration
public class RabbitmqConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitmqConfig.class);

    @Value("${amqp.exchange.name}")
    private String exchangeName;

    @Value("${amqp.queue.post-name}")
    private String postQueueName;

    @Value("${amqp.queue.post-delete-name}")
    private String postDeletionQueueName;

    @Value("${amqp.queue.comment-name}")
    private String commentQueueName;

    @Value(value = "${amqp.queue.comment-delete-name}")
    private String commentDeletionQueueName;

    @Value("${amqp.queue.like-name}")
    private String likeQueueName;

    @Bean
    public Queue postQueue() {
        LOGGER.info("Creating queue " + postQueueName);
        return new Queue(postQueueName, false);
    }

    @Bean
    public Queue postDeletionQueue() {
        LOGGER.info("Creating queue " + postDeletionQueueName);
        return new Queue(postDeletionQueueName, false);
    }

    @Bean
    public Queue commentQueue() {
        LOGGER.info("Creating queue " + commentQueueName);
        return new Queue(commentQueueName, false);
    }

    @Bean
    public Queue commentDeletionQueue() {
        LOGGER.info("Creating queue " + commentDeletionQueueName);
        return new Queue(commentDeletionQueueName, false);
    }

    @Bean
    public Queue likeQueue() {
        LOGGER.info("Creating queue " + likeQueueName);
        return new Queue(likeQueueName, false);
    }

    @Bean
    public DirectExchange directExchange() {
        LOGGER.info("Creating direct exchange " + exchangeName);
        return new DirectExchange(exchangeName);
    }

    @Bean
    public Binding postBinding(DirectExchange directExchange) {
        LOGGER.info("Binding exchange " + exchangeName + " to queue " + postQueueName);
        return BindingBuilder.bind(postQueue()).to(directExchange).with(postQueueName);
    }

    @Bean
    public Binding postDeletionBinding(DirectExchange directExchange) {
        LOGGER.info("Binding exchange " + exchangeName + " to queue " + postDeletionQueueName);
        return BindingBuilder.bind(postDeletionQueue()).to(directExchange).with(postDeletionQueueName);
    }


    @Bean
    public Binding commentBinding(DirectExchange directExchange) {
        LOGGER.info("Binding exchange " + exchangeName + " to queue " + commentQueueName);
        return BindingBuilder.bind(commentQueue()).to(directExchange).with(commentQueueName);
    }

    @Bean
    public Binding commentDeletionBinding(DirectExchange directExchange) {
        LOGGER.info("Binding exchange " + exchangeName + " to queue " + commentDeletionQueueName);
        return BindingBuilder.bind(commentDeletionQueue()).to(directExchange).with(commentDeletionQueueName);
    }

    @Bean
    public Binding likeBinding(DirectExchange directExchange) {
        LOGGER.info("Binding exchange " + exchangeName + " to queue " + likeQueueName);
        return BindingBuilder.bind(likeQueue()).to(directExchange).with(likeQueueName);
    }
}
