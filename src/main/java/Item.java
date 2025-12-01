import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
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
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
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
        final int size = Main.TIER_HEIGHT;

        JPanel itemTile = new JPanel(new BorderLayout());
        itemTile.setPreferredSize(new Dimension(size, size));
        itemTile.setMaximumSize(new Dimension(size, size));
        itemTile.setMinimumSize(new Dimension(size, size));
        itemTile.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        //top bar:
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JButton uploadButton = new JButton("↑");
        uploadButton.setFont(new Font("Arial", Font.BOLD, 10));
        uploadButton.setMargin(new Insets(0, 0, 0, 0));
        uploadButton.setPreferredSize(new Dimension(16, 16));
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

        JLabel nameLabel = new JLabel(this.name, SwingConstants.CENTER);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        topBar.add(nameLabel, BorderLayout.CENTER);

        JButton searchButton = new JButton("⌕");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 10));
        searchButton.setMargin(new Insets(0, 0, 0, 0));
        searchButton.setPreferredSize(new Dimension(16, 16));
        searchButton.setToolTipText("Google Image Search");
        searchButton.addActionListener(e -> {
            String query = (String) JOptionPane.showInputDialog(itemTile, "Search images for:", "Google Image Search", JOptionPane.PLAIN_MESSAGE, null, null, this.name);
            if (query == null) return;
            query = query.trim();
            if (query.isEmpty()) return;

            final String searchQuery = query;

            final JDialog waiting = new JDialog((Frame) SwingUtilities.getWindowAncestor(itemTile), "Searching...", true);
            waiting.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            waiting.getContentPane().add(new JLabel("Searching Google Images...", SwingConstants.CENTER), BorderLayout.CENTER);
            waiting.setSize(380, 80);
            waiting.setLocationRelativeTo(itemTile);

            SwingWorker<List<Image>, Void> worker = new SwingWorker<List<Image>, Void>() {
                @Override
                protected List<Image> doInBackground() throws Exception {
                    GoogleAPI api = new GoogleAPI();
                    return api.getImages(searchQuery);
                }

                @Override
                protected void done() {
                    try {
                        waiting.dispose();

                        List<Image> images = get();
                        if (images == null || images.isEmpty()) {
                            JOptionPane.showMessageDialog(itemTile, "No images found", "No Results", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }

                        JDialog chooser = new JDialog((Frame) SwingUtilities.getWindowAncestor(itemTile), "Select an image", true);
                        JPanel grid = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
                        JScrollPane sp = new JScrollPane(grid);
                        sp.setPreferredSize(new Dimension(520, 320));
                        chooser.getContentPane().add(sp, BorderLayout.CENTER);

                        java.util.function.Function<Image, BufferedImage> toBuffered = (Image src) -> {
                            if (src == null) return null;
                            if (src instanceof BufferedImage) return (BufferedImage) src;
                            int w = src.getWidth(null);
                            int h = src.getHeight(null);
                            if (w <= 0) w = 1;
                            if (h <= 0) h = 1;
                            BufferedImage b = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2 = b.createGraphics();
                            g2.drawImage(src, 0, 0, null);
                            g2.dispose();
                            return b;
                        };

                        for (int i = 0; i < images.size(); i++) {
                            final Image orig = images.get(i);
                            if (orig == null) continue;
                            int thumbW = 140, thumbH = 140;
                            int iw = orig.getWidth(null);
                            int ih = orig.getHeight(null);
                            if (iw <= 0 || ih <= 0) { iw = thumbW; ih = thumbH; }
                            double scale = Math.min((double)thumbW/iw, (double)thumbH/ih);
                            int nw = (int)Math.max(1, Math.round(iw * scale));
                            int nh = (int)Math.max(1, Math.round(ih * scale));
                            Image thumb = orig.getScaledInstance(nw, nh, Image.SCALE_SMOOTH);
                            ImageIcon icon = new ImageIcon(thumb);

                            JButton b = new JButton(icon);
                            b.setPreferredSize(new Dimension(thumbW, thumbH));
                            b.setToolTipText("Click to select this image");
                            b.addActionListener(ev -> {
                                try {
                                    BufferedImage buf = toBuffered.apply(orig);
                                    if (buf == null) {
                                        JOptionPane.showMessageDialog(chooser, "Failed to convert image", "Error", JOptionPane.ERROR_MESSAGE);
                                        return;
                                    }

                                    File imagesDir = new File(System.getProperty("user.dir"), "images");
                                    if (!imagesDir.exists()) imagesDir.mkdirs();

                                    String safeName = name.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
                                    File out = new File(imagesDir, safeName + "_" + System.currentTimeMillis() + ".png");
                                    ImageIO.write(buf, "png", out);

                                    setImage(out.getAbsolutePath());

                                    itemTile.revalidate();
                                    itemTile.repaint();
                                    chooser.dispose();
                                } catch (IOException ioex) {
                                    ioex.printStackTrace();
                                    JOptionPane.showMessageDialog(chooser, "Failed to save image: " + ioex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                                }
                            });
                            grid.add(b);
                        }

                        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        JButton cancel = new JButton("Cancel");
                        cancel.addActionListener(ae -> chooser.dispose());
                        bottom.add(cancel);
                        chooser.getContentPane().add(bottom, BorderLayout.SOUTH);

                        chooser.pack();
                        chooser.setLocationRelativeTo(itemTile);
                        chooser.setVisible(true);
                    } catch (java.util.concurrent.ExecutionException ee) {
                        Throwable cause = ee.getCause();
                        String msg = (cause != null && cause.getMessage() != null) ? cause.getMessage() : ee.getMessage();
                        JOptionPane.showMessageDialog(itemTile, "Image search failed: " + msg, "Search Error", JOptionPane.ERROR_MESSAGE);
                        waiting.dispose();
                    } catch (InterruptedException ie) {
                        waiting.dispose();
                        Thread.currentThread().interrupt();
                        JOptionPane.showMessageDialog(itemTile, "Image search interrupted.", "Search Interrupted", JOptionPane.ERROR_MESSAGE);                                    
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(itemTile, "Image search failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
            waiting.setVisible(true);
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


        //everything together:
        itemTile.add(topBar, BorderLayout.NORTH);
        itemTile.add(imagePanel, BorderLayout.CENTER);

        itemTile.revalidate();
        itemTile.repaint();

        return itemTile;
    }
}
