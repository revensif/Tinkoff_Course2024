package edu.java.bot.configuration.kafka;

import edu.java.bot.configuration.serializer.GeneralUpdateSerializer;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConfigurationProperties.class)
public class KafkaDLQConfig {

    private final KafkaConfigurationProperties kafkaProperties;

    @Bean
    public ProducerFactory<Integer, byte[]> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerDLQProps());
    }

    @Bean
    public KafkaTemplate<Integer, byte[]> kafkaTemplate(ProducerFactory<Integer, byte[]> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<Integer, byte[]> template) {
        return new DeadLetterPublishingRecoverer(
            template,
            ((consRec, exception) -> new TopicPartition(kafkaProperties.dlq().topic(), consRec.partition()))
        );
    }

    @Bean
    public CommonErrorHandler commonErrorHandler(DeadLetterPublishingRecoverer deadLetterRecoverer) {
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(deadLetterRecoverer);
        errorHandler.addNotRetryableExceptions(ValidationException.class, ConstraintViolationException.class);
        return errorHandler;
    }

    private Map<String, Object> producerDLQProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.bootstrapServers());
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProperties.dlq().lingerMs());
        props.put(ProducerConfig.ACKS_CONFIG, kafkaProperties.dlq().acksMode());
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, (int) kafkaProperties.dlq().deliveryTimeout().toMillis());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProperties.dlq().batchSize());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, GeneralUpdateSerializer.class);
        return props;
    }
}
