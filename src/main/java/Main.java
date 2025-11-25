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

    public static void swtitchTheme(){
        BG_COLOR = new Color(111,111,111);
        frame.getContentPane().setBackground(BG_COLOR);
        frame.repaint();
        frame.revalidate();
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
        
        // RIGHT PANEL
        JButton deleteButton = new JButton("DELETE");
        deleteButton.setMargin(new Insets(10, 100, 10, 100));
        deleteButton.addActionListener(e -> {
            //This button deletes elements from the item list
            int index = itemList.getSelectedIndex();
            if (index != -1) {
                listModel.remove(index);
            }
        });


        JPanel buttonPanel_1 = new JPanel();
        buttonPanel_1.setLayout(new BoxLayout(buttonPanel_1, BoxLayout.X_AXIS));
        buttonPanel_1.add(deleteButton);


        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension( (int)WINDOW_SIZE_WIDTH/3, WINDOW_SIZE_HEIGHT));
        rightPanel.setBackground(BG_COLOR);

        rightPanel.add(buttonPanel_1, BorderLayout.NORTH);


        // ITEM LIST
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(BG_COLOR);
        
        // TEXT INPUT
        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(0, 40));

        listModel = new DefaultListModel<>(); //999
        itemList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(itemList);
        scrollPane.setBorder(null);
        itemPanel.add(scrollPane);

        // Add message at the bottom and create item object and item list
        ItemList items = new ItemList();

        inputField.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                Item item = new Item(text);
                //items.addItem(item); Uncomment when ItemList TODOS are done
                listModel.addElement(text);
                
                itemPanel.revalidate();
                itemPanel.repaint();

                // auto-scroll to bottom
                JScrollBar vert = scrollPane.getVerticalScrollBar();
                vert.setValue(vert.getMaximum());

                inputField.setText("");
            }
        });
        
        selected = new Object(); // Selected will be used to track any selected thing ever
        
        //Selection in rightPanel
        itemList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // This ensures the event only fires once
                String selectedValue = itemList.getSelectedValue();
                if (selectedValue != null) {
                    selected = selectionConverter(selectedValue, items);
                }
            }
        });
    

        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(inputField, BorderLayout.SOUTH);

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
