import javax.swing.*;

public class Main {
    public static void createAndShowGUI() { //user story 6
        //Create and set up the window.
        JFrame frame = new JFrame("Tier List");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTextField text = new JTextField("hi");
        frame.add(text);

        frame.pack();

        frame.setSize(1000, 750);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }
}
