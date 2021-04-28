import java.util.ArrayList;
import java.util.List;

public class CareTaker {
    private List<Memento> mementoList = new ArrayList<Memento>();

    public void add(Memento state){
        mementoList.add(state);
    }

    public void clearMementoList(){
        mementoList.clear();
    }

    public Memento get(int index){
        if(index < mementoList.size())
            return mementoList.get(index);
        return null;
    }
}
