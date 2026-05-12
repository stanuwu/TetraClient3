package com.stanuwu.tetraclient3.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EventSubscriber {
    Class<? extends BaseEvent<?>> event();

    EventOrder order() default EventOrder.NORMAL;
}
