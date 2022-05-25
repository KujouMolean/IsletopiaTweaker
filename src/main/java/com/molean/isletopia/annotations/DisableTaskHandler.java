package com.molean.isletopia.annotations;

import com.molean.isletopia.shared.ClassResolver;
import com.molean.isletopia.shared.annotations.BeanHandler;
import com.molean.isletopia.shared.annotations.BeanHandlerPriority;
import com.molean.isletopia.shared.annotations.DisableTask;
import com.molean.isletopia.task.Tasks;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@BeanHandlerPriority(0)
public class DisableTaskHandler implements BeanHandler {
    @Override
    public void handle(Object object) {
        outer:
        for (Method declaredMethod : object.getClass().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(DisableTask.class)) {
                List<Object> parameters = new ArrayList<>();
                for (Parameter parameter : declaredMethod.getParameters()) {
                    Object parameterObject = ClassResolver.INSTANCE.getObject(parameter);
                    if (parameterObject == null) {
                        continue outer;
                    }
                    parameters.add(parameterObject);
                }
                try {
                    Tasks.INSTANCE.addDisableTask(object.getClass() + "'s disable task", () -> {
                        try {
                            declaredMethod.invoke(object, parameters.toArray());
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
