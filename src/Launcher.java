import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Launcher {

    public static void main(String[] args) {

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.useGL30 = false;
        config.pauseWhenMinimized = true;
        config.pauseWhenBackground = true;
        config.width = Settings.SCREEN_W;
        config.height = Settings.SCREEN_H;
        //config.addIcon();
        config.resizable = false;
        Application app = new LwjglApplication(new GameClass(), config);
        Gdx.app = app;
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }
}
