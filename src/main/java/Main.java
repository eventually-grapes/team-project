import java.awt.*;
import java.io.File;
import java.util.HashMap;

import javax.swing.*;

public class Main {
    final static int WINDOW_SIZE_WIDTH = 1920;
    final static int WINDOW_SIZE_HEIGHT = 1080;
    private static Color BG_COLOR = new Color(0,000,000); // could add another one called element/ foreground color
    private static JFrame frame;
    private static Object selected; // will be used for anything currently selected by the user at any given time
    private static JList<String> itemList;
    private static DefaultListModel<String> listModel;
    private static TierList tierList;
    private static JPanel tierListPanel;
    private static ItemList items; // items in the right panel
    private static Tier selectedSourceTier; // tracks which tier a selected came from (null if from item list
    final static int TIER_HEIGHT = 160;
    private static boolean darkMode = true; // start in dark mode
    private static JPanel leftPanel;
    private static JPanel rightPanel;
    private static JScrollPane tierScrollPane;
    private static JScrollPane itemScrollPane;

    public static void switchTheme(){
        darkMode = !darkMode;
        if (darkMode) {
            BG_COLOR = new Color(0, 0, 0);
            itemList.setBackground(new Color(30, 30, 30));
            itemList.setForeground(Color.WHITE);
        } else {
            BG_COLOR = new Color(220, 220, 220);
            itemList.setBackground(Color.WHITE);
            itemList.setForeground(Color.BLACK);
        }
        // Update all panel backgrounds
        frame.getContentPane().setBackground(BG_COLOR);
        leftPanel.setBackground(BG_COLOR);
        rightPanel.setBackground(BG_COLOR);
        tierListPanel.setBackground(BG_COLOR);
        tierScrollPane.getViewport().setBackground(BG_COLOR);
        itemScrollPane.getViewport().setBackground(BG_COLOR);
        
        refreshTierList();
        frame.repaint();
        frame.revalidate();
    }

    // Creates the visual representation of a single tier row
    public static JPanel createTierRow(Tier tier, int position) {
        JPanel tierRow = new JPanel(new BorderLayout());
        tierRow.setBackground(new Color(30, 30, 30));
        tierRow.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        tierRow.setPreferredSize(new Dimension(0, TIER_HEIGHT));
        tierRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, TIER_HEIGHT));

        // Left side: Panel containing buttons and tier name
        JPanel tierLabelPanel = new JPanel(new BorderLayout());
        tierLabelPanel.setPreferredSize(new Dimension(TIER_HEIGHT, TIER_HEIGHT));
        tierLabelPanel.setBackground(tier.color);

        // Panel to hold buttons vertically on the left side like for the arrow keys up and down
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        // Edit button (color and name)
        JButton editButton = new JButton("✎");
        editButton.setFont(new Font("Arial", Font.PLAIN, 10));
        editButton.setMargin(new Insets(0, 0, 0, 0));
        editButton.setPreferredSize(new Dimension(20, 20));
        editButton.setMaximumSize(new Dimension(20, 20));
        editButton.addActionListener(e -> {
            // panel for dialog box
            JPanel editPanel = new JPanel(new BorderLayout(5, 5));

            // Name field
            JPanel namePanel = new JPanel(new BorderLayout());
            namePanel.add(new JLabel("Tier Name: "), BorderLayout.WEST);
            JTextField nameField = new JTextField(tier.name);
            namePanel.add(nameField, BorderLayout.CENTER);
            editPanel.add(namePanel, BorderLayout.NORTH);

            // Color chooser button
            JButton chooseColorButton = new JButton("Choose Color");
            final Color[] selectedColor = {tier.color}; // use array to allow modification in lambda
            chooseColorButton.setBackground(tier.color);
            chooseColorButton.addActionListener(ev -> {
                Color newColor = JColorChooser.showDialog(frame, "Choose Tier Color", selectedColor[0]);
                if (newColor != null) {
                    selectedColor[0] = newColor;
                    chooseColorButton.setBackground(newColor);
                }
            });
            editPanel.add(chooseColorButton, BorderLayout.CENTER);

            int result = JOptionPane.showConfirmDialog(frame, editPanel, "Edit Tier",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty()) {
                    tier.name = newName;
                }
                tier.color = selectedColor[0];
                refreshTierList();
            }
        });
        buttonPanel.add(editButton);

        // Up button
        JButton upButton = new JButton("▲");
        upButton.setFont(new Font("Arial", Font.PLAIN, 10));
        upButton.setMargin(new Insets(0, 0, 0, 0));
        upButton.setPreferredSize(new Dimension(20, 20));
        upButton.setMaximumSize(new Dimension(20, 20));
        upButton.addActionListener(e -> {
            if (position > 0) {
                // Swap with tier above
                Tier tierAbove = tierList.tiers.get(position - 1);
                tierList.tiers.put(position - 1, tier);
                tierList.tiers.put(position, tierAbove);
                refreshTierList();
            }
        });
        buttonPanel.add(upButton);

        // Down button
        JButton downButton = new JButton("▼");
        downButton.setFont(new Font("Arial", Font.PLAIN, 10));
        downButton.setMargin(new Insets(0, 0, 0, 0));
        downButton.setPreferredSize(new Dimension(20, 20));
        downButton.setMaximumSize(new Dimension(20, 20));
        downButton.addActionListener(e -> {
            if (position < tierList.tiers.size() - 1) {
                // Swap with tier below
                Tier tierBelow = tierList.tiers.get(position + 1);
                tierList.tiers.put(position + 1, tier);
                tierList.tiers.put(position, tierBelow);
                refreshTierList();
            }
        });
        buttonPanel.add(downButton);

        tierLabelPanel.add(buttonPanel, BorderLayout.WEST);

        // Delete butto
        JButton deleteButton = new JButton("X");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 10));
        deleteButton.setMargin(new Insets(0, 0, 0, 0));
        deleteButton.setPreferredSize(new Dimension(20, 20));
        deleteButton.setForeground(Color.RED);
        deleteButton.addActionListener(e -> {
            // Move all items back to the item list
            for (Item item : tier.items.list) {
                items.addItem(item);
                listModel.addElement(item.name);
            }

            // Remove this tier and shift all tiers above down
            tierList.tiers.remove(position);

            // Reindex  tiers
            HashMap<Integer, Tier> newTiers = new HashMap<>();
            int newIndex = 0;
            for (int i = 0; i < tierList.tiers.size() + 1; i++) {
                if (i != position) {
                    Tier t = tierList.tiers.get(i);
                    if (t != null) {
                        newTiers.put(newIndex, t);
                        newIndex++;
                    }
                }
            }
            tierList.tiers = newTiers;

            refreshTierList();
        });

        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 2));
        deletePanel.setOpaque(false);
        deletePanel.add(deleteButton);
        tierLabelPanel.add(deletePanel, BorderLayout.EAST);

        // Tier name label
        JLabel tierLabel = new JLabel(tier.name, SwingConstants.CENTER);
        tierLabel.setOpaque(false);
        tierLabel.setForeground(Color.BLACK);
        tierLabel.setFont(new Font("Arial", Font.BOLD, 24));
        tierLabelPanel.add(tierLabel, BorderLayout.CENTER);

        tierRow.add(tierLabelPanel, BorderLayout.WEST);

        // Right side: Panel to hold items (horizontal flow, no wrapping)
        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.X_AXIS));
        itemsContainer.setBackground(new Color(40, 40, 40));
        
        // Add any existing items in this tier
        for (Item item : tier.items.list) {
            JPanel itemPanel = createItemPanel(item, tier);
            itemsContainer.add(itemPanel);
            itemsContainer.add(Box.createRigidArea(new Dimension(5, 0))); // spacing between items
        }

        // Wrap items container in a scroll pane for horizontal scrolling
        JScrollPane itemsScrollPane = new JScrollPane(itemsContainer);
        itemsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        itemsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        itemsScrollPane.setBorder(null);
        itemsScrollPane.getViewport().setBackground(new Color(40, 40, 40));

        tierRow.add(itemsScrollPane, BorderLayout.CENTER);

        // Click listener for the entire tier row - adds selected item to this tier
        tierRow.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selected instanceof Item) {
                    Item selectedItem = (Item) selected;
                    
                    // If item is from same tier, do nothing
                    if (selectedSourceTier == tier) {
                        return;
                    }

                    // Add item to this tier
                    tier.items.addItem(selectedItem);
                    
                    // Remove from source (either another tier or item list)
                    if (selectedSourceTier != null) {
                        // Item came from another tier
                        selectedSourceTier.items.removeItem(selectedItem);
                    } else {
                        // Item came from right panel item list
                        items.removeItem(selectedItem);
                        listModel.removeElement(selectedItem.name);
                    }
                    
                    // Clear selection
                    selected = null;
                    selectedSourceTier = null;
                    itemList.clearSelection();
                    
                    // Refresh display
                    refreshTierList();
                }
            }
        });

        // Also add click listener to itemsContainer so clicking empty space works
        itemsContainer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selected instanceof Item) {
                    Item selectedItem = (Item) selected;
                    
                    // If item is from same tier, do nothing
                    if (selectedSourceTier == tier) {
                        return;
                    }

                    // Add item to this tier
                    tier.items.addItem(selectedItem);
                    
                    // Remove from source (either another tier or item list)
                    if (selectedSourceTier != null) {
                        // Item came from another tier
                        selectedSourceTier.items.removeItem(selectedItem);
                    } else {
                        // Item came from right panel item list
                        items.removeItem(selectedItem);
                        listModel.removeElement(selectedItem.name);
                    }
                    
                    // Clear selection
                    selected = null;
                    selectedSourceTier = null;
                    itemList.clearSelection();
                    
                    // Refresh display
                    refreshTierList();
                }
            }
        });

        return tierRow;
    }

    // Creates the visual representation of an item in a tier
    public static JPanel createItemPanel(Item item, Tier sourceTier) {
        JPanel itemPanel = item.getItemGUI();
        Dimension guiPref = itemPanel.getPreferredSize();

        itemPanel.setPreferredSize(guiPref);
        itemPanel.setMaximumSize(guiPref);
        itemPanel.setMinimumSize(guiPref);
        itemPanel.setBackground(new Color(60, 60, 60));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        // Send back button in top right corner
        JButton sendBackButton = new JButton("→");
        sendBackButton.setFont(new Font("Arial", Font.PLAIN, 8));
        sendBackButton.setMargin(new Insets(0, 0, 0, 0));
        sendBackButton.setPreferredSize(new Dimension(16, 16));
        sendBackButton.addActionListener(e -> {
            // Remove from tier
            sourceTier.items.removeItem(item);

            // Add to item list on the right
            items.addItem(item);
            listModel.addElement(item.name);

            // Clear selection if this item was selected
            if (selected == item) {
                selected = null;
                selectedSourceTier = null;
            }

            // Refresh display
            refreshTierList();
        });

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottomBar.setOpaque(false);
        bottomBar.add(sendBackButton);
        itemPanel.add(bottomBar, BorderLayout.SOUTH);

        // Click listener to select this item or insert selected item
        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // If there's a selected item and it's not this item
                if (selected instanceof Item && selected != item) {
                    Item selectedItem = (Item) selected;

                    // if clicked on left or right half
                    int clickX = e.getX();
                    int panelWidth = itemPanel.getWidth();
                    boolean clickedRightHalf = clickX > panelWidth / 2;
                    int clickedIndex = sourceTier.items.list.indexOf(item);

                    // Remove selected
                    if (selectedSourceTier != null) {
                        selectedSourceTier.items.removeItem(selectedItem);
                    } else {
                        items.removeItem(selectedItem);
                        listModel.removeElement(selectedItem.name);
                    }

                    // Recalculate index after removal (in case item was in same tier before clicked item)
                    clickedIndex = sourceTier.items.list.indexOf(item);

                    // Insert at the correct position
                    int insertIndex;
                    if (clickedRightHalf) {
                        // Insert after the clicked item
                        insertIndex = clickedIndex + 1;
                    } else {
                        // Insert befor
                        insertIndex = clickedIndex;
                    }

                    // What if index wasnt valid
                    if (insertIndex < 0) insertIndex = 0;
                    if (insertIndex > sourceTier.items.list.size()) insertIndex = sourceTier.items.list.size();

                    sourceTier.items.list.add(insertIndex, selectedItem);

                    // Clear selection
                    selected = null;
                    selectedSourceTier = null;
                    itemList.clearSelection();

                    // Refresh display
                    refreshTierList();
                } else {
                    // No item selected or clicking same item - select this item
                    selected = item;
                    selectedSourceTier = sourceTier;
                    // Visual feedback - highlight selected item
                    itemPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                    refreshTierList();
                }
            }
        });

        // Highlight if this item is selected
        if (selected == item) {
            itemPanel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        }

        return itemPanel;
    }

    // Refreshes the tier list display
    public static void refreshTierList() {
        tierListPanel.removeAll();
        
        // Add tiers in order (by position key)
        for (int i = 0; i < tierList.tiers.size(); i++) {
            Tier tier = tierList.tiers.get(i);
            if (tier != null) {
                JPanel tierRow = createTierRow(tier, i);
                tierListPanel.add(tierRow);
            }
        }

        tierListPanel.revalidate();
        tierListPanel.repaint();
    }

    // JSON HELPER METHODS

    // JSON serialization method
    private static void saveTierListToFile(File file) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            org.json.JSONObject json = new org.json.JSONObject();
            org.json.JSONArray tiersArray = new org.json.JSONArray();

            for (int i = 0; i < tierList.tiers.size(); i++) {
                Tier tier = tierList.tiers.get(i);
                if (tier != null) {
                    org.json.JSONObject tierObj = new org.json.JSONObject();

                    tierObj.put("name", tier.name);
                    tierObj.put("color", tier.color.getRGB());

                    org.json.JSONArray itemsArray = new org.json.JSONArray();
                    for (Item item : tier.items.list) {
                        org.json.JSONArray itemTuple = new org.json.JSONArray();
                        itemTuple.put(item.name);
                        itemTuple.put(item.imageDir);
                        itemsArray.put(itemTuple);
                    }
                    tierObj.put("items", itemsArray);
                    tiersArray.put(tierObj);
                }
            }
            json.put("tiers", tiersArray);
            writer.write(json.toString(2));
            JOptionPane.showMessageDialog(frame, "Tier list saved successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error saving: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // JSON deserialization method
    private static void loadTierListFromFile(File file) {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            org.json.JSONObject json = new org.json.JSONObject(content);
            org.json.JSONArray tiersArray = json.getJSONArray("tiers");

            tierList.tiers.clear();
            items.list.clear();
            listModel.clear();

            for (int i = 0; i < tiersArray.length(); i++) {
                org.json.JSONObject tierObj = tiersArray.getJSONObject(i);
                String name = tierObj.getString("name");
                Color color = new Color(tierObj.getInt("color"));

                ItemList tierItems = new ItemList();
                org.json.JSONArray itemsArray = tierObj.getJSONArray("items");
                for (int j = 0; j < itemsArray.length(); j++) {
                    org.json.JSONArray itemTuple = itemsArray.getJSONArray(j);
                    String itemName = itemTuple.getString(0);
                    String imageDir = itemTuple.optString(1, null);
                    if (imageDir == null) {
                        tierItems.addItem(new Item(itemName));
                    }
                    else {
                        tierItems.addItem(new Item(itemName, imageDir));
                    }
                }

                tierList.tiers.put(i, new Tier(tierItems, color, name));
            }

            refreshTierList();
            JOptionPane.showMessageDialog(frame, "Tier list loaded successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error loading: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void createAndShowGUI() { //user story 6
        //Create and set up the window.
        frame = new JFrame("Tier List");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.pack();

        frame.setSize(WINDOW_SIZE_WIDTH, WINDOW_SIZE_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.getContentPane().setBackground(BG_COLOR);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Initialize the tier list data
        tierList = new TierList();
        
        // ---- LEFT PANEL (TIER LIST) ----
        leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_COLOR);

        // Panel that holds all tier rows
        tierListPanel = new JPanel();
        tierListPanel.setLayout(new BoxLayout(tierListPanel, BoxLayout.Y_AXIS));
        tierListPanel.setBackground(BG_COLOR);

        // Wrap in scroll pane for when there are many tiers
        tierScrollPane = new JScrollPane(tierListPanel);
        tierScrollPane.setBorder(null);
        tierScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tierScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tierScrollPane.getViewport().setBackground(BG_COLOR);

        leftPanel.add(tierScrollPane, BorderLayout.CENTER);

        // Text field to add new tiers
        JTextField tierInputField = new JTextField();
        tierInputField.setPreferredSize(new Dimension(0, 40));
        tierInputField.addActionListener(e -> {
            String tierName = tierInputField.getText().trim();
            if (!tierName.isEmpty()) {
                // Create new tier with white color
                Tier newTier = new Tier(new ItemList(), Color.WHITE, tierName);
                
                // Add to tier list at the next position
                int newPosition = tierList.tiers.size();
                tierList.tiers.put(newPosition, newTier);
                
                // Refresh display
                refreshTierList();
                
                tierInputField.setText("");
            }
        });
        leftPanel.add(tierInputField, BorderLayout.SOUTH);

        // Initial render of tier list
        refreshTierList();

        frame.add(leftPanel, BorderLayout.CENTER);




        // ---- RIGHT PANEL (ITEM LIST) ----


        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension( (int)WINDOW_SIZE_WIDTH/6, WINDOW_SIZE_HEIGHT));
        rightPanel.setBackground(BG_COLOR);


        // ITEM LIST
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(BG_COLOR);

        // UPPER (BUTTONS) PANEL
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.X_AXIS));

        JButton saveButton = new JButton("SAVE TIER LIST");
        saveButton.setFont(new Font("Courier New", Font.BOLD, 15));
        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Tier List");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));

            int result = fileChooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".json")) {
                    file = new File(file.getAbsolutePath() + ".json");
                }
                saveTierListToFile(file);
            }
        });
        JButton loadButton = new JButton("LOAD TIER LIST");
        loadButton.setFont(new Font("Courier New", Font.BOLD, 15));
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Load Tier List");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));

            int result = fileChooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                loadTierListFromFile(file);
            }
        });

        upperPanel.add(saveButton);
        upperPanel.add(loadButton);
        
        JToggleButton darkModeToggle = new JToggleButton("☀ Light");
        darkModeToggle.setSelected(true); // start in dark mode
        darkModeToggle.setFont(new Font("Courier New", Font.BOLD, 12));
        darkModeToggle.addActionListener(e -> {
            switchTheme();
            if (darkMode) {
                darkModeToggle.setText("☀ Light");
            } else {
                darkModeToggle.setText("🌙 Dark");
            }
        });
        upperPanel.add(darkModeToggle);
        
        rightPanel.add(upperPanel, BorderLayout.NORTH);



        // TEXT INPUT FIELD and DELETE BUTTON
        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(WINDOW_SIZE_WIDTH/6, 40));
        inputField.setFont(new Font("MV Boli", Font.BOLD, 26));

        JButton deleteButton = new JButton("DELETE");
        deleteButton.setFont(new Font("Courier New", Font.BOLD, 16)); // Button font
        deleteButton.setMargin(new Insets(10, 50, 10, 50)); // Makes the button bigger
        deleteButton.setVisible(false); // Delete buttn nitially hidden
        deleteButton.addActionListener(e -> {
            int index = itemList.getSelectedIndex();
            if (index != -1) {
                listModel.remove(index);
            }
        });

        JPanel buttonPanel_1 = new JPanel();
        buttonPanel_1.setLayout(new BorderLayout());
        buttonPanel_1.add(deleteButton, BorderLayout.NORTH);
        buttonPanel_1.add(inputField, BorderLayout.SOUTH);
        rightPanel.add(buttonPanel_1, BorderLayout.SOUTH);


        listModel = new DefaultListModel<>();
        itemList = new JList<>(listModel);
        itemList.setFixedCellHeight(50);  // List Height
        itemList.setFont(new Font("MV Boli", Font.BOLD, 18)); // List Font
        itemList.setBackground(new Color(30, 30, 30)); // Dark mode default
        itemList.setForeground(Color.WHITE); // Dark mode default
        itemScrollPane = new JScrollPane();
        itemScrollPane.setViewportView(itemList);
        itemScrollPane.setBorder(null);
        itemPanel.add(itemScrollPane);
        rightPanel.add(itemScrollPane, BorderLayout.CENTER);



        // Add message at the bottom and create item object and item list
        items = new ItemList();

        inputField.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                Item item = new Item(text);
                items.addItem(item);
                listModel.addElement(text);
                
                itemPanel.revalidate();
                itemPanel.repaint();

                // auto-scroll to bottom
                JScrollBar vert = itemScrollPane.getVerticalScrollBar();
                vert.setValue(vert.getMaximum());

                inputField.setText("");
            }
        });
        

        //Selection in rightPanel
        selected = new Object(); // Selected will be used to track any selected thing ever
        itemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // This ensures the event only fires once
                String selectedValue = itemList.getSelectedValue();
                if (selectedValue != null) {
                    selected = selectionConverter(selectedValue, items);
                    selectedSourceTier = null; // item is from item list, not a tier
                    deleteButton.setVisible(true);
                } else {
                    selected = null;
                    selectedSourceTier = null;
                    deleteButton.setVisible(false);
                }
            }
        });

        // Click listener on roght panel to send tier items back to item list
        rightPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selected instanceof Item && selectedSourceTier != null) {
                    Item selectedItem = (Item) selected;

                    // Remove from source tier
                    selectedSourceTier.items.removeItem(selectedItem);

                    // Add to item list
                    items.addItem(selectedItem);
                    listModel.addElement(selectedItem.name);

                    // Clear selection
                    selected = null;
                    selectedSourceTier = null;

                    // Refresh display
                    refreshTierList();
                }
            }
        });

        // Also add click listener to itemScrollPane for item list
        itemScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (selected instanceof Item && selectedSourceTier != null) {
                    Item selectedItem = (Item) selected;

                    // Remove from source tier
                    selectedSourceTier.items.removeItem(selectedItem);

                    // Add to item list
                    items.addItem(selectedItem);
                    listModel.addElement(selectedItem.name);

                    // Clear selection
                    selected = null;
                    selectedSourceTier = null;

                    // Refresh display
                    refreshTierList();
                }
            }
        });




        frame.add(rightPanel, BorderLayout.EAST);
        frame.setVisible(true);
    }

    public static Item selectionConverter(String text, ItemList items){ // SHOULD be overriden for other objects being selected like tiers or buttons
        Item item = items.searchItem(text);
        return item;
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }
}