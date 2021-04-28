public class ComputerPartDispalyVisitor implements ComputerPartVisitor {
    @Override
    public void visit(Keyboard keyboard) {
        System.out.println("Displaying keyboard.");        
    }
    
    @Override
    public void visit(Mouse mouse) {
        System.out.println("Displaying Mouse.");
    }
}
