package robokill;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * 
 * @author HRM_SHAMS
 * @version 1.3
 */

public class StatusPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Gooshe gooshe = new Gooshe();
	private HealthBar healthBar = new HealthBar();
	private BarBackground healthBackground = new BarBackground(new Point(26, 0));
	private ShieldBar shieldBar = new ShieldBar();
	private BarBackground shieldBackground = new BarBackground(
			new Point(126, 0));

	public StatusPanel() {
		setSize(1000, 27);
		setLocation(0, 673);
		setOpaque(false);
		setLayout(null);

		add(new GoosheRight());

		add(gooshe);
		add(new HealthText());
		add(healthBar);
		add(healthBackground);
		add(new ShieldText());
		add(shieldBar);
		add(shieldBackground);
	}

	/**
	 * Reduces the health of the player.
	 * 
	 * @param reducePercent
	 *            An integer between 1 to 100
	 */
	public synchronized void reduceHealth(int reducePercent) {
		new Thread() {
			public void run() {
				int reduceAmount = (int) ((double) (reducePercent / 100.0) * 117);

				int curX = healthBar.getX();
				int curY = healthBar.getY();

				for (int i = 1; i <= reduceAmount; i++) {
					curX--;
					healthBar.setLocation(curX, curY);

					try {
						Thread.sleep(10);
					} catch (Exception e) {
						System.out.println("error in sleeping");
					}
				}
			}
		}.start();
	}

	/**
	 * Increases the health of the player.
	 * 
	 * @param increasePercent
	 *            An integer between 1 to 100
	 */
	public synchronized void increaseHealth(int increasePercent) {
		new Thread() {
			public void run() {
				int increaseAmount = (int) ((double) (increasePercent / 100.0) * 117);

				int curX = healthBar.getX();
				int curY = healthBar.getY();

				for (int i = 1; i <= increaseAmount; i++) {
					curX++;
					healthBar.setLocation(curX, curY);

					try {
						Thread.sleep(10);
					} catch (Exception e) {
						System.out.println("error in sleeping");
					}
				}
			}
		}.start();
	}

	/**
	 * Reduces the shield of the player.
	 * 
	 * @param reducePercent
	 *            An integer between 1 to 100
	 */
	public synchronized void reduceShield(int reducePercent) {
		new Thread() {
			public void run() {
				int reduceAmount = (int) ((double) (reducePercent / 100) * 117);

				int curX = shieldBar.getX();
				int curY = shieldBar.getY();

				for (int i = 1; i <= reduceAmount; i++) {
					curX--;
					shieldBar.setLocation(curX, curY);

					try {
						Thread.sleep(10);
					} catch (Exception e) {
						System.out.println("error in sleeping");
					}
				}
			}
		}.start();
	}

	/**
	 * Increases the shield of the player.
	 * 
	 * @param increasePercent
	 *            An integer between 1 to 100
	 */
	public synchronized void increaseShield(int increasePercent) {
		new Thread() {
			public void run() {
				int increaseAmount = (int) ((double) (increasePercent / 100) * 117);

				int curX = shieldBar.getX();
				int curY = shieldBar.getY();

				for (int i = 1; i <= increaseAmount; i++) {
					curX++;
					shieldBar.setLocation(curX, curY);

					try {
						Thread.sleep(10);
					} catch (Exception e) {
						System.out.println("error in sleeping");
					}
				}
			}
		}.start();
	}

	class GoosheRight extends JPanel {

		private static final long serialVersionUID = 1L;

		BufferedImage image;

		public GoosheRight() {
			setSize(159, 27);
			setLocation(841, 0);
			setOpaque(false);

			try {
				image = ImageIO.read(getClass().getResource(
						"/images/panel/goosheRight.png"));
			} catch (IOException e) {
				System.out.println("error in reading image gooshe");
			}
		}

		public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

	}

	class Gooshe extends JPanel {

		private static final long serialVersionUID = 1L;

		BufferedImage image;

		public Gooshe() {
			setSize(40, 27);
			setLocation(0, 0);
			setOpaque(false);

			try {
				image = ImageIO.read(getClass().getResource(
						"/images/panel/gooshe.png"));
			} catch (IOException e) {
				System.out.println("error in reading image gooshe");
			}
		}

		public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

	}

	class BarBackground extends JPanel {

		private static final long serialVersionUID = 1L;

		BufferedImage image;

		public BarBackground(Point location) {
			setSize(121, 27);
			setLocation(location);
			setOpaque(false);

			try {
				image = ImageIO.read(getClass().getResource(
						"/images/panel/BarBackground.png"));
			} catch (IOException e) {
				System.out.println("error in reading image barBAckground");
			}
		}

		public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

	}

	class HealthBar extends JPanel {

		private static final long serialVersionUID = 1L;

		BufferedImage image;

		public HealthBar() {
			setSize(111, 27);
			setLocation(35, 0);
			setOpaque(false);

			try {
				image = ImageIO.read(getClass().getResource(
						"/images/panel/HealthBar.png"));
			} catch (IOException e) {
				System.out.println("error in reading image HealthBar");
			}
		}

		public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

	}

	class ShieldBar extends JPanel {

		private static final long serialVersionUID = 1L;

		BufferedImage image;

		public ShieldBar() {
			setSize(121, 27);
			setLocation(126, 0);
			setOpaque(false);

			try {
				image = ImageIO.read(getClass().getResource(
						"/images/panel/ShieldBar.png"));
			} catch (IOException e) {
				System.out.println("error in reading image SheildBar");
			}
		}

		public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

	}

	class HealthText extends JPanel {

		private static final long serialVersionUID = 1L;

		BufferedImage image;

		public HealthText() {
			setSize(121, 27);
			setLocation(26, 0);
			setOpaque(false);

			try {
				image = ImageIO.read(getClass().getResource(
						"/images/panel/HealthText.png"));
			} catch (IOException e) {
				System.out.println("error in reading image SheildBar");
			}
		}

		public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

	}

	class ShieldText extends JPanel {

		private static final long serialVersionUID = 1L;

		BufferedImage image;

		public ShieldText() {
			setSize(121, 27);
			setLocation(126, 0);
			setOpaque(false);

			try {
				image = ImageIO.read(getClass().getResource(
						"/images/panel/ShieldText.png"));
			} catch (IOException e) {
				System.out.println("error in reading image SheildBar");
			}
		}

		public void paintComponent(Graphics g) {
			g.drawImage(image, 0, 0, null);
		}

	}

}
