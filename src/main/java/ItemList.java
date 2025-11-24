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
    public Item searchItem(String name) {
        // search for an item and return it
        for (Item item : list) {
            if (item.values[0] != null && item.values[0].equals(name)) {
                return item;
            }
        }
        return null;  // if not found
    }
}

