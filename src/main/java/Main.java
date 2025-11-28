import java.awt.*;

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

    public static void swtitchTheme(){
        BG_COLOR = new Color(111,111,111);
        frame.getContentPane().setBackground(BG_COLOR);
        frame.repaint();
        frame.revalidate();
    }

    // Creates the visual representation of a single tier row
    public static JPanel createTierRow(Tier tier) {
        JPanel tierRow = new JPanel(new BorderLayout());
        tierRow.setBackground(new Color(30, 30, 30));
        tierRow.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        tierRow.setPreferredSize(new Dimension(0, 100));
        tierRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // Left side: Tier name label with color
        JLabel tierLabel = new JLabel(tier.name, SwingConstants.CENTER);
        tierLabel.setOpaque(true);
        tierLabel.setBackground(tier.color);
        tierLabel.setForeground(Color.BLACK);
        tierLabel.setFont(new Font("Arial", Font.BOLD, 24));
        tierLabel.setPreferredSize(new Dimension(100, 100));
        tierRow.add(tierLabel, BorderLayout.WEST);

        // Right side: Panel to hold items (horizontal flow, no wrapping)
        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.X_AXIS));
        itemsContainer.setBackground(new Color(40, 40, 40));
        
        // Add any existing items in this tier
        for (Item item : tier.items.list) {
            JPanel itemPanel = createItemPanel(item);
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
                    
                    // Add item to this tier
                    tier.items.addItem(selectedItem);
                    
                    // Remove from right panel item list
                    items.removeItem(selectedItem);
                    listModel.removeElement(selectedItem.name);
                    
                    // Clear selection
                    selected = null;
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
                    
                    // Add item to this tier
                    tier.items.addItem(selectedItem);
                    
                    // Remove from right panel item list
                    items.removeItem(selectedItem);
                    listModel.removeElement(selectedItem.name);
                    
                    // Clear selection
                    selected = null;
                    itemList.clearSelection();
                    
                    // Refresh display
                    refreshTierList();
                }
            }
        });

        return tierRow;
    }

    // Creates the visual representation of an item in a tier
    public static JPanel createItemPanel(Item item) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setPreferredSize(new Dimension(80, 80));
        itemPanel.setBackground(new Color(60, 60, 60));
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel nameLabel = new JLabel(item.name, SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        itemPanel.add(nameLabel, BorderLayout.CENTER);

        return itemPanel;
    }

    // Refreshes the tier list display
    public static void refreshTierList() {
        tierListPanel.removeAll();
        
        // Add tiers in order (by position key)
        for (int i = 0; i < tierList.tiers.size(); i++) {
            Tier tier = tierList.tiers.get(i);
            if (tier != null) {
                JPanel tierRow = createTierRow(tier);
                tierListPanel.add(tierRow);
            }
        }

        tierListPanel.revalidate();
        tierListPanel.repaint();
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

        // Initialize the tier list data
        tierList = new TierList();
        
        // ---- LEFT PANEL (TIER LIST) ----
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BG_COLOR);

        // Panel that holds all tier rows
        tierListPanel = new JPanel();
        tierListPanel.setLayout(new BoxLayout(tierListPanel, BoxLayout.Y_AXIS));
        tierListPanel.setBackground(BG_COLOR);

        // Wrap in scroll pane for when there are many tiers
        JScrollPane tierScrollPane = new JScrollPane(tierListPanel);
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
        
        // ---- RIGHT PANEL ----


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension( (int)WINDOW_SIZE_WIDTH/6, WINDOW_SIZE_HEIGHT));
        rightPanel.setBackground(BG_COLOR);


        // ITEM LIST
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(BG_COLOR);
        
        // TEXT INPUT FIELD and DELETE BUTTON
        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(0, 40));

        JButton deleteButton = new JButton("DELETE");
        deleteButton.setMargin(new Insets(10, 200, 10, 200)); // Makes the button bigger
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


        listModel = new DefaultListModel<>(); //999
        itemList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(itemList);
        scrollPane.setBorder(null);
        itemPanel.add(scrollPane);
        rightPanel.add(scrollPane, BorderLayout.CENTER);


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
                JScrollBar vert = scrollPane.getVerticalScrollBar();
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
                    deleteButton.setVisible(true);
                } else {
                    selected = null;
                    deleteButton.setVisible(false);
                }
            }
        });
    



        frame.add(rightPanel, BorderLayout.EAST);
        frame.setVisible(true);
    }

    public static Item selectionConverter(String text, ItemList items){ // SHOULD be overriden for other objects being selected like tiers or buttons
        Item item = items.searchItem(text); // will work after ItemList TODOS are done
        return item;
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }
}