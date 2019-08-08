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
    private JFileChooser fc = new JFileChooser(userDir);
    
    IDView(){
        // Create the main panel
        windowContent= new JPanel();
        
        // Set a layout manager for this panel
	BorderLayout bl = new BorderLayout();
	windowContent.setLayout(bl);
        
        // Create the control panel
        controlPanel = new JPanel();
	FlowLayout fl =new FlowLayout(FlowLayout.LEADING);
	controlPanel.setLayout(fl);
        
        // Create and add controls to the panel
        JLabel urlLabel= new JLabel("Enter the URL:");
	controlPanel.add(urlLabel);
	urlField = new JTextField(25);
	controlPanel.add(urlField);
        
        JLabel l1= new JLabel("Min Width:");
	controlPanel.add(l1);	
	widthSNM = new SpinnerNumberModel(350, 1, 5000, 1);
	widthSpinner = new JSpinner(widthSNM);
	controlPanel.add(widthSpinner);
        
        JLabel l2= new JLabel("Min Height:");
	controlPanel.add(l2);	
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
        
        // Add the control panel to the main panel
        windowContent.add("North",controlPanel);
        
        // Create the status panel
       	statusPanel = new JPanel();
	GridLayout gl = new GridLayout(1,2);
	statusPanel.setLayout(gl);
        
        statusLabel = new JLabel();	
        statusPanel.add(statusLabel);
	progressBar = new JProgressBar();
	statusPanel.add(progressBar);
        
        // Add the status panel to the main panel
	windowContent.add("South",statusPanel);
        
        // Create the thumbnail panel
        thumbPanel = new JPanel();
	WrapLayout wl = new WrapLayout(WrapLayout.LEADING); // FlowLayout subclass that fully supports wrapping of components
	thumbPanel.setLayout(wl);
        
        // Create the scroll panel and add the thumbnail panel to it
        scrollPanel = new JScrollPane(thumbPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        // Add the scroll panel to the main panel
        windowContent.add("Center",scrollPanel);
        
        // Create the frame and add the main panel to it
        JFrame frame = new JFrame("Image Downloader");
        frame.add(windowContent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        // Set the minimum size and make the window visible
        frame.setMinimumSize(new Dimension(960, 540));        
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
        JToggleButton tb [];
        Component[] components = thumbPanel.getComponents();
        tb = new JToggleButton [components.length];
        for (int i=0; i<components.length; i++) {
            tb[i] = (JToggleButton) components[i];
        }
        return tb;
    }
    
    // Returns urls of all selected elements
    public ArrayList<String> getSelected () {
        JToggleButton tb [] = getComponents ();
        ArrayList <String> url = new ArrayList <> ();
        for (int i=0; i<tb.length; i++) {
            if(tb[i].isSelected()) {
                url.add (tb[i].getToolTipText());
            }
        }
        return url;
    }
    
    public void setAllSelected (boolean state) {
        JToggleButton tb [] = getComponents ();
        for (int i=0; i<tb.length; i++) {
            if(state)
                tb[i].setSelected(true);
            else tb[i].setSelected(false);
        }
    }
    
    // Pops up a file chooser for the user to choose save path
    public String getSavePath () {
        String savePath=null;
        
        // Instruction to display only directories
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        // Pops up a "Save File" file chooser dialog
        int returnVal = fc.showSaveDialog(windowContent);
        
        // Return value if approve (yes, ok) is chosen
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile(); // Returns the selected folder
            savePath=file.getAbsolutePath(); // Returns the absolute path
            fc.setCurrentDirectory(file);
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
