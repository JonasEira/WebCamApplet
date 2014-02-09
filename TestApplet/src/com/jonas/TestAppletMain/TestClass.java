package com.jonas.TestAppletMain;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.swing.JButton;

public class TestClass extends Applet implements ActionListener {

	public class PictureTimer extends TimerTask {

		private Date dateGen = new Date();

		@Override
		public void run() {
			dateGen.setTime(System.currentTimeMillis());
			testVariable = dateGen.toString();
			callback();
		}

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4000340353503634623L;
	private int red = 0, green = 0, blue = 0;
	private String testVariable;
	private String rgbString;
	private int rdiff;
	private int gdiff;
	private int bdiff;
	private double speed;
	private URI serverLocation;
	private URL imageLoc;
	private long _timeSinceLastDraw = System.currentTimeMillis();
	private Image drawn;
	private Image renderArea = new BufferedImage(5, 5, 1);

	public TestClass() {
		TimerTask t = new PictureTimer();
		testVariable = "Test: " + new Date().toString();
		rgbString = "Hmm?";
		Timer updateTimer = new Timer("updateTimer");
		Date start = new Date();
		start.setTime(System.currentTimeMillis() + 100);
		updateTimer.schedule(t, start, (int)(1000.0/24.0));
		rdiff = (int) (9.0 * Math.random()) + 1;
		gdiff = (int) (9.0 * Math.random()) + 1;
		bdiff = (int) (9.0 * Math.random()) + 1;
		speed = 9.0;
		JButton resetButton = new JButton("Reset");
		this.add(resetButton);
		resetButton.addActionListener(this);
		this.setBackground(Color.black);
		renderArea = createImage(5, 5);
		serverLocation = getURI();
		if (serverLocation != null) {
			Image img = null;
			try {
				imageLoc = new URI(serverLocation.toString() + "/lastsnap.jpg")
						.toURL();
			} catch (MalformedURLException | URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			img = getImage();
			drawn = img;
			System.out.println("Image info: " + img.toString());
		}
	}

	private Image getImage() {
		Image img = null;
		try {
			img = ImageIO.read(imageLoc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return img;
	}

	private URI getURI() {
		try {
			URI server = new URI("http://hpserver");
			return server;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void paint(Graphics g) {
		System.out.println("Paint");
		g.drawImage(renderArea, 0, 0, null);
		super.paint(g);
	}

	@Override
	public void update(Graphics g2) {
		// TODO Auto-generated method stub
		System.out.println("Update");
		int width = g2.getClipBounds().width;
		int height = g2.getClipBounds().height;
		if(renderArea == null){
			renderArea = createImage(width, height);
		} else if(width != renderArea.getWidth(null) || height != renderArea.getHeight(null)) {
			renderArea = createImage(width, height);
		}
		Graphics g = renderArea.getGraphics();

		if (drawn != null) {
			g.drawImage(drawn.getScaledInstance(width, height,
					BufferedImage.SCALE_SMOOTH), 0, 0, null);
		}
		// ----------------------------
		g.setColor(new Color(red, green, blue));
		int testWidth = g.getFontMetrics().stringWidth(testVariable);
		g.setFont(g.getFont().deriveFont(
				(float) g.getFont().getSize2D() * (float) width
						/ (float) testWidth * (float) 0.5));
		testWidth = g.getFontMetrics().stringWidth(testVariable);
		// int rgbWidth = g.getFontMetrics().stringWidth(rgbString);
		// g.drawString(testVariable, width / 2 - testWidth / 2, height / 2);
		// g.drawString(rgbString, width / 2 - rgbWidth / 2, height / 2
		// + g.getFontMetrics().getHeight());
		g.drawString(testVariable, 0, height - g.getFontMetrics().getHeight());
		g.drawString(rgbString, 0, height - g.getFontMetrics().getHeight() / 4);
		// ----------------------------

		if (System.currentTimeMillis() - _timeSinceLastDraw > 2500) {
			fireImageUpdate();
			_timeSinceLastDraw = System.currentTimeMillis();
		}
		paint(g2);
	}

	private void fireImageUpdate() {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				drawn = getImage();
			}
		};
		Thread runnerThread = new Thread(r);
		runnerThread.start();
	}

	public void callback() {
		System.out.println("Callback");
		this.getNewColors();
		this.repaint();
	}

	private void getNewColors() {

		red = red + rdiff;
		green = green + gdiff;
		blue = blue + bdiff;

		if (red < 0) {
			red = 0;
			rdiff = (int) (speed * Math.random()) + 1;
		} else if (red > 255) {
			red = 255;
			rdiff = (int) (-speed * Math.random()) - 1;
		}
		if (green < 0) {
			green = 0;
			gdiff = (int) (speed * Math.random()) + 1;
		} else if (green > 255) {
			green = 255;
			gdiff = (int) (-speed * Math.random()) - 1;
		}
		if (blue < 0) {
			blue = 0;
			bdiff = (int) (speed * Math.random()) + 1;
		} else if (blue > 255) {
			blue = 255;
			bdiff = (int) (-speed * Math.random()) - 1;
		}

		rgbString = "R=" + red + " G=" + green + " B=" + blue;

	}

	public static void main(String[] args) {
		TestClass t = new TestClass();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("event:" + arg0.getActionCommand());
		if (arg0.getActionCommand() == "Reset") {
			red = 0;
			green = 0;
			blue = 0;
		}
	}
}
