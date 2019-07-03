package proxy.javaproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JProxyFactory implements InvocationHandler {
    private final Object human;

    public JProxyFactory(Object human) {
        this.human = human;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Someone is speaking");
        method.invoke(human, args);
        System.out.println("End");
        return null;
    }
}
