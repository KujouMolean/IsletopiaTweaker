package com.molean.isletopia.annotations;

import com.molean.isletopia.shared.annotations.Bean;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface BukkitCommand {
    @NotNull String value();
}
