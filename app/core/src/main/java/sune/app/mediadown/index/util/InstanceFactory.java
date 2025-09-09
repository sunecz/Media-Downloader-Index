package sune.app.mediadown.index.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class InstanceFactory {

    private final ApplicationContext context;

    public InstanceFactory(ApplicationContext context) {
        this.context = context;
    }
    
    public <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            if (constructors.length == 0) {
                throw new IllegalArgumentException("Class has no constructors: " + clazz.getName());
            }

            Constructor<?> constructor = constructors[0];
            constructor.setAccessible(true);

            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                try {
                    args[i] = context.getBean(paramTypes[i]);
                } catch(BeansException ex) {
                    throw new IllegalStateException(
                        "No bean found for constructor parameter type: " + paramTypes[i].getName(), ex
                    );
                }
            }

            @SuppressWarnings("unchecked")
            T instance = (T) constructor.newInstance(args);
            return instance;
        } catch(InvocationTargetException
        			| InstantiationException
        			| IllegalAccessException
        			| IllegalArgumentException ex) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), ex);
        }
    }
}
