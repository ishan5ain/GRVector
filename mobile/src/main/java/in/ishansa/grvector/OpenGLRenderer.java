package in.ishansa.grvector;

import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

/**
 * Created by gufra27 on 22/11/16.
 */

public class OpenGLRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "OpenGLRenderer";
    private Cube mCube = new Cube();
    private float xRot;
    private float yRot;
    private float zRot;
    private float mCubeRotation;


    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        Log.d(TAG, "onSurfaceCreated: In");
        gl10.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

        gl10.glClearDepthf(1.0f);
        gl10.glEnable(GL10.GL_DEPTH_TEST);
        gl10.glDepthFunc(GL10.GL_LEQUAL);

        gl10.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST);

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: Surface is changing");
        gl10.glViewport(0, 0, width, height);
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        GLU.gluPerspective(gl10, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
        gl10.glViewport(0, 0, width, height);

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glLoadIdentity();

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        Log.d(TAG, "onDrawFrame: Redrawing...");
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl10.glLoadIdentity();

        gl10.glTranslatef(0.0f, 0.0f, -10.0f);
//        gl10.glRotatef(mCubeRotation, 1.0f, 1.0f, 1.0f);
        try {
            gl10.glRotatef(xRot, 1.0f, 0.0f, 0.0f);
            gl10.glRotatef(yRot, 0.0f, 1.0f, 0.0f);
            gl10.glRotatef(zRot, 0.0f, 0.0f, 1.0f);
        } catch (NullPointerException e){
            Log.d(TAG, "onDrawFrame: Null Pointer Exception in xRot, yRot, zRot");
            e.printStackTrace();
        }

        mCube.draw(gl10);

        gl10.glLoadIdentity();

//        mCubeRotation -= 0.50f;

    }

    public void setXYZRot(float x, float y, float z){
        Log.d(TAG, "setXYZRot: Setting Raw x, y, z ");
        this.xRot = x;
        this.yRot = y;
        this.zRot = z;
    }
}
