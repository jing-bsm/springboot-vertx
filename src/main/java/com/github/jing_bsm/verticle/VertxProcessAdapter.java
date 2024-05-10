package com.github.jing_bsm.verticle;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.function.Consumer;

public interface VertxProcessAdapter<T> extends Consumer<T> {
    String getId(T t);

    TypeReference<T> type();
}
