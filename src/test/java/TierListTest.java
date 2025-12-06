import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.awt.Color;

public class TierListTest {
    private TierList tierList;

    @Before
    public void setUp() {
        tierList = new TierList();
    }

    @Test
    public void testConstructor() {
        TierList newTierList = new TierList();
        
        assertNotNull(newTierList.tiers);
        assertEquals(6, newTierList.tiers.size());
    }

    @Test
    public void testConstructorCreatesDefaultTiers() {
        assertNotNull(tierList.tiers);
        assertEquals(6, tierList.tiers.size());
        
        // Verify all default tiers exist at correct positions
        assertTrue(tierList.tiers.containsKey(0));
        assertTrue(tierList.tiers.containsKey(1));
        assertTrue(tierList.tiers.containsKey(2));
        assertTrue(tierList.tiers.containsKey(3));
        assertTrue(tierList.tiers.containsKey(4));
        assertTrue(tierList.tiers.containsKey(5));
    }

    @Test
    public void testDefaultTierNames() {
        assertEquals("S", tierList.tiers.get(0).name);
        assertEquals("A", tierList.tiers.get(1).name);
        assertEquals("B", tierList.tiers.get(2).name);
        assertEquals("C", tierList.tiers.get(3).name);
        assertEquals("D", tierList.tiers.get(4).name);
        assertEquals("F", tierList.tiers.get(5).name);
    }

    @Test
    public void testDefaultTierColors() {
        assertEquals(Color.WHITE, tierList.tiers.get(0).color);
        assertEquals(Color.WHITE, tierList.tiers.get(1).color);
        assertEquals(Color.WHITE, tierList.tiers.get(2).color);
        assertEquals(Color.WHITE, tierList.tiers.get(3).color);
        assertEquals(Color.WHITE, tierList.tiers.get(4).color);
        assertEquals(Color.WHITE, tierList.tiers.get(5).color);
    }

    @Test
    public void testDefaultTiersHaveEmptyItemLists() {
        for (int i = 0; i < 6; i++) {
            assertNotNull(tierList.tiers.get(i).items);
            assertEquals(0, tierList.tiers.get(i).items.list.size());
        }
    }

    @Test
    public void testTiersMapNotNull() {
        assertNotNull(tierList.tiers);
    }

    @Test
    public void testEachTierHasUniquePosition() {
        for (int i = 0; i < 6; i++) {
            Tier tier = tierList.tiers.get(i);
            assertNotNull("Tier at position " + i + " should not be null", tier);
        }
    }

    @Test
    public void testModifyTierName() {
        Tier tier = tierList.tiers.get(0);
        tier.name = "S+";
        
        assertEquals("S+", tierList.tiers.get(0).name);
    }

    @Test
    public void testModifyTierColor() {
        Tier tier = tierList.tiers.get(0);
        tier.color = Color.RED;
        
        assertEquals(Color.RED, tierList.tiers.get(0).color);
    }

    @Test
    public void testAddItemToTier() {
        Tier tier = tierList.tiers.get(0);
        Item item = new Item("TestItem");
        tier.items.addItem(item);
        
        assertEquals(1, tier.items.list.size());
        assertTrue(tier.items.list.contains(item));
    }

    @Test
    public void testAddMultipleItemsToTier() {
        Tier tier = tierList.tiers.get(0);
        Item item1 = new Item("Item1");
        Item item2 = new Item("Item2");
        Item item3 = new Item("Item3");
        
        tier.items.addItem(item1);
        tier.items.addItem(item2);
        tier.items.addItem(item3);
        
        assertEquals(3, tier.items.list.size());
    }

    @Test
    public void testRemoveItemFromTier() {
        Tier tier = tierList.tiers.get(0);
        Item item = new Item("TestItem");
        tier.items.addItem(item);
        tier.items.removeItem(item);
        
        assertEquals(0, tier.items.list.size());
        assertFalse(tier.items.list.contains(item));
    }

    @Test
    public void testReplaceTierAtPosition() {
        Tier newTier = new Tier(new ItemList(), Color.BLUE, "Custom");
        tierList.tiers.put(0, newTier);
        
        assertEquals("Custom", tierList.tiers.get(0).name);
        assertEquals(Color.BLUE, tierList.tiers.get(0).color);
    }

    @Test
    public void testRemoveTierFromMap() {
        tierList.tiers.remove(0);
        
        assertEquals(5, tierList.tiers.size());
        assertFalse(tierList.tiers.containsKey(0));
    }

    @Test
    public void testAddNewTierAtCustomPosition() {
        Tier newTier = new Tier(new ItemList(), Color.MAGENTA, "S+");
        tierList.tiers.put(6, newTier);
        
        assertEquals(7, tierList.tiers.size());
        assertEquals("S+", tierList.tiers.get(6).name);
    }

    @Test
    public void testClearAllTiers() {
        tierList.tiers.clear();
        
        assertEquals(0, tierList.tiers.size());
        assertTrue(tierList.tiers.isEmpty());
    }

    @Test
    public void testSwapTiers() {
        Tier tier0 = tierList.tiers.get(0);
        Tier tier1 = tierList.tiers.get(1);
        
        tierList.tiers.put(0, tier1);
        tierList.tiers.put(1, tier0);
        
        assertEquals("A", tierList.tiers.get(0).name);
        assertEquals("S", tierList.tiers.get(1).name);
    }

    @Test
    public void testMultipleTierListsAreIndependent() {
        TierList tierList1 = new TierList();
        TierList tierList2 = new TierList();
        
        tierList1.tiers.get(0).name = "Modified";
        
        assertEquals("Modified", tierList1.tiers.get(0).name);
        assertEquals("S", tierList2.tiers.get(0).name);
    }

    @Test
    public void testTierItemListsAreIndependent() {
        Item item = new Item("TestItem");
        tierList.tiers.get(0).items.addItem(item);
        
        assertEquals(1, tierList.tiers.get(0).items.list.size());
        assertEquals(0, tierList.tiers.get(1).items.list.size());
    }

    @Test
    public void testGetNonExistentTier() {
        Tier tier = tierList.tiers.get(99);
        
        assertNull(tier);
    }

    @Test
    public void testTierOrderPreserved() {
        String[] expectedNames = {"S", "A", "B", "C", "D", "F"};
        
        for (int i = 0; i < 6; i++) {
            assertEquals(expectedNames[i], tierList.tiers.get(i).name);
        }
    }

    @Test
    public void testModifyMultipleTierProperties() {
        tierList.tiers.get(0).name = "Excellent";
        tierList.tiers.get(0).color = Color.red;
        tierList.tiers.get(0).items.addItem(new Item("Item1"));
        
        tierList.tiers.get(1).name = "Good";
        tierList.tiers.get(1).color = Color.blue;
        tierList.tiers.get(1).items.addItem(new Item("Item2"));
        
        assertEquals("Excellent", tierList.tiers.get(0).name);
        assertEquals(Color.red, tierList.tiers.get(0).color);
        assertEquals(1, tierList.tiers.get(0).items.list.size());
        
        assertEquals("Good", tierList.tiers.get(1).name);
        assertEquals(Color.blue, tierList.tiers.get(1).color);
        assertEquals(1, tierList.tiers.get(1).items.list.size());
    }

    @Test
    public void testRebuildTierMap() {
        // Remove some tiers and rebuild with new indices
        tierList.tiers.remove(2);
        tierList.tiers.remove(4);
        
        // Rebuild map with consecutive indices
        java.util.HashMap<Integer, Tier> newTiers = new java.util.HashMap<>();
        int newIndex = 0;
        for (int i = 0; i < 6; i++) {
            Tier tier = tierList.tiers.get(i);
            if (tier != null) {
                newTiers.put(newIndex, tier);
                newIndex++;
            }
        }
        
        tierList.tiers = newTiers;
        
        assertEquals(4, tierList.tiers.size());
        assertTrue(tierList.tiers.containsKey(0));
        assertTrue(tierList.tiers.containsKey(1));
        assertTrue(tierList.tiers.containsKey(2));
        assertTrue(tierList.tiers.containsKey(3));
        assertFalse(tierList.tiers.containsKey(4));
    }

    @Test
    public void testTierWithCustomColor() {
        Color customColor = new Color(255, 128, 0);
        tierList.tiers.get(0).color = customColor;
        
        assertEquals(customColor, tierList.tiers.get(0).color);
        assertEquals(255, tierList.tiers.get(0).color.getRed());
        assertEquals(128, tierList.tiers.get(0).color.getGreen());
        assertEquals(0, tierList.tiers.get(0).color.getBlue());
    }

    @Test
    public void testIterateOverAllTiers() {
        int count = 0;
        for (int i = 0; i < tierList.tiers.size(); i++) {
            Tier tier = tierList.tiers.get(i);
            if (tier != null) {
                assertNotNull(tier.name);
                assertNotNull(tier.color);
                assertNotNull(tier.items);
                count++;
            }
        }
        assertEquals(6, count);
    }
}
