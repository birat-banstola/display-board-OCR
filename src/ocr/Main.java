package ocr;
import java.io.*;
import java.util.concurrent.ExecutionException;
import org.apache.poi.xwpf.usermodel.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import net.sourceforge.tess4j.*;
import java.sql.*;

interface SetTheLocation
{
	void setLocationForComponent(JFrame wrtFrame);
}

class LoadingWindow implements SetTheLocation
{
	JFrame frame;
	JDialog jd;
	String message;
	JLabel label;
	public LoadingWindow(JFrame cw, String message)
	{
		
		this.message=message;
		frame = new JFrame("Test");
		jd = new JDialog(frame);
	    ImageIcon loading = new ImageIcon(System.getProperty("user.dir")+"\\images\\ajax-loader.gif");
	    jd.add((label=new JLabel(this.message, loading, JLabel.CENTER)));
		jd.setUndecorated(true);
		//frame.setLocationRelativeTo(null);
		setLocationForComponent(cw);
		jd.getRootPane().setBorder(BorderFactory.createMatteBorder(1,1,1,1, Color.BLACK));
	    jd.setSize(130,50);
	    jd.setAlwaysOnTop (true);
	    jd.setVisible(true);
	}
	
	public void changeLabelState(String message)
	{
			this.message=message;
			label.setText(message);
			frame.revalidate();
	}
	
	public void disposeLoadingWindow()
	{
		frame.dispose();
	}
	
	public void setLocationForComponent(JFrame wrtFrame)
	{
		int xcord=wrtFrame.getX();
		int ycord=wrtFrame.getY();
		int width=wrtFrame.getWidth();
		int height = wrtFrame.getHeight();
		jd.setLocation(xcord+(int)(width/2.5),ycord+height/2);
		
	}
}

class DisplayContent extends JFrame
{
	String res;
	public DisplayContent(String res)
	{
		this.res=res;
		JTextArea area = new JTextArea();
		area.setText(res);
		//area.setPreferredSize(new Dimension(400,400));
		JScrollPane scrollPane = new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				scrollPane.setPreferredSize(new Dimension(800,200));
		JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
		scrollBar.setValue( scrollBar.getMaximum() );
		add(scrollPane);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
		setVisible(true);
	}
}

class DisplayMessageDialogBox {
	// For any dialogbox
	JFrame f;

	public DisplayMessageDialogBox(String str) {
		f = new JFrame();
		JOptionPane.showMessageDialog(f, str);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class DialogToRead
{
	String documentName;
	JTextField tfield;
	JFrame frame;
	public DialogToRead()
	{
		frame = new JFrame();
		JDialog jd = new JDialog(frame,"Dialog Example",true);
		jd.setLayout(new BorderLayout());
		JPanel panelForLabelAndText = new JPanel();
		JLabel label= new JLabel("Enter document name: ");
		tfield= new JTextField(15);
		panelForLabelAndText.add(label);
		panelForLabelAndText.add(tfield);
		jd.add(panelForLabelAndText,BorderLayout.NORTH);
		JPanel panelForButton = new JPanel();
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				documentName=tfield.getText();
				jd.setVisible(false);
			}
		});
		jd.setLocationRelativeTo(null);
		panelForButton.add(ok);
		jd.add(panelForButton,BorderLayout.CENTER);
		jd.pack();
		jd.setVisible(true);
	}
	
}

class FirstWindow extends JFrame implements ActionListener
{
	public FirstWindow()
	{
		super("OCR");
		setPreferredSize(new Dimension(600,250));
		setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
		JLabel welcomeLabel=new JLabel("Eagle-Eye");
		welcomeLabel.setFont(new Font("Algerian", Font.BOLD, 40));
		welcomeLabel.setBorder(BorderFactory.createCompoundBorder(
			    welcomeLabel.getBorder(), 
			    BorderFactory.createEmptyBorder(0,0,0,0)));
		welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		add(welcomeLabel);
		
		JLabel tagLine=new JLabel("An accurate,agile OCR agent");
		tagLine.setFont(new Font("Times New Roman", Font.BOLD, 11));
		tagLine.setBorder(BorderFactory.createCompoundBorder(
			    tagLine.getBorder(), 
			    BorderFactory.createEmptyBorder(0,20,30,30)));
		tagLine.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(tagLine);
		JLabel actionLabel = new JLabel("What do you wanna do?");
		actionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		actionLabel.setBorder(BorderFactory.createCompoundBorder(
			    welcomeLabel.getBorder(), 
			    BorderFactory.createEmptyBorder(7,7,7,7)));
		add(actionLabel);
		JPanel panel1 = new JPanel();
		JButton action1= new JButton("Test the product");
		action1.addActionListener(this);
		panel1.add(action1);
		JPanel panel2 = new JPanel();
		JButton action2= new JButton("Use the product");
		action2.addActionListener(this);
		panel2.add(action2);
		add(panel1);
		add(panel2);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand()=="Test the product")
		{
			new TestWindow(this);
		}
		
		else if(ae.getActionCommand()=="Use the product")
		{
			new ChooseWindow(this);
		}
	}
	
}

class TestWindow extends JFrame implements ActionListener,ComponentListener,SetTheLocation
{
	JPanel panelForImage,panelForTryMoreButton,panelForResult,panelForError;
	JButton chooseButton,tryButton,finish;
	JLabel labelForImage,labelForResult,labelForError,labelForIcon;
	JTextField field,fieldForError;
	LoadingWindow lw=null;
	String result;
	DoingOcr doingOcr;
	FirstWindow fw;
	
	public TestWindow(FirstWindow fw)
	{
		super("Test OCR");
		this.fw=fw;
		//setPreferredSize(new Dimension(600,350));
		setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
		this.getRootPane().setBorder(BorderFactory.createCompoundBorder(
			    this.getRootPane().getBorder(), 
			    BorderFactory.createEmptyBorder(30,30,30,30)));
		JLabel welcomeLabel=new JLabel("Test OCR",SwingConstants.CENTER);
		welcomeLabel.setFont(new Font("Algerian", Font.BOLD, 40));
		welcomeLabel.setBorder(BorderFactory.createCompoundBorder(
			    welcomeLabel.getBorder(), 
			    BorderFactory.createEmptyBorder(7,7,7,7)));
		welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(welcomeLabel);
		
		panelForImage = new JPanel();
		panelForImage.setLayout(new FlowLayout(FlowLayout.LEFT));
		labelForImage = new JLabel("Choose an image:  ");
		chooseButton = new JButton("Choose");
		chooseButton.addActionListener(this);
		chooseButton.setSize(new Dimension(65,25));
		panelForImage.add(labelForImage);
		panelForImage.add(chooseButton);
		add(panelForImage);
		panelForImage.setBorder(BorderFactory.createCompoundBorder(
			    panelForImage.getBorder(), 
			    BorderFactory.createEmptyBorder(7,7,7,7)));
		
		panelForResult = new JPanel();
		panelForResult.setLayout(new FlowLayout(FlowLayout.LEFT));
		labelForResult = new JLabel("Result:                 ");
		field = new JTextField(20);
		panelForResult.add(labelForResult);
		panelForResult.add(field);
		add(panelForResult);
		panelForResult.setBorder(BorderFactory.createCompoundBorder(
				panelForResult.getBorder(), 
			    BorderFactory.createEmptyBorder(7,7,7,7)));
		
		panelForError = new JPanel();
		panelForError.setLayout(new FlowLayout(FlowLayout.LEFT));
		labelForError = new JLabel("OCR Accuracy:  ");
		fieldForError = new JTextField(20);
		panelForError.add(labelForError);
		panelForError.add(fieldForError);
		add(panelForError);
		panelForError.setBorder(BorderFactory.createCompoundBorder(
				panelForError.getBorder(), 
			    BorderFactory.createEmptyBorder(7,7,7,7)));
		
		setLocationForComponent(fw);
		pack();
		
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setPreferredSize(new Dimension(375,375));
		pack();
		addComponentListener(this);
		setVisible(true);
		
	}
	
	public void setLocationForComponent(JFrame wrtFrame)
	{
		int x = wrtFrame.getX();
		int y= wrtFrame.getY();
		int width= wrtFrame.getWidth();
		int height= wrtFrame.getHeight();
		
		setLocation(x+width/5,y);
	}

    public void componentMoved(ComponentEvent e) {
    	if(lw!=null)
        {
    		lw.setLocationForComponent(this);
        }
    }
    
    public void componentHidden(ComponentEvent e) {
    	if(lw!=null)
        {
    		lw.setLocationForComponent(this);
        }
    }

    public void componentResized(ComponentEvent e) {
    	if(lw!=null)
        {
    		lw.setLocationForComponent(this);
        }      
    }

    public void componentShown(ComponentEvent e) {
    	if(lw!=null)
        {
    		lw.setLocationForComponent(this);
        }

    }
    
    
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getActionCommand()=="Finish")
		{
			this.dispose();
		}
		
		else if(ae.getActionCommand()=="Choose")
		{
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir")+"\\images");
			chooser.showOpenDialog(null);
			File file=chooser.getSelectedFile();
			
			
			
			if(file!=null)
			{
				doingOcr = new DoingOcr();
				SwingWorker<JFrame,Void> sw1 = new SwingWorker<JFrame,Void>() {
					String filename;
					Connection con;
					Statement st;
					ResultSet rs;
					String actualResult;
					
				    protected JFrame doInBackground() throws Exception {
				    	
						try
						{
							lw= new LoadingWindow(TestWindow.this,"wait a moment...");
							filename=file.getName();
							Class.forName("org.postgresql.Driver");
							con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres","postgres","Kaviswacha");
							st=con.createStatement();
							rs=st.executeQuery("SELECT * from \"ImageResults\"");
							
							while(rs.next())
							{
								if(rs.getString(1).equals(filename))
								{
									actualResult = rs.getString(2);
									break;
								}
								

							}
							
							con.close();
						}
						catch (SQLException se)
						{
							se.printStackTrace();

						}
						
						catch(ClassNotFoundException cnfe)
						{
							cnfe.printStackTrace();
						}
						
				    	
		    			
		    			ImageIcon icon = new ImageIcon(file.getPath());
						Image image = icon.getImage(); // transform it 
						Image newimg = image.getScaledInstance(180, 120,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
						icon = new ImageIcon(newimg);  // transform it back
						panelForImage.remove(chooseButton);
						labelForIcon = new JLabel(icon);
						
						panelForImage.add(labelForIcon);
						
						//labelForIcon.setSize(new Dimension(100,100));
						
						
						try{
						TestWindow.this.result=doingOcr.giveOcrResult(file);
						field.setText(TestWindow.this.result);
						}
						catch(TesseractException te)
						{
							te.getMessage();
						}
						
						fieldForError.setText(accuracyCheck(actualResult,TestWindow.this.result));
						
						panelForTryMoreButton = new JPanel();
						tryButton = new JButton("Try Again");
						tryButton.addActionListener(TestWindow.this);
						finish = new JButton("Finish");
						finish.addActionListener(TestWindow.this);
						panelForTryMoreButton.add(tryButton);
						panelForTryMoreButton.add(finish);
						TestWindow.this.add(panelForTryMoreButton);
						labelForImage.setText("Image:                 ");
				    	
				  return TestWindow.this;
				    }
				      
				      @Override
				      protected void done() {
				    	  
				    	  	try{
				    	  		JFrame frame=get();
				    	  	
				    	  	
				  	    	Thread t = new Thread(){
				  	    		public void run(){
				  	    			try{
				  	    				lw.changeLabelState("Done");
				  	    				Thread.sleep(2000);
				  	    			}
				  	    			catch(InterruptedException ie)
				  	    			{
				  	    				ie.printStackTrace();
				  	    			}
				  	    			
				  	    	lw.disposeLoadingWindow();
				  	    	frame.pack();
				  	    	frame.repaint();
				  	    	frame.revalidate();
				  	    		}
				  	    	};
				  	    	t.start();
				  	    	
				  	    	
				    	  
				    	  
				      }
				    	  	catch(InterruptedException ie)
				    	  	{
				    	  		ie.printStackTrace();
				    	  	}
				    	  	catch(ExecutionException ee)
				    	  	{
				    	  		ee.printStackTrace();
				    	  	}
				      }
				  };
				  sw1.execute();
				  
				//lw= new LoadingWindow(this,"Loading");
				
				
				return;
			}
			else
			{
				return;
			}
		}
		else if(ae.getActionCommand()=="Try Again");
		{
			
			panelForImage.remove(labelForIcon);
			chooseButton = new JButton("Choose");
			chooseButton.addActionListener(this);
			panelForImage.add(chooseButton);
			panelForImage.revalidate();
			panelForImage.repaint();
			remove(panelForTryMoreButton);
			field.setText("");
			fieldForError.setText("");
			this.pack();
			this.repaint();this.revalidate();
			return;
			
		}
		
	}
	
	public static String accuracyCheck(String actualResult,String ocrResult)
	{
		ocrResult=ocrResult.trim();
		int d = editDist(ocrResult, actualResult, ocrResult.length(),
			actualResult.length());
		int accuracy = (int)((double)((1-((double)d/actualResult.length())))*100);
		return String.valueOf(accuracy)+"%";
	}
	
	public static int min(int x,int y,int z)
    {
        if (x <= y && x <= z) return x;
        if (y <= x && y <= z) return y;
        else return z;
    }
	
	public static int editDist(String str1, String str2, int m, int n)
    {
        // Create a table to store results of subproblems
        int dp[][] = new int[m+1][n+1];
      
        // Fill d[][] in bottom up manner
        for (int i=0; i<=m; i++)
        {
            for (int j=0; j<=n; j++)
            {
                // If first string is empty, only option is to
                // isnert all characters of second string
                if (i==0)
                    dp[i][j] = j;  // Min. operations = j
      
                // If second string is empty, only option is to
                // remove all characters of second string
                else if (j==0)
                    dp[i][j] = i; // Min. operations = i
      
                // If last characters are same, ignore last char
                // and recur for remaining string
                else if (str1.charAt(i-1) == str2.charAt(j-1))
                    dp[i][j] = dp[i-1][j-1];
      
                // If last character are different, consider all
                // possibilities and find minimum
                else
                    dp[i][j] = 1 + min(dp[i][j-1],  // Insert
                                       dp[i-1][j],  // Remove
                                       dp[i-1][j-1]); // Replace
            }
        }
  
        return dp[m][n];
    }
}

class ChooseWindow extends JFrame implements ActionListener, ComponentListener, SetTheLocation
{
	UI ui;
	FirstWindow fw;
	public ChooseWindow(FirstWindow fw)
	{
		super("OCR");
		this.fw=fw;
		setPreferredSize(new Dimension(600,350));
		setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
		//JPanel panelForMenu = new JPanel();
		//panelForMenu.setLayout(new FlowLayout(FlowLayout.LEFT));
		JMenuBar mb=new JMenuBar();  
        JMenu menu=new JMenu("File");
        JMenu menu2=new JMenu("Help");
        JMenuItem item1 = new JMenuItem("Open docs");
        JMenuItem item2 = new JMenuItem("About");
        item2.addActionListener(this);
        item1.addActionListener(this);
        menu.add(item1);
        menu2.add(item2);
        mb.add(menu);
        mb.add(menu2);
        setJMenuBar(mb);
       
		JLabel welcomeLabel=new JLabel("Eagle-Eye");
		welcomeLabel.setFont(new Font("Algerian", Font.BOLD, 40));
		welcomeLabel.setBorder(BorderFactory.createCompoundBorder(
			    welcomeLabel.getBorder(), 
			    BorderFactory.createEmptyBorder(0,0,0,0)));
		welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		add(welcomeLabel);
		
		JLabel tagLine=new JLabel("An accurate,agile OCR agent");
		tagLine.setFont(new Font("Times New Roman", Font.BOLD, 11));
		tagLine.setBorder(BorderFactory.createCompoundBorder(
			    tagLine.getBorder(), 
			    BorderFactory.createEmptyBorder(0,20,30,30)));
		tagLine.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(tagLine);
		
		JLabel label = new JLabel("Choose file/s to read" );
		label.setBorder(BorderFactory.createCompoundBorder(
			    label.getBorder(), 
			    BorderFactory.createEmptyBorder(7,7,7,7)));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel panelForButton = new JPanel();
		JButton open = new JButton("Open");
		open.setPreferredSize(new Dimension(65,25));
		open.addActionListener(this);
		panelForButton.add(open);
		add(label);
		add(panelForButton);
		JPanel panelForLabelDirections = new JPanel();
		JLabel labelForDirections = new JLabel("<html><body style= \"font-family:Comic Sans MS;\">*Directions<br>1)Select image/s to read<br>"
				+ "2)Choose what you want to do with image content<br>"
				+ "3)Check out the reults<br>"
				+ "4)For saved documents, select \"Open docs\" from \"File\"</body></html>");
		labelForDirections.setAlignmentX(Component.CENTER_ALIGNMENT);
		labelForDirections.setFont(new Font("Arial", Font.PLAIN, 14));
		panelForLabelDirections.add(labelForDirections);
		add(panelForLabelDirections);
		pack();
		setLocationForComponent(fw);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addComponentListener(this);
		setVisible(true);
	
	}
	
	public void setLocationForComponent(JFrame wrtFrame)
	{
		int x = wrtFrame.getX();
		int y= wrtFrame.getY();
		int width= wrtFrame.getWidth();
		int height= wrtFrame.getHeight();
		setLocation(x+width/5,y);
	}
	
	public void componentMoved(ComponentEvent e) {
		if(ui!=null && ui.lw!=null){
		
    	
        	updateLoadingFramePosition(ui.lw);
        	return;
		}
		return;
        
    }
    
    public void componentHidden(ComponentEvent e) {
    	if(ui!=null && ui.lw!=null)
        {
        	updateLoadingFramePosition(ui.lw);
        	return;
        }
    	return;
    }

    public void componentResized(ComponentEvent e) {
    	if(ui!=null && ui.lw!=null)
        {
        	updateLoadingFramePosition(ui.lw);
        	return ;
        }      
    	return;
    }

    public void componentShown(ComponentEvent e) {
    	if(ui!=null && ui.lw!=null)
        {
        	updateLoadingFramePosition(ui.lw);
        	return;
        }
    	return;

    }
    
    public void updateLoadingFramePosition(LoadingWindow lw)
    {
    	
    	lw.setLocationForComponent(this);
    }
    
    
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand()=="Open")
		{
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir")+"\\images");
			chooser.setMultiSelectionEnabled(true);
			chooser.showOpenDialog(null);
			File files[]=chooser.getSelectedFiles();
			if(files.length!=0)
			ui = new UI(files,this);
		}
		else if(e.getActionCommand()=="Open docs")
		{
			JFileChooser chooser = new JFileChooser(System.getProperty("user.dir")+"\\results");//"C:\\Users\\ChanakyaS\\workspace\\ocr\\results");
			chooser.showOpenDialog(null);
			File file = chooser.getSelectedFile();
			try{
				if(file!=null)
				Desktop.getDesktop().open(file);
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
			}
			
			
		}
		else if(e.getActionCommand()=="About")
		{
			
		}
	}
}


class UI implements ActionListener
{
	JFrame mainFrame;
	JLabel welcomeLabel;
	JPanel panelForButtons;
	JLabel labelForChoosing;
	JButton choose;
	File files[];
	UI ui;
	volatile String documentName;
	String res;
	LoadingWindow lw=null;
	ChooseWindow cw;
	
	public UI(File files[], ChooseWindow cw)
	{
		this.files=files;
		this.cw=cw;
		res= new String("");
		mainFrame = new JFrame("Pick an option");
		mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane(),BoxLayout.Y_AXIS));
		mainFrame.setPreferredSize(new Dimension(500,250));
		JPanel forLabel =new JPanel();
		welcomeLabel=new JLabel("OCR",SwingConstants.CENTER);
		welcomeLabel.setFont(new Font("Algerian", Font.BOLD, 25));
		welcomeLabel.setBorder(BorderFactory.createCompoundBorder(
			    welcomeLabel.getBorder(), 
			    BorderFactory.createEmptyBorder(7,7,7,7)));
		welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		forLabel.add(welcomeLabel);
		mainFrame.add(forLabel);
		panelForButtons =new JPanel();
		panelForButtons.setLayout(new BoxLayout(panelForButtons,BoxLayout.Y_AXIS));
		panelForButtons.setBorder(BorderFactory.createCompoundBorder(
			    panelForButtons.getBorder(), 
			    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		
		labelForChoosing = new JLabel("What do you want to do with image content?",SwingConstants.CENTER);
		labelForChoosing.setFont(new Font("Arial", Font.PLAIN, 15));
		labelForChoosing.setBorder(BorderFactory.createCompoundBorder(
			    labelForChoosing.getBorder(), 
			    BorderFactory.createEmptyBorder(7,7,7,7)));
		labelForChoosing.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelForButtons.add(labelForChoosing);
		JPanel panel = new JPanel();
		
		panel.setPreferredSize(new Dimension(50,50));
		panel.setBorder(BorderFactory.createCompoundBorder(
			    panel.getBorder(), 
			    BorderFactory.createEmptyBorder(0,0,0,0)));
		
		JButton disp = new JButton("Display");
		disp.setSize(new Dimension(50,50));
		disp.addActionListener(this);
		panel.add(disp);
		panelForButtons.add(panel);
		
		JPanel panel2 = new JPanel();
		panel2.setPreferredSize(new Dimension(50,50));
		JButton copyToDoc = new JButton("Save as word doc");
		copyToDoc.setSize(new Dimension(50,50));
		copyToDoc.addActionListener(this);
		panel2.add(copyToDoc);
		panelForButtons.add(panel2);
		mainFrame.add(panelForButtons);
		mainFrame.pack();
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}
	
	public void actionPerformed(ActionEvent e)
	{
		DoingOcr doingOcr = new DoingOcr();
		
		if(e.getActionCommand()=="Save as word doc")
		{
		DialogToRead obj = new DialogToRead();
		if(obj.documentName==null )
			return;
			
			if(obj.documentName.length()==0)
				return;
			
		documentName = obj.documentName;
		
		//obj.frame.dispose();
		
		SwingWorker<XWPFDocument,Void> sw1 = new SwingWorker<XWPFDocument,Void>() {
		    
		    protected XWPFDocument doInBackground() throws Exception {
		    	XWPFDocument document = new XWPFDocument(); 
    			lw= new LoadingWindow(cw,"wait a moment...");
    			
    			try{
    		    document=doingOcr.giveOcrResult(files, document);
    			}
    			catch(TesseractException te)
    			{
    				te.getMessage();
    			}
		    	
		  return document;
		    }
		      
		      @Override
		      protected void done() {
		    	  try{
		    		  XWPFDocument document=get();
		    		//Write the Document in file system
		    			
		  	    	try{
		  	    	  
		  	    		FileOutputStream out = new FileOutputStream( new File
		  	    		  (System.getProperty("user.dir")+"\\results\\"+documentName+".docx"));
		  		    document.write(out);
		  		    document.close();
		  		   
		  	    		}
		  	    	catch(Exception exception)
		  	    	{
		  		   exception.printStackTrace();
		  	    	}
		  	    	Thread t = new Thread(){
		  	    		public void run(){
		  	    			try{
		  	    				lw.changeLabelState("Done");
		  	    				Thread.sleep(2000);
		  	    			}
		  	    			catch(InterruptedException ie)
		  	    			{
		  	    				ie.printStackTrace();
		  	    			}
		  	    	lw.disposeLoadingWindow();
		  	    	String str="<html><body>Document saved to reaults</body></html>";
		  	    	DisplayMessageDialogBox db = new DisplayMessageDialogBox(str);
		  	    		}
		  	    	};
		  	    	t.start();
		    		  
		    	  }
		    	  catch(InterruptedException ie)
		    	  {
		    		  ie.printStackTrace();
		    	  }
		    	  catch(ExecutionException ee)
		    	  {
		    		  ee.printStackTrace();
		    	  }
		      }
		  };
		  sw1.execute();
		  
		mainFrame.dispose();
		
	    
		}
	    
		else if(e.getActionCommand()=="Display")
		{
			
			    SwingWorker<String,Void> sw1 = new SwingWorker<String,Void>() {
			  
			    protected String doInBackground() throws Exception {
			        // Start
			    	lw= new LoadingWindow(cw,"Wait a moment...");
			    	try{
			    		res=doingOcr.giveOcrResult(files);
			    	}
			    	catch(TesseractException te)
			    	{
			    		te.getMessage();
			    	}
			    	return res;
			        
			      }
			      
			      @Override
			      protected void done() {
			    	 
			    		  Thread t = new Thread(){
				  	    		public void run(){
				  	    			try{
				  	    				lw.changeLabelState("Done");
				  	    			Thread.sleep(2000);
				  	    			}
				  	    			catch(InterruptedException ie)
				  	    			{
				  	    				ie.printStackTrace();
				  	    			}
				  	    	lw.disposeLoadingWindow();
				  	    	 try{
				  	    	new DisplayContent(get());
				  	    		}
				  	    	catch(InterruptedException  ie)
					    	  {
					    		  ie.printStackTrace();
					    	  }
					    	  catch(ExecutionException  ee)
					    	  {
					    		  ee.printStackTrace();
					    	  }
				  	    	}
				  	    	};
				  	    	t.start();
			    	  
			    	  }
			    	
			    };
			    sw1.execute();
			    mainFrame.dispose();
		}
		
		
	}
		
}

public class Main {
	
	public static void main(String args[])throws IOException
	{
		new FirstWindow();
	}
	

}

