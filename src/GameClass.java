import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class GameClass extends ApplicationAdapter {

    // draw
    SpriteBatch batch;
    Texture red, green;
    FrameBuffer frameBuffer;
    // camera
    OrthographicCamera camera;
    // control
    Vector2 direction;
    final float speed = 300;

    private final Matrix4 originalMatrixTemp = new Matrix4();
    private static final Matrix4 IDENTITY = new Matrix4();


    @Override
    public void create() {

        red = new Texture(Gdx.files.internal("res/red.png"));
        green = new Texture(Gdx.files.internal("res/green.png"));
        batch = new SpriteBatch();

        // framebuffer the size of the screen
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,Settings.SCREEN_W,Settings.SCREEN_H,false);

        // camera
        camera = new OrthographicCamera(Settings.SCREEN_W, Settings.SCREEN_H);
        camera.position.set(Settings.SCREEN_W/2f,Settings.SCREEN_H/2f,0);
        camera.update();

        // basic input (w,a,s,d)
        direction = new Vector2();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                        direction.set(0,1);
                        break;
                    case Input.Keys.A:
                        direction.set(-1,0);
                        break;
                    case Input.Keys.D:
                        direction.set(1,0);
                        break;
                    case Input.Keys.S:
                        direction.set(0,-1);
                        break;
                }
                return super.keyDown(keycode);
            }
            public boolean keyUp(int keycode) {
                switch (keycode) {
                    case Input.Keys.W:
                        direction.set(0,0);
                    case Input.Keys.A:
                        direction.set(0,0);
                    case Input.Keys.D:
                        direction.set(0,0);
                    case Input.Keys.S:
                        direction.set(0,0);
                }
                return super.keyDown(keycode);
            }
        });
    }



    @Override
    public void render() {
        // basic controls
        float dt = Gdx.graphics.getDeltaTime();
        camera.translate(direction.x*speed*dt, direction.y*speed*dt,0);
        camera.update();

        // clear the screen, set batch's projection matrix
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);

        // draw some arbitrary textures as layer 0
        batch.begin();
        batch.draw(green,256,0);
        batch.draw(green,512,0);

        batch.flush(); // No need to call batch.end() / batch.begin()

        // magic. something about storing the original values of the batch before changing it.
        originalMatrixTemp.set(batch.getProjectionMatrix());
        int originalBlendSrcFunc = batch.getBlendSrcFunc();
        int originalBlendDstFunc = batch.getBlendDstFunc();

        // more sorcery. batch is changed
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);


        frameBuffer.begin(); // initialize framebuffer
        // clear the colors of the batch
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(red,0,0); // Draw another texture, now unto the framebuffer texture
        batch.flush(); // flush batch
        frameBuffer.end(); // end framebuffer

        batch.setColor(Color.WHITE);

        batch.setProjectionMatrix(IDENTITY); // magic
        // draw the framebuffers texture across all the screen (layer 1)
        batch.draw(frameBuffer.getColorBufferTexture(),-1, 1, 2, -2);
        //batch.draw(frameBuffer.getColorBufferTexture(),-0.5f, 0.5f, 1f, -1f);
        batch.flush();
        // restoring the original state
        batch.setProjectionMatrix(originalMatrixTemp);
        batch.setBlendFunction(originalBlendSrcFunc, originalBlendDstFunc);

        // drawing arbitrary layer 3
        batch.draw(green,256,256);
        batch.draw(green,256,512);

        batch.end(); // end of cycle
    }


    @Override
    public void dispose() {
        red.dispose();
        green.dispose();
        frameBuffer.dispose();
        batch.dispose();
    }
}
