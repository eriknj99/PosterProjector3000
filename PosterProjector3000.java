import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
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
import javax.swing.Timer;

public class PosterProjector3000 extends JFrame implements KeyListener, MouseListener, MouseMotionListener{

	private Timer buffer;
	private Timer render;

	private Point mousePoint;
	private ArrayList<BufferedImage> frameBuffer;
	private ArrayList<Poster> posters;
	
	private int transparent;
	private boolean showHelp;
	private final String HELP_MESSAGE = "#Poster Projector 3000%\n\nMOUSE LEFT : Trace Poster\nSPACE : Cycle Transparent Mode\nBACKSPACE : Undo\nESC : Quit\nS : Save as default\nD : Load default\nF : Delete all\nH : Toggle help";
	private Font font;
	private final int MAX_BUFFER_SIZE = 3;
	private final boolean ENABLE_AA = true;
	
	public final String DEFAULT_SAVE_LOCATION = "default.pst";
	
	public PosterProjector3000() {
		this.setBounds(0,0,1920,1080);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setUndecorated(true);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		this.setBackground(new Color(0,0,0,0));
		this.setVisible(true);
		
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		transparent = 0;
		showHelp = false;
		mousePoint = new Point(0,0);
		frameBuffer = new ArrayList<BufferedImage>();
		posters = new ArrayList<Poster>();
		
		buffer = new Timer(0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				buffer();
			}
			
		});
		
		render = new Timer(0, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
			
		});
		
		buffer.start();
		render.start();
	}
	
	public void paint(Graphics g) {
		if(transparent == 0) {
			if(frameBuffer.size() > 0) {
				g.drawImage(frameBuffer.get(0), 0,0,null);
				frameBuffer.remove(0);
			}
		}else if(transparent == 1){
			this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			this.setBackground(new Color(0,0,0,0));
			super.paint(g);
			g.drawImage(createTransparentImage(), 0,0,null);
		}else {
			this.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			this.setBackground(new Color(0,0,0,0));
			super.paint(g);
			g.drawImage(createInverseTransparentImage(), 0,0,null);
		}
		
	}
	
	private BufferedImage createInverseTransparentImage() {
		BufferedImage out = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);

		frameBuffer = new ArrayList<BufferedImage>();
		buffer();
		
		for(int x = 0; x < frameBuffer.get(0).getWidth();x++) {
			for(int y = 0; y < frameBuffer.get(0).getHeight();y++) {
				if(frameBuffer.get(0).getRGB(x, y) == Color.WHITE.getRGB()) {
					out.setRGB(x, y, Color.WHITE.getRGB());
				}else {
					Color c1 = new Color(frameBuffer.get(0).getRGB(x, y));
					int greyscale = (c1.getRed() + c1.getGreen() + c1.getBlue()) / 3;
					Color c2 = new Color(255,255,255, greyscale);
					out.setRGB(x, y, c2.getRGB());
				}
			}
		}
		
		return out;
	}
	
	private BufferedImage createTransparentImage() {
		BufferedImage out = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		
		frameBuffer = new ArrayList<BufferedImage>();
		buffer();
		
		for(int x = 0; x < frameBuffer.get(0).getWidth();x++) {
			for(int y = 0; y < frameBuffer.get(0).getHeight();y++) {
				if(frameBuffer.get(0).getRGB(x, y) == Color.BLACK.getRGB()) {
					out.setRGB(x, y, Color.BLACK.getRGB());
				}else {
					Color c1 = new Color(frameBuffer.get(0).getRGB(x, y));
					int greyscale = (c1.getRed() + c1.getGreen() + c1.getBlue()) / 3;
					Color c2 = new Color(0,0,0, 255-greyscale);
					out.setRGB(x, y, c2.getRGB());
				}
			}
		}
		
		return out;
	}
	
	private void buffer() {
		BufferedImage frame = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		Graphics2D g = frame.createGraphics();
		
		if(ENABLE_AA) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		
		if(showHelp) {
			g.setFont(new Font(Font.MONOSPACED, Font.PLAIN ,12));
			int startX = 100;
			Point currPoint = new Point(startX,100);
			
			for(char a : HELP_MESSAGE.toCharArray()) {
				
				if(a == '\n') {
					currPoint.y += g.getFontMetrics().getHeight();
					currPoint.x = startX;
				}else if(a == '#'){
					g.setFont(new Font(Font.MONOSPACED, Font.PLAIN ,24));
				}else if(a == '%'){
					g.setFont(new Font(Font.MONOSPACED, Font.PLAIN ,12));
				}else {
					g.drawString(a + "", currPoint.x, currPoint.y);
					currPoint.x += g.getFontMetrics().charWidth(a);
				}
			}
		}
		
		for(Poster p : posters) {
			if(p.isComplete()) {
				g.fill(p.getPolygon());
			}else{
				Point[] tmp = p.getArray();
				
				switch(tmp.length) {
					case 1: g.drawLine(tmp[0].x, tmp[0].y, mousePoint.x, mousePoint.y); break;
					case 2: g.drawLine(tmp[0].x, tmp[0].y, tmp[1].x, tmp[1].y); g.drawLine(tmp[1].x, tmp[1].y, mousePoint.x, mousePoint.y);break;
					case 3: g.drawLine(tmp[0].x, tmp[0].y, tmp[1].x, tmp[1].y); g.drawLine(tmp[1].x, tmp[1].y, tmp[2].x, tmp[2].y);  g.drawLine(tmp[2].x, tmp[2].y, mousePoint.x, mousePoint.y); g.drawLine(tmp[0].x, tmp[0].y, mousePoint.x, mousePoint.y);break;
				}
			}
		}
		
		
		if(frameBuffer.size() > MAX_BUFFER_SIZE) {
			frameBuffer = new ArrayList<BufferedImage>();
		}
		
		frameBuffer.add(frame);
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		addPoint(e.getPoint());
	}
	
	private void addPoint(Point e) {
		if(posters.size() == 0) {
			posters.add(new Poster(e));
			return;
		}
		if(!posters.get(posters.size() - 1).isComplete()) {
			posters.get(posters.size() - 1).add(e);
			return;
		}
		
		posters.add(new Poster(e));
	}
	
	private void saveAsDefault() {
		if(transparent == 0) {
			
			String data = "";
			
			for(Poster p: posters) {
				if(p.isComplete()) {
					Point[] tmp = p.getArray();
					for(Point point:tmp) {
						data += "["+point.x+","+point.y+"]";
					}
					data+="\n";
				}
			}
			
			try(FileWriter fileWriter = new FileWriter(DEFAULT_SAVE_LOCATION)) {  
			    String fileContent = data;
			    fileWriter.write(fileContent);
			} catch (IOException e) {
			    // exception handling
			}
		}
	}
	
	private void loadDefault() {
		if(transparent == 0) {
			String in = "";
			
			try(FileReader fileReader = new FileReader(DEFAULT_SAVE_LOCATION)) {  
			    int ch = fileReader.read();
			    while(ch != -1) {
			        in += "" + (char)ch;
			        ch = fileReader.read();
			    }
			} catch (FileNotFoundException e) {
				System.out.println("ERROR\tFile: " + DEFAULT_SAVE_LOCATION + " not found.");
				return;
			} catch (IOException e) {
			   return;
			}
			
			System.out.println(in);
			
			posters = new ArrayList<Poster>();
			
			String[] lines = in.split("\n");
			
			for(String line : lines) {
				while(line.contains("[")) {
					line = line.substring(line.indexOf("[") + "[".length());
					int x = Integer.parseInt(line.substring(0,line.indexOf(",")));
					line = line.substring(line.indexOf(",") + ",".length());
					int y = Integer.parseInt(line.substring(0,line.indexOf("]")));
					addPoint(new Point(x,y));
				}
			}
			
		}
	}
	
	private void cycleTransparentMode() {
		if(transparent == 0) {
			showHelp = false;
			render.stop();
			buffer.stop();
			this.setVisible(false);
			transparent = 1;
			this.setVisible(true);
		}else if(transparent == 1){
			this.setVisible(false);
			transparent = 2;
			this.setVisible(true);
		}else {
			transparent = 0;
			buffer.start();
			render.start();
		}
	}
	
	private void toggleHelp() {
		if(transparent == 0) {
			if(!showHelp) {
				showHelp = true;
				System.out.println(HELP_MESSAGE);
			}else {
				showHelp = false;
			}
		}
	}
	
	private void undo() {
		if(transparent == 0) {
			
			if(posters.size() == 0) {
				return;
			}
			
			if(posters.get(posters.size() - 1).getArray().length == 1) {
				posters.remove(posters.size() - 1);
				return;
			}
			
			posters.get(posters.size() - 1).remove();
		}
	}
	
	private void deleteAll() {
		if(transparent == 0) {
			posters = new ArrayList<Poster>();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case KeyEvent.VK_BACK_SPACE: undo(); break;
		case KeyEvent.VK_SPACE: cycleTransparentMode(); break;
		case KeyEvent.VK_S: saveAsDefault(); break;
		case KeyEvent.VK_D: loadDefault(); break;
		case KeyEvent.VK_F: deleteAll(); break;
		case KeyEvent.VK_H: toggleHelp(); break;
		case KeyEvent.VK_ESCAPE: System.exit(0); break;

		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePoint = e.getPoint();
	}
	
}
