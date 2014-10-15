import java.awt.Graphics;
import java.awt.Rectangle;


public abstract class Entities implements Comparable<Entities>{

	protected double x;
	protected double y;
	protected int vie;
	protected int score;
	protected double gene [] = new double [600];
	private Rectangle me = new Rectangle();
	private Rectangle him = new Rectangle();
	protected int size;
	protected boolean selected = false;
	protected int numberOfGeneration;
	
	public Entities(){
		
	}
	
public Entities(double g[]){
		
	}
	
	public Entities(double posX, double posY, int size){
		this.x = posX;
		this.y = posY;
		this.size = size;
	}

	public abstract void collidedWith(Entities other);

	public abstract void drawEntity(Graphics g);

	public abstract void AI();
	
	public boolean collidesWith(Entities other) {

		me.setBounds((int) this.x-this.size/2,(int) this.y-this.size/2,this.size,this.size);
		him.setBounds((int) other.x-other.size/2,(int) other.y-other.size/2,other.size,other.size);
		
		return me.intersects(him);
	}

	public Rectangle getBounds(){
		
		me.setBounds((int) this.x-this.size/2,(int) this.y-this.size/2,this.size,this.size);
		
		return me;
		
	}
	
	public void select (){
		this.selected = true;
	}
	
	public void deselect (){
		this.selected = false;
	}
	public boolean isSelected (){
		return this.selected;
	}
	
	public void duplicateWithModifs() {
		// TODO Auto-generated method stub
		
	}

}
