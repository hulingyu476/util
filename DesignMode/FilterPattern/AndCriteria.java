import java.util.List;

public class AndCriteria implements Criteria {
    private Criteria criteria;
    private Criteria othercriteria;

    public AndCriteria(Criteria criteria, Criteria otherCriteria){
        this.criteria = criteria;
        this.othercriteria = otherCriteria;
    }

    @Override
    public List<Person> meetCriteria(List<Person> persons) {
        List<Person> firstCriterriaPersons = criteria.meetCriteria(persons);
        return othercriteria.meetCriteria(firstCriterriaPersons);
    }
}
