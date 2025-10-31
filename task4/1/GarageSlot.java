public class GarageSlot {
    private int id;
    private boolean isOccupied = false;

    public GarageSlot(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public boolean getStatus(){
        return isOccupied;
    }
    public void occupy(){
        isOccupied = true;
    }
    public void unOccupy(){
        isOccupied = false;
    }
    @Override
    public String toString(){
        return "Id слота: " + getId() + ",Статус: " + (getStatus()?"Занято" : "Свободно");
    }


}
