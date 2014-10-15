import java.awt.Color;
import java.awt.Graphics;


public class Food extends Entities implements Comparable<Entities> {

	public Food() {
		super();
		this.x = (int)(Math.random()* 3000 - 1500);
		this.y = (int)(Math.random()* 3000 - 1500);
		this.size = 5;
		this.score = -1;
	}

	@Override
	public void collidedWith(Entities other) {

		if (other instanceof Blob){
			MainPanelNN.removeEntity(this);
			MainPanelNN.addEntity(new Food());
		}
	}

	@Override
	public void drawEntity(Graphics g) {

		g.setColor(Color.red);
		g.fillArc((int) (this.x-this.size/2), (int) (this.y-this.size/2), this.size, this.size, 0, 360);

	}

	@Override
	public void AI() {

		//    'dead' entity

	}

	@Override
	public int compareTo(Entities o) {
		if (o.score < this.score)
			return -1;
		if (o.score == this.score)
			return 0;
		if (o.score > this.score)
			return 1;
		return 0;
	}

}
