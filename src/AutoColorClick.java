import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.sf.feeling.swt.win32.extension.system.WindowsSession;

public class AutoColorClick extends Thread {

	private Color objectColor = null;
	private List<Color> objectColorColection = null;
	private boolean stop = false;
	private boolean randomClick = false;
	private boolean colorColection = false;
	private boolean newScreen = false;
	private int delay = 9000;
	private int screenBeginX;
	private int screenEndX;
	private int screenBeginY;
	private int screenEndY;
	private int min;
	private int hour;
	private int day;
	
	public AutoColorClick() {
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
	
	public Color getObjectColor() {
		return objectColor;
	}

	public void setObjectColor(Color objectColor) {
		this.objectColor = objectColor;
	}
	
	public List<Color> getObjectColorColection() {
		return objectColorColection;
	}

	public void setObjectColorColection(List<Color> objectColorColection) {
		this.objectColorColection = objectColorColection;
	}

	public boolean isColorColection() {
		return colorColection;
	}

	public void setColorColection(boolean colorColection) {
		this.colorColection = colorColection;
	}

	public int getScreenBeginX() {
		return screenBeginX;
	}

	public void setScreenBeginX(int screenBeginX) {
		this.screenBeginX = screenBeginX;
	}

	public int getScreenEndX() {
		return screenEndX;
	}

	public void setScreenEndX(int screenEndX) {
		this.screenEndX = screenEndX;
	}

	public int getScreenBeginY() {
		return screenBeginY;
	}

	public void setScreenBeginY(int screenBeginY) {
		this.screenBeginY = screenBeginY;
	}

	public int getScreenEndY() {
		return screenEndY;
	}

	public void setScreenEndY(int screenEndY) {
		this.screenEndY = screenEndY;
	}
	
	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
	
	public boolean isRandomClick() {
		return randomClick;
	}

	public void setRandomClick(boolean randomClick) {
		this.randomClick = randomClick;
	}
	
	public boolean isNewScreen() {
		return newScreen;
	}

	public void setNewScreen(boolean newScreen) {
		this.newScreen = newScreen;
	}

	public void run() {
		try {
			boolean doHalfBarrelRoll = false;
			Robot robot = new Robot();
			action: for (int x = screenBeginX; x <= screenEndX; x++) {
				for (int y = screenBeginY; y <= screenEndY; y++) {
					if (colorColection && objectColorColection != null && (objectColorColection.contains(new Color(robot.getPixelColor(x, y).getRGB()))))
						doHalfBarrelRoll = performAction(robot, x, y);
					else if (robot.getPixelColor(x, y).getRGB() == objectColor.getRGB())
						doHalfBarrelRoll = performAction(robot, x, y);
					scheduledShutDown();
					performTempStop();
				}
				if (x > (screenEndX-1)) {
					x = screenBeginX;
					roll(doHalfBarrelRoll, robot);
					doHalfBarrelRoll = true;
					continue action;
				}
				if (newScreen) {
					newScreen = false;
					continue action;
				}
				preventJamming(robot);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean performAction(Robot robot, int x, int y) {
		boolean doHalfBarrelRoll;
		attack(robot, x, y, randomClick);
		doHalfBarrelRoll = false;
		return doHalfBarrelRoll;
	}

	private void preventJamming(Robot robot) {
		if (!colorColection)
			robot.delay(8-((int)Math.round(Math.random()*2)));
		else
			robot.delay(40-((int)Math.round(Math.random()*4)));
	}

	private void performTempStop() throws InterruptedException {
		while (stop)
			Thread.sleep(1);
	}

	private void roll(boolean doHalfBarrelRoll, Robot robot) throws InterruptedException {
		if (Math.round(Math.random() * 1) == 1 && doHalfBarrelRoll)
			performArrows(robot, KeyEvent.VK_LEFT);
		else if (Math.round(Math.random() * 1) == 0 && doHalfBarrelRoll)
			performArrows(robot, KeyEvent.VK_RIGHT);
	}

	private void scheduledShutDown() throws IOException, InterruptedException {
		if (isDateToFinish(min, hour, day)) {
			// windows finishment
			System.gc();
			Thread.sleep(500);
			Runtime.getRuntime().exec("taskkill /im firefox.exe /t /f");
			Thread.sleep(500);
			WindowsSession.Shutdown(true);
			Thread.sleep(500);
			System.exit(0);
			return;
		}
	}

	private void performArrows(Robot robot, int keyEvent) throws InterruptedException {
		robot.keyPress(keyEvent);
		Thread.sleep(900);
		robot.keyRelease(keyEvent);
	}

	private void attack(Robot robot, int x, int y, boolean random) {
		robot.mouseMove(x, y);
		robot.mousePress(16);
		robot.mouseRelease(16);
		try {
			if (!random) {
				robot.delay(delay);
			} else {
				int[] rand = new int[] { delay, delay + delay, 10000, 16000, 8000, delay,  5000, delay + delay, 7000, 6000, delay, 15000, 14000, 13000, delay, 11000, delay + delay, 9000, 4000, 19000, delay };
				robot.delay(rand[(int)Math.round(Math.random()*19)]);
			}
		}catch (Exception e) {
			robot.delay(delay);
		}
	}
	
	private boolean isDateToFinish(int min, int hour, int day) {
		Calendar cal = new GregorianCalendar();
		int mmin = min, hhour = hour, dday = day;
		if (hhour == cal.get(Calendar.HOUR_OF_DAY) && dday == cal.get(Calendar.DAY_OF_MONTH) && mmin == cal.get(Calendar.MINUTE))
			return true;
		else
			return false;
		
	}

}
