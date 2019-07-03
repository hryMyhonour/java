package proxy.cblibproxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CglibProxy implements MethodInterceptor {
    private Object human;

    public CglibProxy(Object human) {
        this.human = human;
    }

    public Object build(){
        Enhancer en = new Enhancer();
        en.setSuperclass(human.getClass());
        en.setCallback(this);
        return en.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("Someone is speaking");
        method.invoke(human, args);
        System.out.println("End");
        return null;
    }
}
