import java.awt.Color;

public class Tier{
    // This class is just a data structure
    public ItemList items;
    public Color color;
    public String name;

    public Tier(ItemList items, Color color, String name){
        this.items = items;
        this.color = color;
        this.name = name;
    }
}