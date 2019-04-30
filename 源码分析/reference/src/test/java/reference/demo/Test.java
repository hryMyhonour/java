package reference.demo;

import java.util.HashMap;

public class Test {

    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        map.put(null, "1");
        System.out.println(map.get(null));
    }
}
