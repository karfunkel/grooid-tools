package groovy.android.factory;

import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.lang.reflect.Method;

public class DummyClosure extends Closure {
    private Method method;

    public DummyClosure(Method method, Object thisObject) {
        super(thisObject, thisObject);
        this.method = method;
        parameterTypes = method.getParameterTypes();
        maximumNumberOfParameters = method.getParameterTypes().length;
    }

    @Override
    public Object call(Object... args) {
        if (method.getReturnType().isPrimitive())
            if (method.getReturnType().equals(boolean.class))
                return false;
            else if (method.getReturnType().equals(Void.class) || method.getReturnType().equals(void.class))
                return null;
            else
                return DefaultGroovyMethods.asType(0, method.getReturnType());
        else
            return null;
    }
}
