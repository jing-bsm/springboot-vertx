package com.github.jing_bsm.verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class VertxConfig {
    @Getter(lazy = true, value = AccessLevel.PACKAGE)
    private final Vertx vertx = Vertx.vertx(new VertxOptions());

    @Getter(lazy = true)
    private static final VertxConfig vertxConfig = new VertxConfig();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public <T> Consumer<T> getConsumer(VertxProcessAdapter<T> adapter) {
        return new Consumer<>() {
            private static final Set<String> ID_SET = new ConcurrentSkipListSet<>();

            @Override
            public void accept(T t) {
                String address = String.format("/%s/", adapter.getId(t));

                if (!ID_SET.contains(address)) {
                    ID_SET.add(address);
                    var verticle = new ConsumerVerticle<T>(getVertx().eventBus(), address, adapter);
                    getVertx().deployVerticle(verticle);
                }
                getVertx().eventBus().send(address, JsonObject.mapFrom(t));
            }
        };
    }

    @Log4j2
    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    static class ConsumerVerticle<T> extends AbstractVerticle {
        private final EventBus eventBus;
        private final String address;

        private final VertxProcessAdapter<T> adapter;

        @Override
        public void start() {
            eventBus.consumer(address).handler(message -> {
                try {
                    var t = OBJECT_MAPPER.readValue(message.body().toString(), adapter.type());
                    adapter.accept(t);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
