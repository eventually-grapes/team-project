import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import kotlin.internal.RequireKotlin.Container;

public class Item{
    public String name;
    public String imageDir;
    public Image image;

    public Item(String itemName){
        this.name = itemName;  
        this.imageDir = null;
        this.image = null;
    }
    public Item(String itemName, String imageDir){
        this.name = itemName;  
        this.imageDir = imageDir;
        this.image = getImage();
        
    }
    public void setImage(String directory) {
        this.imageDir = directory;
        this.image = getImage();
    }
    public Image getImage() {
        File source = new File(this.imageDir);
        try {
            return ImageIO.read(source);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public JPanel getItemGUI() {
        JPanel itemTile = new JPanel(new BorderLayout());
        itemTile.setPreferredSize(new Dimension(200, 200));
        itemTile.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        JPanel topBar = new JPanel(new BorderLayout());

        JButton uploadButton = new JButton("↑");
        uploadButton.setFont(new Font("Arial", Font.BOLD, 14));
        uploadButton.setMargin(new Insets(0, 0, 0, 0));
        uploadButton.setPreferredSize(new Dimension(30, 30));
        uploadButton.setToolTipText("Image Upload");
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            int result = fileChooser.showOpenDialog(null);
            if (result != 1) { //file was actually selected
                setImage(fileChooser.getSelectedFile().getAbsolutePath());
            }
            }
        });
        topBar.add(uploadButton, BorderLayout.WEST);

        JLabel nameLabel = new JLabel(" "+this.name+" ", SwingConstants.CENTER);
        topBar.add(nameLabel, BorderLayout.CENTER);

        JButton searchButton = new JButton("⌕");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        searchButton.setMargin(new Insets(0, 0, 0, 0));
        searchButton.setPreferredSize(new Dimension(30, 30));
        searchButton.setToolTipText("Google Image Search");
        searchButton.addActionListener(e -> {
        });
        topBar.add(searchButton, BorderLayout.EAST);

        //middle image stuff:


        //bottom bar:
        JPanel bottomBar = new JPanel(new BorderLayout());

        JButton trashButton = new JButton("→");
        trashButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        trashButton.setMargin(new Insets(0, 0, 0, 0));
        trashButton.setPreferredSize(new Dimension(30, 30));
        trashButton.setToolTipText("Remove");
        trashButton.addActionListener(e -> {
        });
        bottomBar.add(trashButton, BorderLayout.EAST);


        itemTile.add(topBar, BorderLayout.NORTH);
        itemTile.add(bottomBar, BorderLayout.SOUTH);


        return itemTile;
    }
}
