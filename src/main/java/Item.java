import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
        final int size = 200;

        JPanel itemTile = new JPanel(new BorderLayout());
        itemTile.setPreferredSize(new Dimension(size, size));
        itemTile.setMaximumSize(new Dimension(size, size));
        itemTile.setMinimumSize(new Dimension(size, size));
        itemTile.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        //top bar:
        JPanel topBar = new JPanel(new BorderLayout());

        JButton uploadButton = new JButton("↑");
        uploadButton.setFont(new Font("Arial", Font.BOLD, 14));
        uploadButton.setMargin(new Insets(0, 0, 0, 0));
        uploadButton.setPreferredSize(new Dimension(30, 30));
        uploadButton.setToolTipText("Image Upload");
        uploadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir")); //open file explorer at current directory
            int result = fileChooser.showOpenDialog(null);
            if (result != 1) { //file was actually selected
                setImage(fileChooser.getSelectedFile().getAbsolutePath());
                itemTile.revalidate();
                itemTile.repaint();
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
        JPanel imagePanel = new JPanel() {
            private Image loaded = null;
            private Image scaled = null;
            private String lastPath = null;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth(), h = getHeight();
                if (imageDir != null && !imageDir.equals(lastPath)) {
                    lastPath = imageDir;
                    try {
                        Image tmp = getImage();
                        loaded = tmp;
                        scaled = null;
                    } catch (Exception ex) {
                        loaded = null;
                        scaled = null;
                    }
                }

                if (loaded != null) {
                    if (scaled == null || scaled.getWidth(this) != w || scaled.getHeight(this) != h) {
                        BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = buf.createGraphics();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.drawImage(loaded, 0, 0, w, h, this);
                        g2.dispose();
                        scaled = buf;
                    }
                    g.drawImage(scaled, 0, 0, w, h, this);
                }
                else { //no image
                    g.setColor(new Color(50, 50, 50));
                    g.fillRect(0, 0, w, h);
                    g.setColor(new Color(150, 150, 150));
                    g.setFont(getFont().deriveFont(Font.ITALIC, 12f));
                    FontMetrics fm = g.getFontMetrics();
                    String msg = "No Image";
                    int tw = fm.stringWidth(msg);
                    g.drawString(msg, (w - tw) / 2, h / 2);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(size, size-60);
            }
        };
        //imagePanel.setOpaque(false);

        //bottom bar:
        JPanel bottomBar = new JPanel(new BorderLayout());

        JButton trashButton = new JButton("→");
        trashButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        trashButton.setMargin(new Insets(0, 0, 0, 0));
        trashButton.setPreferredSize(new Dimension(30, 30));
        trashButton.setToolTipText("Remove");
        trashButton.addActionListener(e -> {
            Container parent = itemTile.getParent();
            if (parent != null) {
                parent.remove(itemTile);
                parent.revalidate();
                parent.repaint();
            }
        });
        bottomBar.add(trashButton, BorderLayout.EAST);

        //everything together:
        itemTile.add(topBar, BorderLayout.NORTH);
        itemTile.add(imagePanel, BorderLayout.CENTER);
        itemTile.add(bottomBar, BorderLayout.SOUTH);

        itemTile.revalidate();
        itemTile.repaint();

        return itemTile;
    }
}
