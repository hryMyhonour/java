package proxy;

public class Teacher implements Human {
    public Teacher() {
        System.out.println("init Teacher");
    }

    @Override
    public void say(String content) {
        System.out.println("I am a teacher");
        System.out.println("I say " + content);
    }

    @Override
    public final void walk(){
        System.out.println("I am working");
    }
}
