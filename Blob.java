import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;


public class Blob extends Entities implements Comparable<Entities> {

	double direction;
	int generation;

	Arc2D.Double viewArc = new Arc2D.Double();
	Area area1 = new Area(viewArc);


	double [] inputs;

	double input1;
	double input2;
	double input3;
	double input4;

	double [] outputs;

	private NeuralNet neuralNet;

	int angleOfView = 16;
	int radiusOfView = 400;
	double mutation = 50;

	public Blob() {
		this.x = (int)(Math.random()* 3000 - 1500);
		this.y = (int)(Math.random()* 3000 - 1500);
		this.direction = (Math.random()*360);
		this.size = 15;
		this.score = 0;
		this.generation = 1;
		inputs = new double [2];
		outputs = new double [3];
		neuralNet = new NeuralNet(2,8,8,3);

		for (int i = 0; i < this.gene.length; i++)
			this.gene[i] = (Math.random()*2)-1;

		neuralNet.setWeights(this.gene);

	}

	public Blob(double b[]) {
		super();
		this.x = (int)(Math.random()* 3000 - 1500);
		this.y = (int)(Math.random()* 3000 - 1500);
		this.direction = (Math.random()*360);
		this.size = 15;
		this.score = 0;
		inputs = new double [2];
		outputs = new double [3];

		neuralNet = new NeuralNet(2,8,8,3);

		for (int i = 0; i < this.gene.length; i++)
			this.gene[i] = ((this.mutation-1)*b[i]+(Math.random()*2)-1)/this.mutation;

		neuralNet.setWeights(this.gene);

	}

	@Override
	public void collidedWith(Entities other) {

		if (other instanceof Food){
			this.score++;
			if (this.score > 30){
				MainPanelNN.generationChange = true;
			}
		}
	}

	@Override
	public void drawEntity(Graphics g) {

		Graphics2D g2d = (Graphics2D)g;
		AffineTransform old = g2d.getTransform();

		g.setColor(Color.yellow);
		if (this.score > 5)
			g.setColor(Color.red);
		if (this.score > 10)
			g.setColor(Color.blue);
		if (this.score > 20)
			g.setColor(Color.pink);
		
		if (this.selected)
			g.setColor(Color.cyan);
		
		g2d.rotate(Math.toRadians(this.direction),this.x,this.y);
		Ellipse2D.Double body = new Ellipse2D.Double(this.x-this.size/2, this.y-this.size/2, this.size, this.size);
		g2d.draw(body);
		g2d.fill(body);
		g.setColor(Color.black);
		Ellipse2D.Double tail = new Ellipse2D.Double(this.x+this.size/4+this.size/8, this.y-this.size/8, this.size/4, this.size/4);
		g2d.draw(tail);
		g2d.fill(tail);
		g2d.setTransform(old);
		//g2d.draw(new Arc2D.Double(this.x-radiusOfView,this.y-radiusOfView,2*radiusOfView,2*radiusOfView,-this.direction-(this.angleOfView/1),this.angleOfView,Arc2D.PIE));
		this.area1 = new Area(new Arc2D.Double(this.x-radiusOfView,this.y-radiusOfView,2*radiusOfView,2*radiusOfView,-this.direction-(this.angleOfView/1),this.angleOfView,Arc2D.PIE));

		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//g2d.drawString(""+score+"", (int)this.x-5, (int)this.y+5);

	}

	@Override
	public void AI(){

		input1 = 0;
		input2 = 0;

		for (Entities e : MainPanelNN.listEntities) {

			if (e instanceof Food){

				try {
					if(this.area1.intersects(e.getBounds())){
						input1 = 1;
					}
				} catch (Exception e1) {
				}
			}
		}
		this.inputs[0] = input1;
		this.inputs[1] = input2;

		this.outputs = this.neuralNet.getOutputs(this.inputs);

		direction += (outputs[0]-outputs[1]);

		this.outputs[2] = Math.abs(this.outputs[2]);

		this.x = ((outputs[2])*Math.cos(Math.toRadians(this.direction))) + this.x;
		this.y = ((outputs[2])*Math.sin(Math.toRadians(this.direction))) + this.y;
		if (this.x < -1500)
			this.x = -1499;
		if (this.y < -1500)
			this.y = -1499;
		if (this.x > 1500)
			this.x = 1499;
		if (this.y > 1500)
			this.y = 1499;
	}

	public void duplicateWithModifs(){

		this.x = (int)(Math.random()*1160 +20);
		this.y = (int)(Math.random()*670 +20);
		
		this.score = 0;
		this.numberOfGeneration++;
		
		MainPanelNN.addEntity(new Blob(this.gene));
		MainPanelNN.addEntity(new Blob(this.gene));
		MainPanelNN.addEntity(new Blob());
		MainPanelNN.addEntity(new Blob());


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
