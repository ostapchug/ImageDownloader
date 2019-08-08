package imagedownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.SwingWorker;

/*
SwingWorker is a generic class, with two type parameters. 
The first type parameter specifies a return type for doInBackground, 
and also for the get method, which is invoked by other threads to retrieve 
the object returned by doInBackground. SwingWorker's second type parameter 
specifies a type for interim results returned while the background task is 
still active. Since this class doesn't return any results, 
Void is used as a placeholder.
*/

class DownloadWorker extends SwingWorker<Void, Void> {
    String savePath;
    ArrayList <String> urls;
    
    DownloadWorker(String savePath, ArrayList <String> urls){
        this.savePath=savePath;
	this.urls=urls;
    }
    
    private void saveImages(String str) {
        URL url=null;
        String fileName=null;
        String destName=null;		
        InputStream is=null;
        OutputStream os=null;
        
        try {
            // Creates a URL object from the String representation
            url = new URL(str);
            fileName = url.getFile(); // Gets the file name of this URL
            destName = savePath + File.separator + fileName.substring(fileName.lastIndexOf("/")+1);
            
            // System.out.println(destName);
            
            // Opens a connection to this URL and returns an InputStream for reading from that connection
            is = url.openStream();
            
            // Creates a file output stream to write to the file with the specified name
            os = new FileOutputStream(destName);
            
            byte[] b = new byte[2048];
            int len;
            
            // Reads 2048 bytes from the input stream and stores them into the buffer array b
            while ((len = is.read(b)) != -1) {
                
                // Writes len bytes from the b byte array starting at offset 0 to this file output stream
                os.write(b, 0, len); 
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }finally {
            try {
                is.close();
		os.close();
            }catch (IOException e) {
                System.out.println(e.getMessage());
            } 
        }
    }
    
    // Executes in a background thread
    @Override
    protected Void doInBackground() {
        for (String s: urls) {
            saveImages(s);
        }
        return null;
    }
    
}
