import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Item{
    public String[] values;

    public Item(String itemName){

        this.values = new String[2];
        this.values[0] = itemName;        
    }
    public static void addImage(){
        // takes image and adds it to the array at pos 1
    
    }
    public Image getImage(String directory) {
        File source = new File(directory);
        try {
            return ImageIO.read(source);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}