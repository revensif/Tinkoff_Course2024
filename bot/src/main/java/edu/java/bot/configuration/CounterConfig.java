package edu.java.bot.configuration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CounterConfig {

    private static final String COMMANDS_PROCESSED_NUMBER = "commands_processed_number";
    private static final String COMMAND_TYPE = "command_type";

    @Bean
    public Counter messageCounter(MeterRegistry registry) {
        return registry.counter("messages_processed_number");
    }

    @Bean(name = "/help")
    public Counter helpCounter(MeterRegistry registry) {
        return registry.counter(COMMANDS_PROCESSED_NUMBER, COMMAND_TYPE, "/help");
    }

    @Bean(name = "/list")
    public Counter listCounter(MeterRegistry registry) {
        return registry.counter(COMMANDS_PROCESSED_NUMBER, COMMAND_TYPE, "/list");
    }

    @Bean(name = "/start")
    public Counter startCounter(MeterRegistry registry) {
        return registry.counter(COMMANDS_PROCESSED_NUMBER, COMMAND_TYPE, "/start");
    }

    @Bean(name = "/track")
    public Counter trackCounter(MeterRegistry registry) {
        return registry.counter(COMMANDS_PROCESSED_NUMBER, COMMAND_TYPE, "/track");
    }

    @Bean(name = "/untrack")
    public Counter untrackCounter(MeterRegistry registry) {
        return registry.counter(COMMANDS_PROCESSED_NUMBER, COMMAND_TYPE, "/untrack");
    }
}
