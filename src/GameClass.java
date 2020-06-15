import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class GameClass extends ApplicationAdapter {

    SpriteBatch batch;
    Texture red, green;
    FrameBuffer frameBuffer;
    TextureRegion bufferTexture;
    OrthographicCamera camera;
    Vector2 direction;
    Vector2 tmp;
    float speed = 300;
    private final Matrix4 originalMatrixTemp = new Matrix4();
    private static final Matrix4 IDENTITY = new Matrix4();

    public GameClass() {
        super();
    }

    @Override
    public void create() {
        direction = new Vector2();
        tmp = new Vector2();
        red = new Texture(Gdx.files.internal("res/red.png"));
        green = new Texture(Gdx.files.internal("res/green.png"));
        batch = new SpriteBatch();
        frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,Settings.SCREEN_W,Settings.SCREEN_H,false);
        bufferTexture = new TextureRegion(frameBuffer.getColorBufferTexture());
        camera = new OrthographicCamera(Settings.SCREEN_W, Settings.SCREEN_H);
        camera.position.set(Settings.SCREEN_W/2f,Settings.SCREEN_H/2f,0);
        camera.update();
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


        super.create();
    }



    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        camera.translate(direction.x*speed*dt, direction.y*speed*dt,0);
        camera.update();


        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        batch.draw(green,256,0);    batch.draw(green,512,0);


        batch.flush();


        originalMatrixTemp.set(batch.getProjectionMatrix());
        int originalBlendSrcFunc = batch.getBlendSrcFunc();
        int originalBlendDstFunc = batch.getBlendDstFunc();
        batch.setBlendFunctionSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_ONE);


        frameBuffer.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.draw(red,0,0);
        batch.flush();
        frameBuffer.end();

        batch.setColor(Color.WHITE);

        batch.setProjectionMatrix(IDENTITY);
        batch.draw(frameBuffer.getColorBufferTexture(),-1, 1, 2, -2);
        //batch.draw(frameBuffer.getColorBufferTexture(),-0.5f, 0.5f, 1f, -1f);
        batch.flush();
        batch.setProjectionMatrix(originalMatrixTemp);
        batch.setBlendFunction(originalBlendSrcFunc, originalBlendDstFunc);



        batch.draw(green,256,256);
        batch.draw(green,256,512);

        batch.end();

    }


    @Override
    public void dispose() {
        bufferTexture.getTexture().dispose();
        red.dispose();
        green.dispose();
        frameBuffer.dispose();
        batch.dispose();
        super.dispose();
    }
}
