package game.robotm.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import game.robotm.RobotM;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 480;
        config.height = 600;
		config.addIcon("icons/RobotM_Icon32x32.png", Files.FileType.Internal);
		new LwjglApplication(new RobotM(), config);
	}
}
