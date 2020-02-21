import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PosterProjector3000 extends JFrame implements MouseListener, MouseMotionListener, KeyListener{
	
	private final String LOGO_TXT = "\n" + 
			"\n" + 
			" /$$$$$$$                       /$$                         /$$$$$$$                                               /$$                         /$$$$$$   /$$$$$$   /$$$$$$   /$$$$$$ \n" + 
			"| $$__  $$                     | $$                        | $$__  $$                                             | $$                        /$$__  $$ /$$$_  $$ /$$$_  $$ /$$$_  $$\n" + 
			"| $$  \\ $$ /$$$$$$   /$$$$$$$ /$$$$$$    /$$$$$$   /$$$$$$ | $$  \\ $$ /$$$$$$   /$$$$$$  /$$  /$$$$$$   /$$$$$$$ /$$$$$$    /$$$$$$   /$$$$$$|__/  \\ $$| $$$$\\ $$| $$$$\\ $$| $$$$\\ $$\n" + 
			"| $$$$$$$//$$__  $$ /$$_____/|_  $$_/   /$$__  $$ /$$__  $$| $$$$$$$//$$__  $$ /$$__  $$|__/ /$$__  $$ /$$_____/|_  $$_/   /$$__  $$ /$$__  $$  /$$$$$/| $$ $$ $$| $$ $$ $$| $$ $$ $$\n" + 
			"| $$____/| $$  \\ $$|  $$$$$$   | $$    | $$$$$$$$| $$  \\__/| $$____/| $$  \\__/| $$  \\ $$ /$$| $$$$$$$$| $$        | $$    | $$  \\ $$| $$  \\__/ |___  $$| $$\\ $$$$| $$\\ $$$$| $$\\ $$$$\n" + 
			"| $$     | $$  | $$ \\____  $$  | $$ /$$| $$_____/| $$      | $$     | $$      | $$  | $$| $$| $$_____/| $$        | $$ /$$| $$  | $$| $$      /$$  \\ $$| $$ \\ $$$| $$ \\ $$$| $$ \\ $$$\n" + 
			"| $$     |  $$$$$$/ /$$$$$$$/  |  $$$$/|  $$$$$$$| $$      | $$     | $$      |  $$$$$$/| $$|  $$$$$$$|  $$$$$$$  |  $$$$/|  $$$$$$/| $$     |  $$$$$$/|  $$$$$$/|  $$$$$$/|  $$$$$$/\n" + 
			"|__/      \\______/ |_______/    \\___/   \\_______/|__/      |__/     |__/       \\______/ | $$ \\_______/ \\_______/   \\___/   \\______/ |__/      \\______/  \\______/  \\______/  \\______/ \n" + 
			"                                                                                   /$$  | $$                                                                                         \n" + 
			"                                                                                  |  $$$$$$/                                                                                         \n" + 
			"                                                                                   \\______/                                                                                          \n" + 
			"\n" + 
			"";
	
	private final String HELP_TXT = 
			"----------Help----------\n"+
			"CLICK        :    Trace Poster\n"+
			"CTRL+CLICK   :    Delete Poster\n"+
			"BACKSPACE    :    Undo\n"+
			"SPACE        :    Toggle Mode (Note : Most functions do not work in transparent mode)\n"+
			"CTRL+S       :    Save Current as Default\n"+
			"CTRL+D       :    Load Default (Destructive)\n"+
			"D            :    Load Default (Non-Destructive)\n"+
			"L            :    Toggle Logo\n"+
			"C            :    Toggle Cursor\n"+
			"H            :    Toggle Help\n"+
			"------------------------";
	
	private Timer tmr;
	private ArrayList<Poster> posters;
	private Point mouseLoc;
	private RotoscopePanel roto;	
	private int mode = 0;
	
	private boolean ctrl = false;
	private boolean logo = false;
	private boolean help = false;
	private boolean hideCursor = false;
	
	public final String DEFAULT_LOCATION = "default.pst";
	public int nonWhiteTransparency = 123;
	
	public PosterProjector3000() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(true);
		this.setBackground(new Color(1f,1f,1f,0f));
	    this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);		
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
				
		//Remove the default cursor.
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		this.getContentPane().setCursor(blankCursor);
		
		mouseLoc = new Point(0,0);
		
		posters = new ArrayList<Poster>();
		
		roto = new RotoscopePanel();
		
		tmr = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				render();
				roto.repaint();
			}
		});
				
		this.add(roto);
		this.setVisible(true);	
		
		tmr.start();
	}
	
	private class RotoscopePanel extends JPanel {
		public BufferedImage frame;
		
		public RotoscopePanel() {
			this.setBackground(new Color(0,0,0,0));
		}
		public void paint(Graphics g) {
			if(frame != null) {
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				g.drawImage(frame, 0, 0, null);
			}
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	
	private void render() {
		BufferedImage frame = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = frame.createGraphics();
		
		RenderingHints rh = new RenderingHints( RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	    g.setRenderingHints(rh);
	    
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		g.setColor(Color.WHITE);
		
		//Render Help Message
		if(help) {
	    	String[] lines = HELP_TXT.split("\n");
	    	int currX = 100;
	    	int currY = 100;
    		g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
	    	for(int i = 0; i < lines.length; i++) {
	    		g.drawString(lines[i], currX, currY);
	    		currY += g.getFontMetrics().getHeight();
	    	}
		}
		//Render Logo
		if(logo) {
			String[] lines = LOGO_TXT.split("\n");
    		g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 10));
    		int maxW = 0;
    		for(int i = 0; i < lines.length; i++) {
    			int lineW = g.getFontMetrics().charsWidth(lines[i].toCharArray(), 0, lines[i].length());
    			if(lineW > maxW) {
    				maxW = lineW;
    			}
    		}
	    	int currX = (int)((this.getWidth() / 2.0) - (maxW / 2.0));
	    	int currY = (int)((this.getHeight() / 2.0) - ((g.getFontMetrics().getHeight() * lines.length)/2.0));
	    	
	    	for(int i = 0; i < lines.length; i++) {
	    		g.drawString(lines[i], currX, currY);
	    		currY += g.getFontMetrics().getHeight();
	    	}
		}
		
		//Render Posters
		for(int i = 0; i < posters.size(); i++) {
			//Fill Complete posters
			if(posters.get(i).isComplete()) {
				g.fill(posters.get(i).getPolygon()); 
			}else {
				g.setStroke(new BasicStroke(2));
				Point[] pts = posters.get(i).getArray();
				switch(posters.get(i).getSize()) {
					case 0: break; //Don't draw anything
					
					case 1: 
					case 2: 
						for(int j = 0; j < pts.length - 1; j++) {
							g.drawLine(pts[j].x, pts[j].y, pts[j+1].x, pts[j+1].y);
						}
						g.drawLine(pts[pts.length - 1].x, pts[pts.length -1].y, mouseLoc.x, mouseLoc.y);
					break; //Connect the dots and connect last to mouseloc.
					
					case 3: 
						for(int j = 0; j < pts.length - 1; j++) {
							g.drawLine(pts[j].x, pts[j].y, pts[j+1].x, pts[j+1].y);
						}
						g.drawLine(pts[pts.length - 1].x, pts[pts.length -1].y, mouseLoc.x, mouseLoc.y);
						g.drawLine(pts[0].x, pts[0].y, mouseLoc.x, mouseLoc.y);
					break; //Connect the dots and connect last and first to mouseloc
				}
			}
		}
		

		if(!hideCursor) {
			int targetSize = 20;
			if(ctrl) {
				g.setColor(Color.RED);
				g.setStroke(new BasicStroke(2));
				g.drawLine(mouseLoc.x+targetSize, mouseLoc.y, mouseLoc.x-targetSize, mouseLoc.y);
				g.drawLine(mouseLoc.x, mouseLoc.y + targetSize, mouseLoc.x, mouseLoc.y - targetSize);

				g.drawOval(mouseLoc.x-targetSize, mouseLoc.y - targetSize, 2 * targetSize, 2 * targetSize);
				g.setColor(Color.WHITE);
				g.fillOval(mouseLoc.x-2, mouseLoc.y-2, 5, 5);
				g.setColor(Color.RED);
				g.drawOval(mouseLoc.x-2, mouseLoc.y-2, 5, 5);
			}else {
				g.setColor(Color.GREEN);
				g.setStroke(new BasicStroke(2));
				g.drawLine(mouseLoc.x+targetSize, mouseLoc.y, mouseLoc.x-targetSize, mouseLoc.y);
				g.drawLine(mouseLoc.x, mouseLoc.y + targetSize, mouseLoc.x, mouseLoc.y - targetSize);
			
				g.drawOval(mouseLoc.x-targetSize, mouseLoc.y - targetSize, 2 * targetSize, 2 * targetSize);
				g.setColor(Color.WHITE);
				g.fillOval(mouseLoc.x-2, mouseLoc.y-2, 5, 5);
				g.setColor(Color.GREEN);
				g.drawOval(mouseLoc.x-2, mouseLoc.y-2, 5, 5);
			}
			
			

		}
		roto.frame = frame;
	}
	
	//Converts all white / grey pixels in last rendered frame to transparent. White - fully transparent,Grey - semi-transparent,  Black - Opaque
	public BufferedImage getTransparentImage() {
		BufferedImage frame = roto.frame;
		BufferedImage out = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		
		for(int x = 0; x < this.getWidth(); x++) {
			for(int  y = 0; y < this.getHeight(); y++) {
				if(frame.getRGB(x, y) == Color.BLACK.getRGB()) {
					out.setRGB(x, y, new Color(0,0,0,255).getRGB());
				}else {
					Color c = new Color(frame.getRGB(x, y));
					int greyscale = (int)((c.getRed() + c.getGreen() + c.getBlue()) / 3.0);
					if(c.getRed() == c.getGreen() && c.getRed()== c.getBlue()) {
						out.setRGB(x, y, new Color(0,0,0,255-greyscale).getRGB());
					}else {
						out.setRGB(x, y, new Color(c.getRed(),c.getGreen(),c.getBlue(),nonWhiteTransparency).getRGB());
					}
				}
			}
		}
		return out;
	}

	public void saveAsDefault() {
		String data = "";
		
		for(int i = 0; i < posters.size(); i++) {
			Point[] pts = posters.get(i).getArray();
			for(Point p : pts) {
				data += "["+p.x+","+p.y+"] ";
			}
			data += "\n";
		}
		
		try(FileWriter fileWriter = new FileWriter(DEFAULT_LOCATION)) {
		    String fileContent = data;
		    fileWriter.write(fileContent);
		} catch (IOException e) {
		    System.err.println("ERROR: Could not write file:"+DEFAULT_LOCATION);
		}
		
	}
	
	public void loadDefault() {
		String data = "";
		try(FileReader fileReader = new FileReader(DEFAULT_LOCATION)) {
		    int ch = fileReader.read();
		    while(ch != -1) {
		        data += ((char)ch);
		        ch = fileReader.read();
		    }
		} catch (FileNotFoundException e) {
		    System.err.print("ERROR: No default preset found");
		} catch (IOException e) {
		    System.err.print("ERROR: Could not read file:"+DEFAULT_LOCATION);

		}
		
		String[] points = data.split(" ");
		for(String p : points) {
			//System.out.println(s);
			if(p.contains("[") && p.contains("]")) {
				int x = Integer.parseInt(p.substring(p.indexOf("[")+1,p.indexOf(",")));
				int y = Integer.parseInt(p.substring(p.indexOf(",")+1,p.indexOf("]")));
				
				Point nP = new Point(x,y);
				this.addPoint(nP);
			}
		}
	}
	
	public void addPoint(Point p) {
		if(posters.size() == 0 || posters.get(posters.size() - 1).isComplete()) {
			posters.add(new Poster(p));
		}else {
			posters.get(posters.size() - 1).add(p);
		}
	}
	
	public void removePoint() {
		if(posters.size() != 0) {
			posters.get(posters.size() - 1).remove();
			if(posters.get(posters.size() - 1).getSize() == 0){
				posters.remove(posters.size() - 1);
			}
		}

	}
	
	public void cycleMode() {
		if(mode == 0) {
			//Turn off help message before switching to transparent.
			help = false;
			boolean tmpC = hideCursor;
			hideCursor = true;
			render();
			hideCursor = tmpC;
			tmr.stop();
			repaint();
			roto.frame = getTransparentImage();
			repaint();
			mode = 1;
		}else {
			tmr.start();
			mode = 0;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
			ctrl = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.out.println("Goodbye!");
			System.exit(0);
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			cycleMode();
		}
		
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if(mode == 0) {
				removePoint();
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_S) {
			if(mode == 0) {
			saveAsDefault();
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_D) {
			if(mode == 0) {
				if(ctrl) {
					posters = new ArrayList<Poster>();
				}
				loadDefault();
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_H) {
			if(mode == 0) {
				if(help) {
					help = false;
				}else {
					help = true;
				}
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_L) {
			if(mode == 0) {
				if(logo) {
					logo = false;
				}else {
					logo = true;
				}
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_C) {
			if(mode == 0) {
				if(hideCursor) {
					hideCursor = false;
				}else {
					hideCursor = true;
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
			ctrl = false;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseLoc = e.getPoint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mouseLoc = e.getPoint();

		if(mode == 0) {
			if(ctrl) {
				Rectangle r = new Rectangle(e.getX(), e.getY(), 2,2);
				for(int i = posters.size() - 1; i >= 0; i--) {
					if(posters.get(i).getPolygon().intersects(r)) {
						posters.remove(i);
						break;
					}
				}
			
			}else{
				addPoint(e.getPoint());
			}
			
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}