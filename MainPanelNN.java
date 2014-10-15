import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;


public class MainPanelNN extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 1L;

	boolean jeuEnCours = true;
	boolean gamePaused = false;

	private NumberFormat formatter = new DecimalFormat("#0.00");

	boolean grapheAfficher = false;
	private AffineTransform tx1;
	private AffineTransform tx1Inv;
	private Point2D clickPoint;
	private Blob blobSelected;
	private Blob blobHighLighted;

	boolean mousePressed = false;
	Rectangle click;
	int [] mouseClickPos = {0,0,0,0};
	boolean showRectangle = false;
	boolean zoomIn = false;
	boolean closeUp = false;
	double [] zoomValues = {0,0,1,1};

	static boolean generationChange = false;
	int speed = 10;
	int generation = 1;
	int score = 0;
	static int scoreBad = 0;
	public int [] bestScore = {0,0};

	static ArrayList<Entities> listEntities = new ArrayList<Entities>();
	static ArrayList<Entities> listIntraGeneration = new ArrayList<Entities>();
	static ArrayList<Entities> waitingList = new ArrayList<Entities>();
	private static ArrayList<Entities> removeList = new ArrayList<Entities>();

	static ArrayList<Entities> stockageList = new ArrayList<Entities>();

	static ArrayList<Integer[]> listPoints = new ArrayList<Integer[]>();


	static JFrame mainFrame;
	static JFrame statsFrame;

	static JPanel mainPanel;
	JPanel buttonPanel;

	JButton speedUp;
	JButton speedDown;
	JButton playButton;
	JButton stopButton;
	JButton afficherG;
	JButton cacherG;
	JButton zoomOut;

	static int size = 70;

	public MainPanelNN(){

		setBackground(Color.white);

		buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.gray);
		playButton = new JButton("Play");
		buttonPanel.add(playButton);
		stopButton = new JButton("Stop");
		buttonPanel.add(stopButton);
		speedUp = new JButton("Faster");
		buttonPanel.add(speedUp);
		speedDown = new JButton("Slower");
		buttonPanel.add(speedDown);
		afficherG = new JButton("Show");
		buttonPanel.add(afficherG);
		cacherG = new JButton("Hide");
		buttonPanel.add(cacherG);
		zoomOut = new JButton("Zoom out");
		buttonPanel.add(zoomOut);

		addMouseListener(this);
		addMouseMotionListener(this);
		playButton.addActionListener(this);
		stopButton.addActionListener(this);
		speedUp.addActionListener(this);
		speedDown.addActionListener(this);
		afficherG.addActionListener(this);
		cacherG.addActionListener(this);
		zoomOut.addActionListener(this);

		mainFrame.add(buttonPanel, BorderLayout.PAGE_END);

		tx1 = new AffineTransform();
		tx1Inv = new AffineTransform();
		click = new Rectangle();
		clickPoint = new Point();

		Thread t1 = new Thread(new lancerLoop());
		t1.start();

	}


	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				mainFrame = new JFrame();
				mainFrame.setTitle("Neural networks");
				mainFrame.setSize(1200,800);
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mainPanel = new MainPanelNN();
				mainFrame.add(mainPanel);
				mainFrame.setVisible(true);
			}


		});
	}

	class  lancerLoop implements Runnable{

		public void run() {

			for (int i = 0; i < 30; i++){
				listEntities.add(new Blob());
			}
			for (int i = 0; i < 100; i++){
				listEntities.add(new Food());
			}


			while (jeuEnCours) {

				while (gamePaused){

					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					repaint();
				}

				for (Entities e : listEntities) {
					e.AI();
				}

				for (int p = 0; p < listEntities.size(); p++) {
					for (int s = p + 1; s < listEntities.size(); s++) {
						Entities mee = (Entities) listEntities.get(p);
						Entities him = (Entities) listEntities.get(s);

						if ((mee instanceof Blob & him instanceof Food)|(mee instanceof Food & him instanceof Blob)){
							if (mee.collidesWith(him)) {
								mee.collidedWith(him);
								him.collidedWith(mee);
							}
						}}
				}



				Collections.sort(listEntities);
				repaint();
				try {
					Thread.sleep(speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (waitingList.size() != 0) {
					listEntities.addAll(waitingList);
					waitingList.clear();
				}

				listEntities.removeAll(removeList);
				removeList.clear();

				if (generationChange){

					for (Entities e : listEntities){

						if (e.score >= 0){
							score+=e.score;
							scoreBad+=e.numberOfGeneration;
						}

					}
					Integer[] a  = {score,scoreBad};
					listPoints.add(a);
					if (score > bestScore[1]){
						bestScore[0] = generation;
						bestScore[1] = score;
					}
					score = 0;
					scoreBad = 0;


					generation++;

					Collections.sort(listEntities);
					if (!listEntities.isEmpty()) {
						listIntraGeneration.add(listEntities.get(0));
						listIntraGeneration.add(listEntities.get(1));
						listIntraGeneration.add(listEntities.get(2));
						listIntraGeneration.add(listEntities.get(3));
						listIntraGeneration.add(listEntities.get(4));
					}
					listEntities.clear();


					for (Entities e : listIntraGeneration){

						e.duplicateWithModifs();

					}
					listEntities.addAll(listIntraGeneration);

					if (waitingList.size() != 0) {
						listEntities.addAll(waitingList);
						waitingList.clear();
					}

					for (int i = 0; i < 100; i++){
						listEntities.add(new Food());
					}

					listEntities.removeAll(removeList);
					removeList.clear();
					listIntraGeneration.clear();

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					generationChange = false;

				}

			}
		}
	}
	//id943782266555odr
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (showRectangle)
			g.drawRect(mouseClickPos[0],mouseClickPos[1],mouseClickPos[2]-mouseClickPos[0],mouseClickPos[3]-mouseClickPos[1]);


		Graphics2D g2d = (Graphics2D) g;
		AffineTransform old = g2d.getTransform();
		tx1 = g2d.getTransform();
		tx1.translate(600,350);
		tx1.scale(0.2, 0.2);

		if (zoomIn){

			if (closeUp){
				zoomValues[0] = -blobSelected.x;
				zoomValues[1] = -blobSelected.y;
			}

			tx1.scale(zoomValues[2], zoomValues[3]);
			tx1.translate(zoomValues[0],zoomValues[1]);
		}
		g2d.transform(tx1);		
		g.setColor(Color.black);
		g.drawRect(-1500, -1500, 3000, 3000);

		for(int i = 0; i < listEntities.size(); i++)
		{
			try {
				Entities e = listEntities.get(i);
				e.drawEntity(g);
			} catch (java.lang.NullPointerException e){

			}

		}
		g2d.setTransform(old);

		//fill the border with gray
		g.setColor(Color.gray);
		g.fillRect(0,0,300,750);
		g.fillRect(300,0,601,50);
		g.fillRect(901,0,300,750);
		g.fillRect(300,651,601,100);
		g.setColor(Color.black);

		//top
		g.drawString("Zoom value: "+formatter.format(zoomValues[2]), 300, 20);
		g.drawString("Pos values from the center: "+formatter.format(zoomValues[0])+" and "+formatter.format(zoomValues[1]), 300, 40);
		g.drawString("Generation "+generation, 310, 70);


		//right
		g.drawString("Best generation : "+bestScore[0], 930,300);
		g.drawString("With the score of : "+bestScore[1], 930,320);

		// Show information about the selected Blob
		// left
		if (blobSelected != null){
			g.drawString("Blob selected :"+blobSelected,20,40);

			g.drawString("Position X :"+formatter.format(blobSelected.x),20,80);
			g.drawString("Position Y :"+formatter.format(blobSelected.y),20,100);
			g.drawString("Score :"+blobSelected.score,20,120);
			g.drawString("Age in generation :"+blobSelected.numberOfGeneration,20,140);

			if (!zoomIn){
				g.drawOval((int) (blobSelected.x*0.2 + 600)-10,(int) (blobSelected.y*0.2 + 350)-10,20,20);
			}

		}
		else {
			int x = 0;

			try {
				for (Entities e : listEntities){
					if (e instanceof Blob){
						x++;
						if (this.blobHighLighted == e)
							g.setColor(Color.red);
						else
							g.setColor(Color.black);

						g.drawString(""+e+" score: "+e.score,40,40+20*x);
					}
				}
			} catch (Exception e) {
			}

			g.setColor(Color.black);
			g.drawString("There're "+x+" entities :",20,40);

			if (blobHighLighted != null & !zoomIn)
				g.drawOval((int) (blobHighLighted.x*0.2 + 600)-10,(int) (blobHighLighted.y*0.2 + 350)-10,20,20);

		}

		if (grapheAfficher){

			g.setColor(Color.white);
			g.fillRect(0, 0, 1300, 800);
			g.setColor(Color.black);
			g.drawLine(0,600,1500,600);
			g.drawLine(100,0,100,800);

			int x = 0;

			for (Integer[] i : listPoints){
				g.setColor(Color.black);
				g.fillRect(100+(2*x),600-(i[0]),4,4);
				g.setColor(Color.red);
				g.fillRect(100+(4*x),600-(i[1]),4,4);
				x++;
			}
		}
	}


	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == playButton ){
			gamePaused = false;
		}
		if (e.getSource() == stopButton ){
			gamePaused = true;
		}
		if (e.getSource() == afficherG ){
			grapheAfficher = true;
		}
		if (e.getSource() == cacherG ){
			grapheAfficher = false;
		}
		if (e.getSource() == zoomOut ){
			zoomValues[0] = 0;
			zoomValues[1] = 0;
			zoomValues[2] = 1;
			zoomValues[3] = 1;
			zoomIn = false;
			closeUp = false;
		}
		if (e.getSource() == speedDown ){
			speed++;
		}
		if (e.getSource() == speedUp ){
			speed--;
			if (speed < 0)
				speed = 0;
		}
	}

	public void mouseClicked(MouseEvent e) {

		if (e.getX() > 300 & e.getX() < 900 & e.getY() > 50 & e.getY() < 650){

			try {
				tx1Inv = tx1.createInverse();
			} catch (NoninvertibleTransformException e1) {
			}
			clickPoint.setLocation(e.getX(),e.getY());

			tx1Inv.transform(clickPoint, clickPoint);

			click.setBounds((int)clickPoint.getX()-20,(int)clickPoint.getY()-20,40,40);
			
			if (blobSelected != null & zoomIn){
				zoomIn = false;
				closeUp = false;
				zoomValues[0] = 0;
				zoomValues[1] = 0;
				zoomValues[2] = 1;
				zoomValues[3] = 1;
			}
			

			for(int i = 0; i < listEntities.size(); i++)
			{
				try {
					Entities en = listEntities.get(i);
					if (blobSelected != null)
						blobSelected.deselect();
					blobSelected = null;
					if (en instanceof Blob){

						if (en.isSelected())
							en.deselect();

						if (en.getBounds().intersects(click)){
							this.blobSelected = (Blob) en;
							en.select();
							break;
						}

					}
				} catch (java.lang.NullPointerException en){}

			}
		} else if (e.getX() > 36 & e.getX() < 205 & e.getY() > 50 & e.getY() < 630 & blobSelected == null){

			if (blobSelected != null)
				blobSelected.deselect();

			this.blobSelected = (Blob) listEntities.get((int)(e.getY()-50)/20);
			listEntities.get((int)(e.getY()-50)/20).select();

			if (!zoomIn){

				zoomIn = true;
				zoomValues[0] = -blobSelected.x;
				zoomValues[1] = -blobSelected.y;
				zoomValues[2] = 4;
				zoomValues[3] = 4;
				closeUp = true;

			}

		}
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub	
	}
	public void mousePressed(MouseEvent e) {
		mousePressed = true;
		this.mouseClickPos[0] = e.getX();
		this.mouseClickPos[1] = e.getY();
		this.mouseClickPos[2] = e.getX();
		this.mouseClickPos[3] = e.getY();

	}
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;

		if(showRectangle){
			zoomIn = true;

			zoomValues[0] = zoomValues[0] + ((3000) - (this.mouseClickPos[0]+this.mouseClickPos[2])*5/2)/zoomValues[2];
			zoomValues[1] = zoomValues[1] + ((1750) - (this.mouseClickPos[1]+this.mouseClickPos[3])*5/2)/zoomValues[3];

			try {
				zoomValues[2] = (600/(0.5*(this.mouseClickPos[2]-this.mouseClickPos[0]+this.mouseClickPos[3]-this.mouseClickPos[1])))*zoomValues[2];
				zoomValues[3] = (600/(0.5*(this.mouseClickPos[2]-this.mouseClickPos[0]+this.mouseClickPos[3]-this.mouseClickPos[1])))*zoomValues[3];
			} catch (Exception e1) {
			}
			showRectangle = false;
		}
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		this.mouseClickPos[2] = e.getX();
		this.mouseClickPos[3] = e.getY();
		if ((this.mouseClickPos[2]-this.mouseClickPos[0])*(this.mouseClickPos[3]-this.mouseClickPos[1]) > 400){
			showRectangle = true;
		}
		else {
			showRectangle = false;
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {


		if (e.getX() > 36 & e.getX() < 205 & e.getY() > 50 & e.getY() < 650){

			this.blobHighLighted = (Blob) listEntities.get((int)(e.getY()-50)/20);

		}
	}

	public static void removeEntity(Entities entity) {
		removeList.add(entity);
	}
	public static void addEntity(Entities entity) {
		waitingList.add(entity);
	}


}
