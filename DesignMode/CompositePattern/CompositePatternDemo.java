public class CompositePatternDemo {
    public static void main(String[] args) {
        Employee CEO = new Employee("John", "CEO", 30000);

        Employee headSales = new Employee("Robert", "Head Sales", 21000);
        Employee HeadMarketing = new Employee("Michel", "Head Marketing", 20000);

        Employee cleak1 = new Employee("Laura", "Marketing", 11000);
        Employee cleak2 = new Employee("Bob", "Marketing", 10000);

        Employee sales1 = new Employee("Richard", "Sales", 5000);
        Employee sales2 = new Employee("Rock", "Sales", 6000);

        CEO.add(headSales);
        CEO.add(HeadMarketing);

        headSales.add(sales1);
        headSales.add(sales2);

        HeadMarketing.add(cleak1);
        HeadMarketing.add(cleak2);

        System.out.println(CEO);
        for( Employee headEmployee : CEO.getSubordinates()){
            System.out.println(headEmployee);
            for(Employee employee : headEmployee.getSubordinates()){
                System.out.println(employee);
            }
        }

    }
}
