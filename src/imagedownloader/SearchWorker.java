package imagedownloader;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/*
SwingWorker is a generic class, with two type parameters. 
The first type parameter specifies a return type for doInBackground, 
and also for the get method, which is invoked by other threads to retrieve 
the object returned by doInBackground. SwingWorker's second type parameter 
specifies a type for interim results returned while the background task is 
still active. Since this class doesn't return interim results, 
Void is used as a placeholder.
*/

class SearchWorker extends SwingWorker<ImageIcon[], Void>  {
    String url;
    int minWidth;
    int minHeight;
    
    SearchWorker(String url, int minWidth, int minHeight){
        this.url=url;
        this.minWidth=minWidth;
        this.minHeight=minHeight;
    }
    
    
    // Executes in a background thread
    @Override
    protected ImageIcon[] doInBackground() throws Exception {
    
        return readImages(searchImages(url),minWidth, minHeight);
    }
    
    // Fetches the page from a URL, and extracts images
    private ArrayList<String> searchImages(String url) throws IOException {
        ArrayList <String> urls = new ArrayList <> ();
        
        // The connect() method creates a new connection, and get() fetches and parses a HTML file
        Document doc = Jsoup.connect(url).get();
        
        // Returns a list of matching elements 
        Elements links = doc.select("a[href]");
        Elements imports = doc.select("link[href]");
        Elements images = doc.select("img[src]");
        
        // Extract attributes from elements
        // To get an absolute URL, there is a attribute key prefix abs:
        // that will cause the attribute value to be resolved against the document's base URI 
        for (Element el: links) {
            String imageUrl = el.attr("abs:href");
            if(imageUrl.toLowerCase().endsWith(".jpg")||imageUrl.toLowerCase().endsWith(".png")) {
                urls.add(imageUrl);
            }
        }
        
        for (Element el: imports) {
            String imageUrl = el.attr("abs:href");
            if(imageUrl.toLowerCase().endsWith(".jpg")||imageUrl.toLowerCase().endsWith(".png")) {
                urls.add(imageUrl);
            }
        }
        
        for (Element el: images) {
            String imageUrl = el.attr("abs:src");
            urls.add(imageUrl);
        }
        
        return urls; 
    }
    
    // Loads the images into an ImageIcon array, and returns a reference to it
    private ImageIcon[] readImages(ArrayList<String> urls, int w, int h) throws IOException {
        URL url = null;
        BufferedImage image;
        ImageIcon imageIcon [] = new ImageIcon[urls.size()];
        
        for (int i=0; i<imageIcon.length; i++) {
            
            // Creates a URL object from the String representation
            url = new URL (urls.get(i));
            
            // Source image to scale                       
            image = ImageIO.read(url);
            
            // Create an ImageIcon if the image is exist
            if(image != null) {
                
                // Create an ImageIcon if the image parameters is valid
                if(image.getWidth()>=w && image.getHeight()>=h) {
                    
                    // Resizes an image then creates an ImageIcon
                    imageIcon[i] = new ImageIcon(image.getScaledInstance(250, -250, Image.SCALE_DEFAULT));
                    
                    // Sets image url as description for further processing
                    imageIcon[i].setDescription(urls.get(i));
                }
            }else{
                System.out.println("Can't read image!");
            }
        }    
        return imageIcon;
    }
}
