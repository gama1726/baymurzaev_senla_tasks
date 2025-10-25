class Mechanic{
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
    
}