package configserver.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.StandardClassMetadata;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;

@SpringBootApplication
public class SpringTest {

    public static class TList extends LinkedList<String>{
        @Override
        public String get(int i) {
            return "";
        }
    }

    @Autowired
    private void t(){}

    public static void main(String[] args) throws Exception {
        StandardAnnotationMetadata metadata = new StandardAnnotationMetadata(SpringTest.class);
        System.out.println(metadata.hasMetaAnnotation("org.springframework.context.annotation.ComponentScan"));
       // Method m = SpringTest.class.getDeclaredMethod("t");
       // AnnotatedElementUtils.isAnnotated(m, "Autowired");
    }
}
