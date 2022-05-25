package com.molean.isletopia.annotations;

import com.molean.isletopia.shared.annotations.Bean;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Completion {
    @NotNull String value();
}
