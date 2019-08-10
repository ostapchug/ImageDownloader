package imagedownloader;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;


public class IDView {
    
    private JPanel windowContent;
    private JPanel controlPanel;
    private JPanel thumbPanel;
    private JPanel statusPanel;
    private JScrollPane scrollPanel;
    private JTextField urlField;
    private JButton srchButton;
    private JButton dwnlButton;
    private JButton cancelButton;
    private SpinnerNumberModel widthSNM;
    private SpinnerNumberModel heightSNM;
    private JSpinner widthSpinner;
    private JSpinner heightSpinner;
    private JCheckBox checkButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    // We need to implement MVC here
    private IDEngine idEngine = new IDEngine(this);
    
    // Get the current working directory and set it as default open path
    private File userDir = new File(System.getProperty("user.dir"));
    private JFileChooser pathChooser = new JFileChooser(userDir);
    
    IDView(){
        // Create the main panel
        windowContent= new JPanel();
        
        // Set a layout manager for this panel
	BorderLayout windowLayout = new BorderLayout();
	windowContent.setLayout(windowLayout);
        
        // Create the control panel
        controlPanel = new JPanel();
	FlowLayout controlLayout =new FlowLayout(FlowLayout.LEADING);
	controlPanel.setLayout(controlLayout);
        
        // Create and add controls to the panel
        JLabel urlLabel= new JLabel("Enter the URL:");
	controlPanel.add(urlLabel);
	urlField = new JTextField(25);
	controlPanel.add(urlField);
        
        JLabel widthLabel= new JLabel("Min Width:");
	controlPanel.add(widthLabel);	
	widthSNM = new SpinnerNumberModel(350, 1, 5000, 1);
	widthSpinner = new JSpinner(widthSNM);
	controlPanel.add(widthSpinner);
        
        JLabel heightLabel= new JLabel("Min Height:");
	controlPanel.add(heightLabel);	
	heightSNM = new SpinnerNumberModel(500, 1, 5000, 1);
	heightSpinner = new JSpinner(heightSNM);
	controlPanel.add(heightSpinner);
        
        srchButton= new JButton("Search");
	srchButton.addActionListener(idEngine); // Register the class IDEngine as an event listener
	controlPanel.add(srchButton);
        
        dwnlButton= new JButton("Download");
	dwnlButton.addActionListener(idEngine);
	controlPanel.add(dwnlButton);
	dwnlButton.setEnabled(false);
        
        checkButton = new JCheckBox("Select all");
	checkButton.addActionListener(idEngine);
	checkButton.setEnabled(false);
	controlPanel.add(checkButton);
        
        cancelButton= new JButton("Cancel");
	cancelButton.addActionListener(idEngine);
	controlPanel.add(cancelButton);
	cancelButton.setEnabled(false);
        
        // Add the control panel to the main panel
        windowContent.add("North",controlPanel);
        
        // Create the status panel
       	statusPanel = new JPanel();
	GridLayout statusLayout = new GridLayout(1,2);
	statusPanel.setLayout(statusLayout);
        
        statusLabel = new JLabel();	
        statusPanel.add(statusLabel);
	progressBar = new JProgressBar();
	statusPanel.add(progressBar);
        
        // Add the status panel to the main panel
	windowContent.add("South",statusPanel);
        
        // Create the thumbnail panel
        thumbPanel = new JPanel();
	WrapLayout thumbLayout = new WrapLayout(WrapLayout.LEADING); // FlowLayout subclass that fully supports wrapping of components
	thumbPanel.setLayout(thumbLayout);
        
        // Create the scroll panel and add the thumbnail panel to it
        scrollPanel = new JScrollPane(thumbPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Add the scroll panel to the main panel
        windowContent.add("Center",scrollPanel);
        
        // Create the frame and add the main panel to it
        JFrame frame = new JFrame("Image Downloader");
        frame.add(windowContent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        // Set the minimum size and make the window visible
        frame.setMinimumSize(new Dimension(1024, 600));        
        frame.setVisible(true); 
    }
    
    // Returns the URL
    public String getURL () {
        return urlField.getText();	
    }
    
    // Returns the minimum width
    public int getMinWidth () {
        int minWidth=(Integer)widthSpinner.getValue();
        return minWidth;
    }
    
    // Returns the minimum height
    public int getMinHeight () {
        int minHeight=(Integer)heightSpinner.getValue();
	return minHeight;
    }
    
    // Inserts the status message
    public void setStatus(String str) {
        statusLabel.setText(str);
    }
    
    // Enables (or disables) the progress bar
    public void setProgress(boolean value) {
        progressBar.setIndeterminate(value);
    }
    
    // Enables (or disables) the search button
    public void setSrchButton(boolean val) {
        srchButton.setEnabled(val);
    }
    
    public void setDwnlButton(boolean val) {
        dwnlButton.setEnabled(val);
    }
    
    public void setCheckButton(boolean val0, boolean val1) {
        checkButton.setEnabled(val0);
        checkButton.setSelected(val1);
    }
       
    public void setCancelButton(boolean val) {
        cancelButton.setEnabled(val);
    }
    
    // Inserts the elements to the thumb panel
    public void addThumbnails (ImageIcon [] imageIcon) {
        JToggleButton thumbs [];
        thumbs = new JToggleButton[imageIcon.length];
        if (thumbs.length!=0)
            setCheckButton(true,false);
        for (int i=0; i<imageIcon.length; i++) {
            if(imageIcon[i]==null) 
                continue;
            thumbs[i]= new JToggleButton(imageIcon[i]);
            thumbs[i].setToolTipText(imageIcon[i].getDescription());
            thumbs[i].addItemListener(idEngine);
            thumbPanel.add(thumbs[i]);
        }
        thumbPanel.revalidate();
    }
    
    // Removes all of the elements from the thumb panel
    public void removeThumbnails() {
        thumbPanel.removeAll();
	thumbPanel.revalidate();
	thumbPanel.repaint();
    }
    
    // Returns all of the elements from the thumb panel
    public JToggleButton []  getComponents () {
        JToggleButton thumbs [];
        Component[] components = thumbPanel.getComponents();
        thumbs = new JToggleButton [components.length];
        for (int i=0; i<components.length; i++) {
            thumbs[i] = (JToggleButton) components[i];
        }
        return thumbs;
    }
    
    // Returns urls of all selected elements
    public ArrayList<String> getSelected () {
        JToggleButton thumbs [] = getComponents ();
        ArrayList <String> urls = new ArrayList <> ();
        for (int i=0; i<thumbs.length; i++) {
            if(thumbs[i].isSelected()) {
                urls.add (thumbs[i].getToolTipText());
            }
        }
        return urls;
    }
    
    public void setAllSelected (boolean state) {
        JToggleButton thumbs [] = getComponents ();
        for (int i=0; i<thumbs.length; i++) {
            if(state)
                thumbs[i].setSelected(true);
            else thumbs[i].setSelected(false);
        }
    }
    
    // Pops up a file chooser for the user to choose save path
    public String getSavePath () {
        String savePath=null;
        
        // Instruction to display only directories
        pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        // Pops up a "Save File" file chooser dialog
        int returnVal = pathChooser.showSaveDialog(windowContent);
        
        // Return value if approve (yes, ok) is chosen
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = pathChooser.getSelectedFile(); // Returns the selected folder
            savePath=file.getAbsolutePath(); // Returns the absolute path
            pathChooser.setCurrentDirectory(file);
        }
        return savePath;
    }
    
    // Loads the swing elements on the event dispatch thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new IDView();
            }
        });
    }
    
}
