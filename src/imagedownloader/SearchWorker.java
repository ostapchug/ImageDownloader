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

class SearchWorker extends SwingWorker<ImageIcon[], Void>  {
    String displayFieldText;
    int imgWidth;
    int imgHeight;
    
    SearchWorker(String displayFieldText, int imgWidth, int imgHeight){
        this.displayFieldText=displayFieldText;
        this.imgWidth=imgWidth;
        this.imgHeight=imgHeight;
    }

    @Override
    protected ImageIcon[] doInBackground() throws Exception {
        ArrayList<String> str = searchImages (displayFieldText);
        setProgress(50);
        ImageIcon[] imageIcon = readImages (str);
        setProgress(100);
        return imageIcon;
    }
    
    @Override
    public void done() {
        try {
            get();
        } catch (Exception ignore) {
        }
    }

    private ArrayList<String> searchImages(String str) {
        ArrayList <String> url = new ArrayList <String> ();
        try{
            Document doc = Jsoup.connect(str).get();
            Elements images = doc.select("a");
            
            for (Element el: images) {
                String imageUrl = el.attr("abs:href");
                if(imageUrl.toLowerCase().endsWith(".jpg")||imageUrl.toLowerCase().endsWith(".png")) {
                    url.add(imageUrl);
                }
            }
            
            Elements images1 = doc.select("img");
            for (Element el1: images1) {
                String imageUrl1 = el1.attr("abs:src");
                url.add(imageUrl1);
            }  
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return url; 
    }

    private ImageIcon[] readImages(ArrayList<String> str) {
        URL url1 = null;
        BufferedImage image;
        ImageIcon imageIcon [] = new ImageIcon[str.size()];
        
        try{
            for (int i=0; i<imageIcon.length; i++) {
                url1 = new URL (str.get(i));
                System.out.println(str.get(i));
		image = ImageIO.read(url1);
                if(image != null) {
                    if(image.getWidth()>imgWidth&&image.getHeight()>imgHeight) {
                        imageIcon[i] = new ImageIcon(image.getScaledInstance(250, -250, Image.SCALE_DEFAULT));
                        imageIcon[i].setDescription(str.get(i));
                    }
                }else{
                    System.out.println("Can't read image!");
                }
            }
        }catch(IOException e) {
            System.out.println(e.getMessage());
        }
        return imageIcon;
    }
}
