package demo;

public class User implements CacheObject {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Object key() {
        return id;
    }

    @Override
    public Object value() {
        return name;
    }
}
