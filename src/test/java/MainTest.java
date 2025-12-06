import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class MainTest {
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private JFrame testFrame;
    private DefaultListModel<String> testListModel;
    private JList<String> testItemList;
    private TierList testTierList;
    private ItemList testItems;

    @Before
    public void setUp() throws Exception {
        // Initialize test GUI components
        testFrame = new JFrame("Test Frame");
        testListModel = new DefaultListModel<>();
        testItemList = new JList<>(testListModel);
        testTierList = new TierList();
        testItems = new ItemList();

        // Use reflection to set private static fields for testing
        setPrivateStaticField("frame", testFrame);
        setPrivateStaticField("listModel", testListModel);
        setPrivateStaticField("itemList", testItemList);
        setPrivateStaticField("tierList", testTierList);
        setPrivateStaticField("items", testItems);
        setPrivateStaticField("selected", null);
        setPrivateStaticField("selectedSourceTier", null);
    }

    @After
    public void tearDown() {
        if (testFrame != null) {
            testFrame.dispose();
        }
    }

    // Helper method to access private static fields using reflection
    private void setPrivateStaticField(String fieldName, Object value) throws Exception {
        Field field = Main.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(null, value);
    }

    private Object getPrivateStaticField(String fieldName) throws Exception {
        Field field = Main.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(null);
    }

    // Helper method to invoke private static methods using reflection
    private Object invokePrivateStaticMethod(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = Main.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(null, args);
    }

    @Test
    public void testSelectionConverter() {
        Item item1 = new Item("TestItem");
        testItems.addItem(item1);

        Item result = Main.selectionConverter("TestItem", testItems);

        assertNotNull(result);
        assertEquals("TestItem", result.name);
        assertEquals(item1, result);
    }

    @Test
    public void testSelectionConverterNotFound() {
        Item item1 = new Item("TestItem");
        testItems.addItem(item1);

        Item result = Main.selectionConverter("NonExistent", testItems);

        assertNull(result);
    }

    @Test
    public void testSelectionConverterEmptyList() {
        Item result = Main.selectionConverter("TestItem", testItems);

        assertNull(result);
    }

    @Test
    public void testCreateTierRowNotNull() {
        Tier tier = new Tier(new ItemList(), Color.RED, "S");

        JPanel tierRow = Main.createTierRow(tier, 0);

        assertNotNull(tierRow);
        assertEquals(Main.TIER_HEIGHT, tierRow.getPreferredSize().height);
    }

    @Test
    public void testCreateTierRowWithItems() {
        ItemList tierItems = new ItemList();
        tierItems.addItem(new Item("Item1"));
        tierItems.addItem(new Item("Item2"));
        Tier tier = new Tier(tierItems, Color.BLUE, "A");

        JPanel tierRow = Main.createTierRow(tier, 0);

        assertNotNull(tierRow);
        assertEquals(Main.TIER_HEIGHT, tierRow.getPreferredSize().height);
    }

    @Test
    public void testCreateItemPanel() {
        Item item = new Item("TestItem");
        Tier tier = new Tier(new ItemList(), Color.GREEN, "B");

        JPanel itemPanel = Main.createItemPanel(item, tier);

        assertNotNull(itemPanel);
        assertTrue(itemPanel.getPreferredSize().width > 0);
        assertTrue(itemPanel.getPreferredSize().height > 0);
    }

    @Test
    public void testRefreshTierListEmpty() throws Exception {
        JPanel tierListPanel = new JPanel();
        tierListPanel.setLayout(new BoxLayout(tierListPanel, BoxLayout.Y_AXIS));
        setPrivateStaticField("tierListPanel", tierListPanel);

        testTierList.tiers.clear();

        Main.refreshTierList();

        assertEquals(0, tierListPanel.getComponentCount());
    }

    @Test
    public void testSaveTierListToFile() throws Exception {
        File tempFile = tempFolder.newFile("test_tierlist.json");

        // Setup test data
        ItemList tierItems = new ItemList();
        tierItems.addItem(new Item("Item1"));
        tierItems.addItem(new Item("Item2"));
        testTierList.tiers.put(0, new Tier(tierItems, Color.RED, "S"));
        testTierList.tiers.put(1, new Tier(new ItemList(), Color.BLUE, "A"));

        // Invoke the private method
        invokePrivateStaticMethod("saveTierListToFile", new Class<?>[]{File.class}, tempFile);

        // Verify file was created and has content
        assertTrue(tempFile.exists());
        assertTrue(tempFile.length() > 0);

        // Read and verify content
        String content = new String(java.nio.file.Files.readAllBytes(tempFile.toPath()));
        assertTrue(content.contains("\"tiers\""));
        assertTrue(content.contains("\"name\""));
        assertTrue(content.contains("S"));
        assertTrue(content.contains("A"));
    }

    @Test
    public void testLoadTierListFromFile() throws Exception {
        File tempFile = tempFolder.newFile("test_load.json");

        // Create a valid JSON file
        String jsonContent = "{\n" +
                "  \"tiers\": [\n" +
                "    {\n" +
                "      \"name\": \"S\",\n" +
                "      \"color\": " + Color.RED.getRGB() + ",\n" +
                "      \"items\": [\n" +
                "        [\"Item1\", null],\n" +
                "        [\"Item2\", null]\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"A\",\n" +
                "      \"color\": " + Color.BLUE.getRGB() + ",\n" +
                "      \"items\": []\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.write(jsonContent);
        }

        // Invoke the private method
        invokePrivateStaticMethod("loadTierListFromFile", new Class<?>[]{File.class}, tempFile);

        // Verify data was loaded
        assertEquals(2, testTierList.tiers.size());
        assertEquals("S", testTierList.tiers.get(0).name);
        assertEquals("A", testTierList.tiers.get(1).name);
        assertEquals(2, testTierList.tiers.get(0).items.list.size());
        assertEquals(0, testTierList.tiers.get(1).items.list.size());
    }

    @Test
    public void testLoadTierListFromFileWithImages() throws Exception {
        File tempFile = tempFolder.newFile("test_load_images.json");

        // Create a valid JSON file with image directories
        String jsonContent = "{\n" +
                "  \"tiers\": [\n" +
                "    {\n" +
                "      \"name\": \"S\",\n" +
                "      \"color\": " + Color.RED.getRGB() + ",\n" +
                "      \"items\": [\n" +
                "        [\"Item1\", \"path/to/image1.png\"],\n" +
                "        [\"Item2\", \"path/to/image2.png\"]\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.write(jsonContent);
        }

        // Invoke the private method
        invokePrivateStaticMethod("loadTierListFromFile", new Class<?>[]{File.class}, tempFile);

        // Verify data was loaded with image directories
        assertEquals(1, testTierList.tiers.size());
        assertEquals("S", testTierList.tiers.get(0).name);
        assertEquals(2, testTierList.tiers.get(0).items.list.size());
        assertEquals("path/to/image1.png", testTierList.tiers.get(0).items.list.get(0).imageDir);
        assertEquals("path/to/image2.png", testTierList.tiers.get(0).items.list.get(1).imageDir);
    }

    @Test
    public void testCreateTierRowAtDifferentPositions() {
        testTierList.tiers.put(0, new Tier(new ItemList(), Color.RED, "S"));
        testTierList.tiers.put(1, new Tier(new ItemList(), Color.ORANGE, "A"));
        testTierList.tiers.put(2, new Tier(new ItemList(), Color.YELLOW, "B"));

        JPanel tierRow0 = Main.createTierRow(testTierList.tiers.get(0), 0);
        JPanel tierRow1 = Main.createTierRow(testTierList.tiers.get(1), 1);
        JPanel tierRow2 = Main.createTierRow(testTierList.tiers.get(2), 2);

        assertNotNull(tierRow0);
        assertNotNull(tierRow1);
        assertNotNull(tierRow2);
    }


    @Test
    public void testCreateItemPanelWithDifferentTiers() {
        Item item1 = new Item("Item1");
        Item item2 = new Item("Item2");
        
        Tier tier1 = new Tier(new ItemList(), Color.RED, "S");
        Tier tier2 = new Tier(new ItemList(), Color.BLUE, "A");

        JPanel panel1 = Main.createItemPanel(item1, tier1);
        JPanel panel2 = Main.createItemPanel(item2, tier2);

        assertNotNull(panel1);
        assertNotNull(panel2);
        assertNotEquals(panel1, panel2);
    }

    @Test
    public void testWindowConstants() {
        assertEquals(1920, Main.WINDOW_SIZE_WIDTH);
        assertEquals(1080, Main.WINDOW_SIZE_HEIGHT);
        assertEquals(160, Main.TIER_HEIGHT);
    }
}
