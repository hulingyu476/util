public class DecoratorPattenDemo {
    public static void main(String[] args) {
        Shape circle = new Circle();
        ShapeDecorator redCircle = new RedShapeDecorator(new Circle());
        ShapeDecorator redRectangle = new RedShapeDecorator(new Rectangle());

        System.out.println("\nCircle with normal border");
        circle.draw();

        System.out.println("\nCircle with Red border");
        redCircle.draw();

        System.out.println("\nRectangle with normal border");
        redRectangle.draw();
    }
}
