//步骤 4
//使用该工厂，通过传递类型信息来获取实体类的对象。
//FactoryPatternDemo.java
public class FactoryPatternDemo {
    public static void main(String[] args) {
        ShapeFactory shapefatory = new ShapeFactory();

        //获取Circle的对象，并调用它的draw方法
        Shape shape1 = shapefatory.getShape("CIRCLE");
        shape1.draw();

        //获取Rectangle的对象，并调用它的draw方法
        Shape shape2 = shapefatory.getShape("RECTANGLE");
        shape2.draw();

        //获取Square的对象，并调用它的draw方法
        Shape shape3 = shapefatory.getShape("SQUARE");
        shape3.draw();        
    }
}
