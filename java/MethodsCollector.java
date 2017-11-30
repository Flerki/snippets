import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodsCollector {

    /*
    *
    * Collects all abstract methods
    * Methods are uniquely identified 
    * by their name and types of parameters
    *
    * */
    public Set<Method> collect(final Class<?> c) {
        return getMethodsToImplementStream(c)
                .collect(Collectors.toSet());
    }

    private Stream<Method> getMethodsToImplementStream(final Class<?> c) {
        Class<?> cur = c;
        Stream<Method> methodsStream = Arrays.stream(cur.getMethods());
        while (cur != null) {
            final Stream<Method> declaredMethodsStream = Arrays.stream(cur.getDeclaredMethods());
            methodsStream = Stream.concat(methodsStream, declaredMethodsStream);
            cur = cur.getSuperclass();
        }

        return methodsStream.filter(m -> Modifier.isAbstract(m.getModifiers()))
                .map(MethodWrapper::new)
                .collect(Collectors.toSet())
                .stream()
                .map(MethodWrapper::unwrap);
    }

    private static class MethodWrapper {
        private final Method m;
        private final String name;
        private final Class<?>[] pars;

        MethodWrapper(final Method m) {
            this.m = m;
            pars = m.getParameterTypes();
            name = m.getName();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final MethodWrapper that = (MethodWrapper) o;

            if (!name.equals(that.name)) {
                return false;
            } else {
                return Arrays.equals(pars, that.pars);
            }
        }

        @Override
        public int hashCode() {
            final int multiplier = 31;
            return multiplier * name.hashCode() + Arrays.hashCode(pars);
        }

        Method unwrap() {
            return m;
        }
    }
}
