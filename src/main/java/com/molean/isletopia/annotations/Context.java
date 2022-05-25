package com.molean.isletopia.annotations;

import co.aikar.commands.annotation.Flags;
import com.molean.isletopia.shared.annotations.Bean;

import javax.inject.Scope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Bean
public @interface Context {
    Class<?> value();
}
