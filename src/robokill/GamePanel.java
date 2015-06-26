package robokill;

import static java.lang.Thread.sleep;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import useful.Direction;
import useful.GlobalKeyListenerFactory;

/**
 * 
 * @author HRM_Shams
 * @author Mr. Coder
 * @version 1.9
 */

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static GamePanel This;

	public Player playerRobot; // the panel of playerRobot!
	private BufferedImage background;

	private boolean isShooting = false;
	private int curMouseX;
	private int curMouseY;

	private ArrayList<Element> elements = new ArrayList<Element>();
	private ArrayList<Door> doors = new ArrayList<Door>();
	private ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private Room currentRoom;

	private final Set<Integer> keys = new HashSet<Integer>(); // related to
																// movement of
																// robot!

	public boolean gameEnded = false;

	public StatusPanel statusPanel = new StatusPanel();

	public static GamePanel getGamePanel() {
		if (This == null) {
			This = new GamePanel();
		}

		return This;
	}

	private GamePanel() {
		super();

		setBounds(0, 0, 1000, 700);

		setSize(new Dimension(1024, 768));
		setLayout(null);

		/** status bar **/
		add(statusPanel);
		/**************/

		/** adding playerRobot to gamePanel **/
		playerRobot = new Player(0, 320, 60, 60, 6, 0);
		add(playerRobot);

		/** adding mouseListener (for rotating head of robot and shooting) **/
		addMouseListenersForRobot();

		/*** adding keyListener for moving ***/
		addKeyListener(new MultiKeyPressListener());

		/** adding a new Thread for moving robot **/
		Thread robotMovement = new Thread(new RobotMovementHandler());
		robotMovement.start();

		/** adding a new thread for shooting bars **/
		Thread shootingBars = new Thread(new ShootingBarsHandler());
		shootingBars.start();

		/** adding elements to GamePanel **/
		 addElements();

//		try {
//			InputStream in = getClass().getResourceAsStream("/data/room 0.dat");
//			ObjectInputStream ois = new ObjectInputStream(in);
//			currentRoom = (Room) ois.readObject();
//			ois.close();
//			rearrange(currentRoom);
//		} catch (IOException | ClassNotFoundException e) {
//			e.printStackTrace();
//		}

		tryOpeningTheDoors();

	}// end of constructor !!

	/**
	 * Determines whether the GamePanel is created (before) or not.
	 * 
	 * @return Returns true if the game panel is created and ready. Otherwise
	 *         false.
	 */
	public static boolean isGamePanelReady() {
		if (This == null)
			return false;
		else
			return true;
	}

	/**
	 * Removes all the elements from the game panel and applies the given room
	 * properties.
	 * 
	 * @param room
	 *            The room for applying new properties.
	 */
	public void rearrange(Room room) {

		background = room.getBackgroundImage();

		/* Remove all elements from gamePanel */
		int a = this.getComponentCount();
		for (int i = a - 1; i >= 0; i--) {
			Component comp = this.getComponent(i);
			if (comp instanceof Element)
				if (!(comp instanceof Player))
					remove(comp);
		}

		doors = room.getDoors();

		enemies = room.getEnemies();

		for (Door door : doors)
			door.revalidateImage();

		for (Element element : room.getElements()) {
			add(element);
			element.revalidateImage();

			if (element instanceof Enemy)
				((Enemy) element).go();
		}

		playerRobot.setLocation(room.getPlayerLocation());

		repaint();
	}

	/**
	 * Adds the elements to the game panel.
	 */
	private void addElements() {
		
		  Block block = new Block(450, 300, Block.BLOCK_TYPE_1); add(block);
		  
		 /* Add Valleys
		 */
		
		  add(new Valley(0, 0, 350, 210)); add(new Valley(640, 0, 350, 210));
		  add(new Valley(0, 480, 350, 210)); add(new Valley(640, 480, 350,
		  210));
		  
		  add(new Valley(0, 210, 170, 50)); add(new Valley(840, 210, 170, 50));
		  add(new Valley(0, 430, 170, 50)); add(new Valley(840, 430, 170, 50));
		 

		try {
			background = ImageIO.read(getClass().getResourceAsStream("/images/GamePanelBackground.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* add door */
		Door door = new Door(960, 301, "3");
		add(door);
		doors.add(door);

		/** enemy **/
		Enemy enemy = new Enemy(800, 301, 80, 80, 1, Enemy.ENEMY_TYPE_1, 2);
		add(enemy);
		enemy.go();
		/*********/

		/* Sample Box */
		add(new Box(300, 300, new Prize(PrizeType.Energy, 3)));

		Room room = new Room(0);
		room.setDoors(doors);

		for (Element element : elements)
			if (!(element instanceof Player))
				room.addElement(element);

		room.setPlayerLocation(playerRobot.getLocation());

		room.setBackgroundImagePath("images/GamePanelBackground.png");

//		try {
//			ObjectOutputStream oos = new ObjectOutputStream(
//					new FileOutputStream("C:/Users/h-noori/Desktop/room 0.dat"));
//			oos.writeObject(room);
//			oos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, this);
	}

	@Override
	public void remove(Component comp) {
		super.remove(comp);

		repaint();

		if (comp instanceof Element)
			elements.remove(comp);

		if (comp instanceof Enemy) {
			enemies.remove(comp);
			tryOpeningTheDoors();
		}
	}

	@Override
	public Component add(Component comp) {
		if (comp instanceof Element) {
			if (!(comp instanceof Bar))
				elements.add((Element) comp);

			if (comp instanceof Enemy)
				enemies.add((Enemy) comp);
		}

		return super.add(comp);
	}

	/**
	 * Checks if the given element is collided with an element or not. If it is
	 * collided with an element, that element is returned. Otherwise null is
	 * returned.
	 * 
	 * @param checkElement
	 *            The element to check if any other element has collided with it
	 *            or not.
	 * @return Returns the element that is collided with the given element.
	 *         Returns null if no collision has occurred.
	 */
	public synchronized Element getCollidedElement(Element checkElement) {
		for (Element e : elements) {
			if (checkElement.isCollided(e) && checkElement != e)
				return e;
		}

		return null;
	}

	/**
	 * Checks if the given element is collided with an element in the next pace
	 * or not. If it is collided with an element in the next pace, that element
	 * is returned. Otherwise null is returned.
	 * 
	 * @param element
	 *            The element to check if any other element has collided with it
	 *            or not.
	 * @param nextLocation
	 *            The new location of the element to check if collision has
	 *            happened.
	 * @return Returns the element that is collided with the given element.
	 *         Returns null if no collision has occurred.
	 */
	public Element getCollidedElement(Element element, Point nextLocation) {

		Dimension size = element.getSize();

		for (Element e : elements) {
			if (e.isCollided(nextLocation, size) && element != e)
				return e;
		}

		return null;
	}

	/**
	 * Determines that whether the element is inside the bounds of the GamePanel
	 * or not. {@link robokill.GamePanel#isElementInside(Point, Dimension)
	 * isElementInside(Point, Dimension)}
	 * 
	 * @param element
	 * @return Returns true if the element is inside the bounds, otherwise
	 *         false.
	 */
	public boolean isElementInside(Element element) {
		return isElementInside(element.getLocation(), element.getSize());
	}

	/**
	 * Determines that whether the element is inside the bounds of the GamePanel
	 * or not.
	 * 
	 * @param element
	 * @return Returns true if the element is inside the bounds, otherwise
	 *         false.
	 */
	public boolean isElementInside(Point elementLoc, Dimension elementSize) {
		if (elementLoc.x >= 0
				&& elementLoc.x + elementSize.getWidth() <= this.getWidth()
				&& elementLoc.y >= 0
				&& elementLoc.y + elementSize.getHeight() <= this.getHeight())
			return true;
		else
			return false;
	}

	// TODO Check if the key is achieved, then open.
	// Otherwise do nothing.
	/**
	 * Opens all of the doors, if all the enemies are killed. Otherwise does
	 * nothing.
	 */
	public void tryOpeningTheDoors() {
		if (enemies.size() == 0)
			for (Door door : doors)
				door.open();
	}

	/**
	 * Gets the current room id. Id is a unique integer to make rooms
	 * distinguishable. w
	 * 
	 * @return Returns the current room id.
	 */
	public int getRoomId() {
		return currentRoom.getId();
	}

	/**
	 * this method has MouseListener and MouseMotionListener ! this do 2 works :
	 * 1) get the coordinate of mouse when it moved or dragged and call method
	 * setTarget in class Robot 1) get the coordinate of mouse when it pressed
	 * or dragged and isShooting will be true to shoot the bars in a thread!
	 */
	private void addMouseListenersForRobot() {
		/*** add mouse listener ***/
		// 1
		addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {/* Do Nothing */
			}

			@Override
			public void mousePressed(MouseEvent e) {
				curMouseX = e.getX();
				curMouseY = e.getY();
				playerRobot.robotHead
						.setTarget(new Point(curMouseX, curMouseY));
				playerRobot.robotHead.repaint();
				isShooting = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				isShooting = false;
			}

			@Override
			public void mouseEntered(MouseEvent e) {/* Do Nothing */
			}

			@Override
			public void mouseExited(MouseEvent e) {/* Do Nothing */
			}
		});

		// 2
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				curMouseX = e.getX();
				curMouseY = e.getY();

				playerRobot.robotHead
						.setTarget(new Point(curMouseX, curMouseY));
				playerRobot.robotHead.repaint();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				int mouseX = e.getX() - 3;
				int mouseY = e.getY() - 25;

				/*** rotating the head of Robot!! ***/
				playerRobot.robotHead.setTarget(new Point(mouseX, mouseY));
				playerRobot.robotHead.repaint();
			}
		}); // end of mouse listener
	}

	/*** a new class in GamePanel class for keyListener **/
	class MultiKeyPressListener implements KeyListener {

		public MultiKeyPressListener() {
			GlobalKeyListenerFactory.setKeyListenerGlobal(this);

		}

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();

			if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_W
					|| keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_D)
				keys.add(keyCode);

		}

		@Override
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();

			if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_W
					|| keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_D)
				keys.remove(keyCode);
		}

		@Override
		public void keyTyped(KeyEvent e) {/* Not used */
		}
	}

	/*******/

	/*** RobotMovementClass ***/
	class RobotMovementHandler implements Runnable {
		@Override
		public void run() {
			while (!gameEnded) {
				if (keys.contains(KeyEvent.VK_W)
						&& keys.contains(KeyEvent.VK_D)) {
					playerRobot.move(Direction.North_East);
				}

				else if (keys.contains(KeyEvent.VK_W)
						&& keys.contains(KeyEvent.VK_A)) {
					playerRobot.move(Direction.North_West);
				}

				else if (keys.contains(KeyEvent.VK_S)
						&& keys.contains(KeyEvent.VK_D)) {
					playerRobot.move(Direction.South_East);
				}

				else if (keys.contains(KeyEvent.VK_S)
						&& keys.contains(KeyEvent.VK_A)) {
					playerRobot.move(Direction.South_West);
				}

				else if (keys.contains(KeyEvent.VK_W)) {
					playerRobot.move(Direction.North);
				}

				else if (keys.contains(KeyEvent.VK_S)) {
					playerRobot.move(Direction.South);
				}

				else if (keys.contains(KeyEvent.VK_A)) {
					playerRobot.move(Direction.West);
				}

				else if (keys.contains(KeyEvent.VK_D)) {
					playerRobot.move(Direction.East);
				}
				try {
					sleep(7);
				} catch (Exception e) {
				}
			}
		}
	}

	/*******/

	/**
	 * related to mouseListeners!
	 */
	class ShootingBarsHandler implements Runnable {
		public void run() {
			while (!gameEnded) {
				if (isShooting) {
					int mouseX = curMouseX;
					int mouseY = curMouseY;

					// rotating the head of Robot!!
					playerRobot.shoot(new Point(mouseX, mouseY));

					try {
						Thread.sleep(50);
					} catch (Exception e) {
						System.out.println("ERROR IN SLEEPING!");
					}
				}
				try {
					sleep(80);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	/****/

} // End Of Class GamePanel
