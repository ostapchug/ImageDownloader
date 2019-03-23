package imagedownloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

class IDEngine implements ActionListener, ItemListener {
    
    IDView parent;
    SearchWorker searchWorker;
    DownloadWorker downloadWorker;
	
    IDEngine(IDView parent){        
        this.parent=parent;
    }
    
    private void searchImages(){
        parent.removeThumbnails();
	parent.setDwnlButton(false);
	parent.setButtons(false,false);
	String displayFieldText=parent.getDisplayFieldText();
	int imgWidth=parent.getMinWidth ();
	int imgHeight=parent.getMinHeight ();
        searchWorker = new SearchWorker (displayFieldText, imgWidth, imgHeight);
        searchWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                String name = e.getPropertyName();
                if ("progress".equalsIgnoreCase(name)) {
                    //int value = (int) arg0.getNewValue();
                    //parent.setProgress(value); 
                }else if("state".equalsIgnoreCase(name)){
                    if (!searchWorker.isDone()) {
                        parent.setSrchButton(false);
                        parent.setStatus(" Searching...");
                        parent.setProgress(true);
                    }else{
                        try {
                            parent.setSrchButton(true);
                            parent.setStatus(" Done!");
                            parent.setProgress(false);
                            parent.addThumbnails((ImageIcon[]) searchWorker.get());
                        }catch(InterruptedException arg0) {
                            System.out.println(arg0.getMessage());
                        }catch(ExecutionException arg0) {
                            System.out.println(arg0.getMessage());
                        }
                    } 
                }
            }
        });
        searchWorker.execute();
    }
    
    private void downloadImages(){
        String savePath=parent.getSavePath();
        ArrayList <String> sUrl = parent.getSelected();
        DownloadWorker downloadWorker = new DownloadWorker (savePath, sUrl);
        downloadWorker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
                public void propertyChange(PropertyChangeEvent e) {
                    String name = e.getPropertyName();
                    if ("progress".equalsIgnoreCase(name)) {
                        
                    }else if ("state".equalsIgnoreCase(name)) {
                        if (!downloadWorker.isDone()) {
                            parent.setSrchButton(false);
                            parent.setDwnlButton(false);
                            parent.setStatus(" Downloading...");
                            parent.setProgress(true);
                        }else{
                            parent.setSrchButton(true);
                            parent.setDwnlButton(true);
                            parent.setStatus(" Done!");
                            parent.setProgress(false);
                        }
                    } 
                }
        });
        downloadWorker.execute();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Search" == e.getActionCommand()){
            searchImages();
        } else if("Download" == e.getActionCommand()){
            downloadImages();
        } else if ("Select all" == e.getActionCommand()) {
            JCheckBox checkButton = (JCheckBox)e.getSource();
            if (checkButton.isSelected())
                parent.setAllSelected (true);
            else parent.setAllSelected (false);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (!parent.getSelected().isEmpty())
            parent.setDwnlButton(true);
        else parent.setDwnlButton(false);
        if (parent.getSelected().size()< parent.getComponents().length) 
            parent.setButtons(true, false);
        else parent.setButtons(true, true);
        
    }
    
}
