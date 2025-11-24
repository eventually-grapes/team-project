import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

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
        JPanel itemTile = new JPanel();
        JButton uploadButton = new JButton();
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            int result = fileChooser.showOpenDialog(null);
            if (result != 1) { //file was actually selected
                setImage(fileChooser.getSelectedFile().getAbsolutePath());
            }
            }
        });
        itemTile.add(uploadButton);
        return itemTile;
    }
}
