public class MediatorPatterDemo {
    public static void main(String[] args) {
        User robert = new User("Robert");
        User john = new User("Jhon");

        robert.sendMessage("Hello, John?");
        john.sendMessage("hi, Robert, I am jonh,");
    }
}
