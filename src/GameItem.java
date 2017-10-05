public abstract class GameItem {
    private char type;
    public String sense;
    GameItem(char c, String s){
        type = c;
        sense = s;
    }
    public char display(){
        return type;
    }
}
