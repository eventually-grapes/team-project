import java.util.ArrayList;

public class ItemList {
    public ArrayList<Item> list;

    public ItemList(){
        this.list = new ArrayList<Item>();
    }
    public void addItem(Item item){
        this.list.add(item);
    }
    public void removeItem(Item item){   //should not be static, right?
        // remove item by searching for it
        this.list.remove(item);
    }
    public static Item searchItems(String name){
        // search for the items name and return it
    return new Item("a"); // placeholder line
    }
}
