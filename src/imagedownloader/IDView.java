package imagedownloader;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;


public class IDView {
    private JPanel windowContent;
    private JPanel thumbPanel;
    private JTextField displayField;
    private JButton srchButton;
    private JButton dwnlButton;
    private SpinnerNumberModel imgWidth;
    private SpinnerNumberModel imgHeight;
    private JSpinner spinner0;
    private JSpinner spinner1;
    private JCheckBox checkButton;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    
    IDEngine idEngine = new IDEngine(this);
    File userDir = new File(System.getProperty("user.dir"));
    JFileChooser fc = new JFileChooser(userDir);
    
    IDView(){
        windowContent= new JPanel();
	BorderLayout bl = new BorderLayout();
	windowContent.setLayout(bl);
        
        JPanel jp0 = new JPanel();
	FlowLayout fl =new FlowLayout(FlowLayout.LEADING);
	jp0.setLayout(fl);
        
        JLabel l0= new JLabel("Enter the URL:");
	jp0.add(l0);
	displayField = new JTextField(25);
	jp0.add(displayField);
        
        JLabel l1= new JLabel("Min Width:");
	jp0.add(l1);	
	imgWidth = new SpinnerNumberModel(350, 1, 5000, 1);
	spinner0 = new JSpinner(imgWidth);
	jp0.add(spinner0);
        
        JLabel l2= new JLabel("Min Height:");
	jp0.add(l2);	
	imgHeight = new SpinnerNumberModel(500, 1, 5000, 1);
	spinner1 = new JSpinner(imgHeight);
	jp0.add(spinner1);
        
        srchButton= new JButton("Search");
	srchButton.addActionListener(idEngine);
	jp0.add(srchButton);
        
        dwnlButton= new JButton("Download");
	dwnlButton.addActionListener(idEngine);
	jp0.add(dwnlButton);
	dwnlButton.setEnabled(false);
        
        checkButton = new JCheckBox("Select all");
	checkButton.addActionListener(idEngine);
	checkButton.setEnabled(false);
	jp0.add(checkButton);
        
        windowContent.add("North",jp0);
        
        statusLabel = new JLabel();	
	progressBar = new JProgressBar();
	JPanel jp1 = new JPanel();
	GridLayout gl = new GridLayout(1,2);
	jp1.setLayout(gl);
	jp1.add(statusLabel);
	jp1.add(progressBar);
	windowContent.add("South",jp1);
        
        thumbPanel = new JPanel();
	WrapLayout wl = new WrapLayout(WrapLayout.LEADING);
	thumbPanel.setLayout(wl);
        
        JScrollPane jsp = new JScrollPane(thumbPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        windowContent.add("Center",jsp);
        
        JFrame frame = new JFrame("Image Downloader");
        frame.setMinimumSize(new Dimension(960, 540));
        frame.add(windowContent);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
    public String getDisplayFieldText () {
        return displayField.getText();	
    }
    
    public int getMinWidth () {
        int imgWidth=(Integer)spinner0.getValue();
        return imgWidth;
    }
    
    public int getMinHeight () {
        int imgHeight=(Integer)spinner1.getValue();
	return imgHeight;
    }
    
    public void setStatus(String str) {
        statusLabel.setText(str);
    }
    
    public void setProgress(boolean value) {
        progressBar.setIndeterminate(value);
    }
    
    public void setSrchButton(boolean val) {
        srchButton.setEnabled(val);
    }
    
    public void setDwnlButton(boolean val) {
        dwnlButton.setEnabled(val);
    }
    
    public void setButtons(boolean val0, boolean val1) {
        checkButton.setEnabled(val0);
        checkButton.setSelected(val1);
    }
    
    public void addThumbnails (ImageIcon [] imageIcon) {
        JToggleButton tb [];
        tb = new JToggleButton[imageIcon.length];
        if (tb.length!=0)
            setButtons(true,false);
        for (int i=0; i<imageIcon.length; i++) {
            if(imageIcon[i]==null) 
                continue;
            tb[i]= new JToggleButton(imageIcon[i]);
            tb[i].setToolTipText(imageIcon[i].getDescription());
            tb[i].addItemListener(idEngine);
            thumbPanel.add(tb[i]);
        }
        thumbPanel.revalidate();
    }
    
    public void removeThumbnails() {
        thumbPanel.removeAll();
	thumbPanel.revalidate();
	thumbPanel.repaint();
    }
    
    public JToggleButton []  getComponents () {
        JToggleButton tb [];
        Component[] components = thumbPanel.getComponents();
        tb = new JToggleButton [components.length];
        for (int i=0; i<components.length; i++) {
            tb[i] = (JToggleButton) components[i];
        }
        return tb;
    }
    
    public ArrayList<String> getSelected () {
        JToggleButton tb [] = getComponents ();
        ArrayList <String> url = new ArrayList <String> ();
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
    
    public String getSavePath () {
        String savePath=null;
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(windowContent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            
            //This is where a real application would open the file.
            savePath=file.getAbsolutePath();
            fc.setCurrentDirectory(file);
        }
        return savePath;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new IDView();
            }
        });
    }
    
}
