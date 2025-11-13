package autoservice.model;
/**
 * Автомеханик.
 */
public class Mechanic{
    private int id;
    private String name;

    public Mechanic(int id,String name) {
        this.id = id;
        this.name = name;
    }

    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    @Override
    public String toString(){
        return "Id мастера : " + getId() + ",Имя: " + getName();
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Mechanic)) return false;
        Mechanic m = (Mechanic) o;
        return id == m.id;//Приводим тип и сравниваем логический идентификатор
}
    
}