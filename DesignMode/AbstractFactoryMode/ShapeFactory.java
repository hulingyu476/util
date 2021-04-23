//ShapeFactory.java
public class ShapeFactory extends AbstractFactory{
    @Override
    public Shape getShape(String shapeType) {
        if( shapeType == null) {
            return null;
        }

        if( shapeType.equalsIgnoreCase("CIRCLE")){
            return new Circle();
        }
        if( shapeType.equalsIgnoreCase("RECTANGLE")){
            return new Rectangle();
        }
        if( shapeType.equalsIgnoreCase("SQUARE")){
            return new Square();
        }
        return null;
    }

    @Override
    public Color getColor(String color) {
        return null;
    }
}