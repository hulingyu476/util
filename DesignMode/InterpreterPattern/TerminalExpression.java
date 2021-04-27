public class TerminalExpression implements Expression {
    private String data;

    public TerminalExpression(String data){
        this.data = data;
    }

    @Override
    public boolean interpret(String context) {
        if(context.toLowerCase().contains(data.toLowerCase()))
            return true;
        return false;
    }
    
}
