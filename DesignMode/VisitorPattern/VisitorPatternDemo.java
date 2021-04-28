public class VisitorPatternDemo {
    public static void main(String[] args) {
        ComputerPart computer ;
        computer = new Mouse();
        computer.accept(new ComputerPartDispalyVisitor());

        computer = new Keyboard();
        computer.accept(new ComputerPartDispalyVisitor());
    }
}
