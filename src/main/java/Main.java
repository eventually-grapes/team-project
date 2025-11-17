import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.*;

public class Main {
final static int WINDOW_SIZE_WIDTH = 1920;
final static int WINDOW_SIZE_HEIGHT = 1080;
private static Color BG_COLOR = new Color(0,000,000); // could add another one called element/ foreground color
private static JFrame frame;  

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
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension( (int)WINDOW_SIZE_WIDTH/3, WINDOW_SIZE_HEIGHT));
        rightPanel.setBackground(BG_COLOR);
    
        // ITEM LIST
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(BG_COLOR);
        
        // TEXT INPUT
        JTextField inputField = new JTextField();
        inputField.setPreferredSize(new Dimension(0, 40));

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> itemList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(itemList);
        scrollPane.setBorder(null);
        itemPanel.add(scrollPane);

        // Add message at the bottom
        inputField.addActionListener(e -> {
            String text = inputField.getText().trim();
            if (!text.isEmpty()) {
                listModel.addElement(text);
                
                itemPanel.revalidate();
                itemPanel.repaint();

                // auto-scroll to bottom
                JScrollBar vert = scrollPane.getVerticalScrollBar();
                vert.setValue(vert.getMaximum());

                inputField.setText("");
            }
        });

        rightPanel.add(scrollPane, BorderLayout.CENTER);
        rightPanel.add(inputField, BorderLayout.SOUTH);

        frame.add(rightPanel, BorderLayout.EAST);
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }
}
