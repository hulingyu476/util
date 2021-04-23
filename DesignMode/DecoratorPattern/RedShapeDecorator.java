public class RedShapeDecorator extends ShapeDecorator {

    public RedShapeDecorator(Shape decoratedShape) {
        super(decoratedShape);
    }

    @Override
    public void draw() {
        decoratedShape.draw();
        setRedBoarder(decoratedShape);
    }

    private void setRedBoarder(Shape decoratorShape){
        System.out.println("Border Color: Red");
    }
        
}
