import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ItemListTest {
    private ItemList itemList;
    private Item item1;
    private Item item2;
    private Item item3;

    @Before
    public void setUp() {
        itemList = new ItemList();
        item1 = new Item("Apple");
        item2 = new Item("Banana");
        item3 = new Item("Cherry");
    }

    @Test
    public void testConstructor() {
        ItemList newList = new ItemList();
        assertNotNull(newList.list);
        assertEquals(0, newList.list.size());
    }

    @Test
    public void testAddItem() {
        itemList.addItem(item1);
        assertEquals(1, itemList.list.size());
        assertTrue(itemList.list.contains(item1));
    }

    @Test
    public void testAddMultipleItems() {
        itemList.addItem(item1);
        itemList.addItem(item2);
        itemList.addItem(item3);
        assertEquals(3, itemList.list.size());
        assertTrue(itemList.list.contains(item1));
        assertTrue(itemList.list.contains(item2));
        assertTrue(itemList.list.contains(item3));
    }

    @Test
    public void testAddDuplicateItem() {
        itemList.addItem(item1);
        itemList.addItem(item1);
        assertEquals(2, itemList.list.size());
    }

    @Test
    public void testRemoveItem() {
        itemList.addItem(item1);
        itemList.addItem(item2);
        itemList.removeItem(item1);
        assertEquals(1, itemList.list.size());
        assertFalse(itemList.list.contains(item1));
        assertTrue(itemList.list.contains(item2));
    }

    @Test
    public void testRemoveItemNotInList() {
        itemList.addItem(item1);
        itemList.removeItem(item2);
        assertEquals(1, itemList.list.size());
        assertTrue(itemList.list.contains(item1));
    }

    @Test
    public void testRemoveFromEmptyList() {
        itemList.removeItem(item1);
        assertEquals(0, itemList.list.size());
    }

    @Test
    public void testSearchItemFound() {
        itemList.addItem(item1);
        itemList.addItem(item2);
        Item result = itemList.searchItem("Apple");
        assertNotNull(result);
        assertEquals(item1, result);
        assertEquals("Apple", result.name);
    }

    @Test
    public void testSearchItemNotFound() {
        itemList.addItem(item1);
        itemList.addItem(item2);
        Item result = itemList.searchItem("Orange");
        assertNull(result);
    }

    @Test
    public void testSearchItemInEmptyList() {
        Item result = itemList.searchItem("Apple");
        assertNull(result);
    }

    @Test
    public void testSearchItemWithNullName() {
        itemList.addItem(item1);
        Item result = itemList.searchItem(null);
        assertNull(result);
    }

    @Test
    public void testSearchItemCaseSensitive() {
        itemList.addItem(item1);
        Item result = itemList.searchItem("apple");
        assertNull(result);
    }

    @Test
    public void testSearchItemWithMultipleItems() {
        itemList.addItem(item1);
        itemList.addItem(item2);
        itemList.addItem(item3);
        Item result = itemList.searchItem("Banana");
        assertNotNull(result);
        assertEquals(item2, result);
    }

    @Test
    public void testAddAndSearchMultipleOperations() {
        itemList.addItem(item1);
        assertNotNull(itemList.searchItem("Apple"));
        itemList.addItem(item2);
        assertNotNull(itemList.searchItem("Banana"));
        itemList.removeItem(item1);
        assertNull(itemList.searchItem("Apple"));
        assertNotNull(itemList.searchItem("Banana"));
    }
}
