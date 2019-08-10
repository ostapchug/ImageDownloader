package imagedownloader;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.JCheckBox;

class IDEngine implements ActionListener, ItemListener, PropertyChangeListener {
    
    IDView parent; // a reference to the IDView
    SearchWorker searchWorker;
    DownloadWorker downloadWorker;
	
    // Constructor stores the reference to the 
    // IDView window in the member variable parent
    IDEngine(IDView parent){        
        this.parent=parent;
    }
    
    private void searchImages(){
        parent.removeThumbnails();
	parent.setDwnlButton(false);
	parent.setCheckButton(false,false);
        
        String url=parent.getURL();
	int minWidth=parent.getMinWidth();
	int minHeight=parent.getMinHeight();
        
        // Background task for searching images.
        searchWorker = new SearchWorker (url, minWidth, minHeight);
        searchWorker.addPropertyChangeListener(this);      
        searchWorker.execute(); // Start searching the images in the background
    }
    
    private void downloadImages(){
        String savePath=parent.getSavePath();
        ArrayList <String> urls = parent.getSelected();
        
        // Background task for downloading images.
        downloadWorker = new DownloadWorker (savePath, urls);
        downloadWorker.addPropertyChangeListener(this);
        downloadWorker.execute(); // Start downloading the images in the background
    }

    // This method gets called when a state property is changed
    @Override
    public void propertyChange(PropertyChangeEvent e) {
                   
            // Get the source object of this action
            if (e.getSource().equals(searchWorker)){
                
                // Returns true if this task completed
                if (!searchWorker.isDone()) {
                    parent.setSrchButton(false);
                    parent.setCancelButton(true);
                    parent.setStatus(" Searching...");
                    parent.setProgress(true);
                }else{
                    parent.setSrchButton(true);
                    parent.setCancelButton(false);
                    parent.setStatus(" Done");
                    parent.setProgress(false);
                    try {
                        parent.addThumbnails(searchWorker.get()); // Returns all images we have got
                    }catch(InterruptedException | ExecutionException ex) {
                        System.out.println(ex.getMessage());
                    }catch (CancellationException ex){
                        System.out.println(ex.getMessage());
                        parent.setStatus(" Canceled");
                    }
                }
            }else{
                if (!downloadWorker.isDone()) {
                    parent.setSrchButton(false);
                    parent.setDwnlButton(false);
                    parent.setCancelButton(true);
                    parent.setStatus(" Downloading...");
                    parent.setProgress(true);
                }else{
                    parent.setSrchButton(true);
                    parent.setDwnlButton(true);
                    parent.setCancelButton(false);
                    parent.setStatus(" Done");
                    parent.setProgress(false);
                }
            }
    }
    
    // Invoked when a button has been pressed
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Get the source object of this action
        switch (e.getActionCommand()) {
            case "Search":
                searchImages();
                break;
            case "Download":
                downloadImages();
                break;
            case "Select all":
                JCheckBox checkButton = (JCheckBox)e.getSource();
                if (checkButton.isSelected())
                    parent.setAllSelected(true);
                else parent.setAllSelected(false);
                break;
            case "Cancel":
                if(searchWorker!=null)
                    searchWorker.cancel(true);
                if(downloadWorker!=null)
                    downloadWorker.cancel(true);
                break;
            default:
                break;
        }
    }
    
    // Invoked when a thumb has been selected or deselected
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (!parent.getSelected().isEmpty())
            parent.setDwnlButton(true);
        else parent.setDwnlButton(false);
        if (parent.getSelected().size()< parent.getComponents().length) 
            parent.setCheckButton(true, false);
        else parent.setCheckButton(true, true);
        
    }
    
}
