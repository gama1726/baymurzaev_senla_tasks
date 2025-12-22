public class Turn {
    private static volatile boolean flag = false;
    public void setFlag() {
        this.flag = true;
    }
    public boolean isFlag() {
        return flag;
    }
}
