import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

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
}