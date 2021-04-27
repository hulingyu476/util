public class BuyStock implements Order {
    private Stock abcStock;

    public BuyStock(Stock stock){
        this.abcStock = stock;
    }

    public void execute() {
       abcStock.buy();
    }
}
