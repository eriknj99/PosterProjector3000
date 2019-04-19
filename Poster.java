import java.awt.Point;
import java.awt.Polygon;


public class Poster {
	
	private Point a;
	private Point b;
	private Point c;
	private Point d;
	
	private int size;
	
	public Poster() {
		a = null;
		b = null;
		c = null;
		d = null;
		size = 0;
	}
	
	public Poster(Point a) {
		this.a = a;
		b = null;
		c = null;
		d = null;
		size = 1;
	}
	
	public Poster(Point a, Point b, Point c, Point d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		size = 4;
	}
	
	public void add(Point p) {
		switch(size) {
			case 0: a = (Point) p.clone(); size++; break;
			case 1: b = (Point) p.clone(); size++; break;
			case 2: c = (Point) p.clone(); size++; break;
			case 3: d = (Point) p.clone(); size++; break;
			default: break;
		}
	}
	
	public void remove() {
		switch(size) {
			case 1: a = null; size--; break;
			case 2: b = null; size--; break;
			case 3: c = null; size--; break;
			case 4: d = null; size--; break;
			default: break;
		}
	}
	
	public boolean isComplete() {
		if(size == 4) {
			return true;
		}
		
		return false;
	}
	
	public Point[] getArray() {
		
		Point[] out = new Point[size];
		
		for(int i = 0; i < out.length; i++) {
			switch(i) {
				case 0: out[i] = a; break;
				case 1: out[i] = b; break;
				case 2: out[i] = c; break;
				case 3: out[i] = d; break;
			}
		}
		
		return out;
	}
	
	public Polygon getPolygon() {
		
		if(!isComplete()) {
			return null;
		}
		
		Polygon out = new Polygon();
		
		Point[] tmp = getArray();
		
		for(Point p:tmp) {
			out.addPoint(p.x,p.y);
		}
		
		return out;

	}
	
}
