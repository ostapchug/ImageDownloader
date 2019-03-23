package imagedownloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.SwingWorker;

class DownloadWorker extends SwingWorker<Void, Void> {
    String savePath;
    ArrayList <String> sUrl;
    
    DownloadWorker(String savePath, ArrayList <String> sUrl){
        this.savePath=savePath;
	this.sUrl=sUrl;
    }
    
    private void saveImage(String str) {
        URL url=null;
        String fileName=null;
        String destName=null;		
        InputStream is=null;
        OutputStream os=null;
        
        try {
            url = new URL(str);
            fileName = url.getFile();
            destName = savePath + File.separator + fileName.substring(fileName.lastIndexOf("/")+1);
            System.out.println(destName);
            is = url.openStream();
            os = new FileOutputStream(destName);
            byte[] b = new byte[2048];
            int length;
            
            while ((length = is.read(b)) != -1) {
                os.write(b, 0, length);
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

    @Override
    protected Void doInBackground() throws Exception {
        for (String s: sUrl) {
            saveImage(s);
        }
        return null;
    }
    
}
