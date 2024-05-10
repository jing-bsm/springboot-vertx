package com.github.jing_bsm.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.jing_bsm.domain.Event;
import com.github.jing_bsm.verticle.VertxConfig;
import com.github.jing_bsm.verticle.VertxProcessAdapter;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Log4j2
@Configuration
public class Tester {

    VertxConfig config = VertxConfig.getVertxConfig();

    private Random random = new Random();

    @SneakyThrows
    @PostConstruct
    public void after() {
        var consumer = config.getConsumer(new VertxProcessAdapter<Event>() {

            @Override
            public void accept(Event event) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                log.info("Processed {}, {}", getId(event), event);
            }

            @Override
            public String getId(Event event) {
                return event.getUserId();
            }

            @Override
            public TypeReference<Event> type() {
                return new TypeReference<>() {
                };
            }
        });


        new Thread(() -> {

            try {
                Thread.sleep(1000L);

                for (int i = 0; i < 200; i++) {
                    var e = new Event(String.valueOf(random.nextInt(5)), // user
                            String.valueOf(random.nextInt(20)), // instance
                            String.valueOf(2), // customer
                            String.valueOf(random.nextInt(20)));

                    consumer.accept(e);
                }
//                    Thread.sleep(5000L);
//                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }).start();
    }

    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            var e = new Event(String.valueOf(random.nextInt(5)),
                    String.valueOf(random.nextInt(20)),
                    String.valueOf(random.nextInt(20)),
                    String.valueOf(random.nextInt(20)));

            new Thread(() ->{
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                log.info("Processed {}, {}", e.getCustomerId(),e);
            }).start();
        }
    }
}
