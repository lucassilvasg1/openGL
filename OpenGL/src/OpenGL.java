
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;

import com.jogamp.opengl.DebugGL2;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;


public class OpenGL extends GLCanvas implements GLEventListener {

    private static final long serialVersionUID = 1L;

    private GLU glu;

    private int fps = 60;

    private FPSAnimator animator;

    
    //textures here
    private Texture earthTexture;
    private Texture sunTexture;
    private Texture marsTexture;
    // planets rotation angles
    private float sunangle = 0;
    private float marsangle = 0;
    private float earthangle = 0;
    private float ufoangle = 0;
    private float satelliteAngle = 0;

    private Texture solarPanelTexture;

    public OpenGL(GLCapabilities capabilities, int width, int height) {
        addGLEventListener(this);
    }

    private static GLCapabilities createGLCapabilities() {
        GLCapabilities capabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
        capabilities.setRedBits(8);
        capabilities.setBlueBits(8);
        capabilities.setGreenBits(8);
        capabilities.setAlphaBits(8);
        return capabilities;
    }

    public void init(GLAutoDrawable drawable) {
        drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
        final GL2 gl = drawable.getGL().getGL2();

        // Enable z- (depth) buffer for hidden surface removal.
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LEQUAL);

        // Enable smooth shading.
        gl.glShadeModel(GL2.GL_SMOOTH);

        // Define "clear" color.
        gl.glClearColor(0f, 0f, 0f, 0f);

        // We want a nice perspective.
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);

        // Create GLU.
        glu = new GLU();

        // Load earth texture.
        try {
            InputStream stream = getClass().getResourceAsStream("earth_1024x512.jpg");
            TextureData data = TextureIO.newTextureData(GLProfile.get(GLProfile.GL2), stream, false, "jpg");
            earthTexture = TextureIO.newTexture(data);
        }
        catch (IOException exc) {
            exc.printStackTrace();
            System.exit(1);
        }

        // Load sun texture.
        try {
            InputStream stream = getClass().getResourceAsStream("sun.jpg");
            TextureData data = TextureIO.newTextureData(GLProfile.get(GLProfile.GL2), stream, false, "jpg");
            sunTexture = TextureIO.newTexture(data);
        }
        catch (IOException exc) {
            exc.printStackTrace();
            System.exit(1);
        }

        // Load mars texture.
        try {
            InputStream stream = getClass().getResourceAsStream("mars.jpeg");
            TextureData data = TextureIO.newTextureData(GLProfile.get(GLProfile.GL2), stream, false, "jpg");
            marsTexture = TextureIO.newTexture(data);
        }
        catch (IOException exc) {
            exc.printStackTrace();
            System.exit(1);
        }

        // Load the solar panel texture.
        try {
            InputStream stream = getClass().getResourceAsStream("solar_panel_256x32.png");
            TextureData data = TextureIO.newTextureData(GLProfile.get(GLProfile.GL2), stream, false, "png");
            solarPanelTexture = TextureIO.newTexture(data);
        }
        catch (IOException exc) {
            exc.printStackTrace();
            System.exit(2);
        }

        // Start animator.
        animator = new FPSAnimator(this, fps);
        animator.start();
    }

    public void display(GLAutoDrawable drawable) {
        if (!animator.isAnimating()) {
            return;
        }
        final GL2 gl = drawable.getGL().getGL2();

        // Clear screen.
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        // Set camera.
        setCamera(gl, glu, 60);

        // Prepare light parameters.
        float SHINE_ALL_DIRECTIONS = 1;
        float[] lightPos = {0f, 0f, 1f, SHINE_ALL_DIRECTIONS};
        float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
        float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};
        float[] lightDiffuse = { 1f, 0f, 0f, 0f };

        // Set light parameters.
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);
        gl.glLightfv( GL2.GL_LIGHT1, GL2.GL_DIFFUSE, lightDiffuse, 0 );

        // Enable lighting in GL.
        gl.glEnable(GL2.GL_LIGHT1);
        gl.glEnable(GL2.GL_LIGHTING);

        // Set material properties.
        float[] rgba = {1f, 1f, 1f};
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.5f);
        //-----------------------------------------------------TERRA-----------------------------------------------------
        // Apply texture.
        earthTexture.enable(gl);
        earthTexture.bind(gl);

        //EARTH Draw sphere (possible styles: FILL, LINE, POINT).
        GLUquadric earth = glu.gluNewQuadric();
        glu.gluQuadricTexture(earth, true);
        glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
        glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);



        //rotating and translating
        earthangle = earthangle+ ( (0.5f) % 360f);
        final float distance = 10.000f;
        final float xearth = (float) Math.sin(Math.toRadians(earthangle)) * (distance*10f) ;
        final float yearth = (float) Math.cos(Math.toRadians(earthangle)) * (distance*10f);
        final float zearth = 0;
        gl.glTranslatef(xearth, yearth, zearth);
        gl.glRotatef(earthangle, earthangle, earthangle, -1);





        final float radius = 6.378f;
        final int slices = 16;
        final int stacks = 16;
        glu.gluSphere(earth, radius, slices, stacks);
        glu.gluDeleteQuadric(earth);

        // Save old state.
        gl.glPushMatrix();
         //Removing Earth from Matrix
        gl.glPopMatrix();
        //-----------------------------------------------------SOL-----------------------------------------------------
    


        //reloading matrix
        gl.glLoadIdentity();


        // Apply texture.
        sunTexture.enable(gl);
        sunTexture.bind(gl);

        //rotate sun

        sunangle =earthangle/27;

        gl.glRotatef(earthangle, earthangle, earthangle, -1);



        //SUN Draw sphere (possible styles: FILL, LINE, POINT).

        GLUquadric sun = glu.gluNewQuadric();
        glu.gluQuadricTexture(sun, true);
        glu.gluQuadricDrawStyle(sun, GLU.GLU_FILL);
        glu.gluQuadricNormals(sun, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(sun, GLU.GLU_OUTSIDE);
    
        final float radiusSun = 50f;
        final int slicesSun = 16;
        final int stacksSun = 16;
        glu.gluSphere(sun, radiusSun, slicesSun, stacksSun);
        glu.gluDeleteQuadric(sun);
        // Save old state.
        gl.glPushMatrix();
        //--------------------------------------------------FIM SOL------------------------------------------------


        //-----------------------------------------------------Mars-----------------------------------------------------
        // Apply texture.


        //Removing sun from Matrix
        gl.glPopMatrix();

        gl.glLoadIdentity();


        // Compute mars position
        
        marsangle = earthangle /2;

        final float xMars = (float) Math.sin(Math.toRadians(satelliteAngle)) * distance*12.5f;
        final float yMars = (float) Math.cos(Math.toRadians(satelliteAngle)) * distance*12.5f;
        final float zMars = 0;
        gl.glTranslatef(xMars, yMars, zMars);
        gl.glRotatef(satelliteAngle, 0, 0, -1);


        marsTexture.enable(gl);
        marsTexture.bind(gl);
        //mars Draw sphere (possible styles: FILL, LINE, POINT).

        GLUquadric mars = glu.gluNewQuadric();
        glu.gluQuadricTexture(mars, true);
        glu.gluQuadricDrawStyle(mars, GLU.GLU_FILL);
        glu.gluQuadricNormals(mars, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(mars, GLU.GLU_OUTSIDE);
        final float radiusMars = 5;
        final int slicesMars = 16;
        final int stacksMars = 16;
        glu.gluSphere(mars, radiusMars, slicesMars, stacksMars);
        glu.gluDeleteQuadric(mars);
        // Save old state.
        gl.glPushMatrix();






//--------------------------------------------------FIM mars------------------------------------------------

//-----------------------------------------------------UFO-----------------------------------------------------
        // Apply texture.


        //Removing MARS from Matrix
        gl.glPopMatrix();

        gl.glLoadIdentity();

        gl.glColor3f(100, 50, 50);
        // Compute UFO position
        
        ufoangle = earthangle /2;

         final float xUfo = (float) Math.sin(Math.toRadians(ufoangle)) * distance*6f;
         final float yUfo = (float) Math.cos(Math.toRadians(ufoangle)) * distance*6f;
         final float zUfo = 0;
        gl.glTranslatef(xUfo, yUfo, 50);
    
         //rotate sun

         sunangle =earthangle/27;

         gl.glRotatef(ufoangle,ufoangle, ufoangle, -1);


        //ufoTexture.enable(gl);
        //ufoTexture.bind(gl);
        //mars Draw sphere (possible styles: FILL, LINE, POINT).
        final float ufoRadius = 5f;
        final float ufoHeight = 2f;
        final int ufoSlices = 16;
        final int ufoStacks = 16;
        GLUquadric ufo = glu.gluNewQuadric();
        glu.gluQuadricTexture(ufo, false);
        glu.gluQuadricDrawStyle(ufo, GLU.GLU_FILL);
        glu.gluQuadricNormals(ufo, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(ufo, GLU.GLU_OUTSIDE);
        gl.glTranslatef(0, 0, -ufoHeight / 2);
        glu.gluDisk(ufo, 0, ufoRadius, ufoSlices, 2);
        gl.glTranslatef(0, 0, ufoHeight);
        glu.gluDisk(ufo, 0, ufoRadius, ufoSlices, 2);
        glu.gluDeleteQuadric(ufo);
        gl.glTranslatef(0, 0, -ufoHeight / 2);
         // Save old state.
        gl.glPushMatrix();


       //--------------------------------------------------FIM UFO------------------------------------------------




        // --------------- Satelite--------------------------
        // Reseting position that was defined in sun so the satellite generates in 0,0,0
        gl.glLoadIdentity();

        // Compute satellite position.
        satelliteAngle = (satelliteAngle + 1f) % 360f;

        final float xsat = (float) Math.sin(Math.toRadians(satelliteAngle)) * distance;
        final float ysat = (float) Math.cos(Math.toRadians(satelliteAngle)) * distance;
        final float zsat = 0;
        gl.glTranslatef(xearth+xsat, yearth+ysat, zearth+zsat);
        gl.glRotatef(satelliteAngle, 0, 0, -1);

        // Set silver color, and disable texturing.
        gl.glDisable(GL.GL_TEXTURE_2D);
        float[] ambiColor = {0.3f, 0.3f, 0.3f, 1f};
        float[] specColor = {0.8f, 0.8f, 0.8f, 1f};
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, ambiColor, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, specColor, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 90f);

        // Draw satellite body.
        final float cylinderRadius = 1f;
        final float cylinderHeight = 2f;
        final int cylinderSlices = 16;
        final int cylinderStacks = 16;
        GLUquadric body = glu.gluNewQuadric();
        glu.gluQuadricTexture(body, false);
        glu.gluQuadricDrawStyle(body, GLU.GLU_FILL);
        glu.gluQuadricNormals(body, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(body, GLU.GLU_OUTSIDE);
        gl.glTranslatef(0, 0, -cylinderHeight / 2);
        glu.gluDisk(body, 0, cylinderRadius, cylinderSlices, 2);
        glu.gluCylinder(body, cylinderRadius, cylinderRadius, cylinderHeight, cylinderSlices, cylinderStacks);
        gl.glTranslatef(0, 0, cylinderHeight);
        glu.gluDisk(body, 0, cylinderRadius, cylinderSlices, 2);
        glu.gluDeleteQuadric(body);
        gl.glTranslatef(0, 0, -cylinderHeight / 2);

        // Set white color, and enable texturing.
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0f);
        
        // Draw solar panels.
        gl.glScalef(6f, 0.7f, 0.1f);
        solarPanelTexture.bind(gl);
        gl.glBegin(GL2.GL_QUADS);
        final float[] frontUL = {-1.0f, -1.0f, 1.0f};
        final float[] frontUR = {1.0f, -1.0f, 1.0f};
        final float[] frontLR = {1.0f, 1.0f, 1.0f};
        final float[] frontLL = {-1.0f, 1.0f, 1.0f};
        final float[] backUL = {-1.0f, -1.0f, -1.0f};
        final float[] backLL = {-1.0f, 1.0f, -1.0f};
        final float[] backLR = {1.0f, 1.0f, -1.0f};
        final float[] backUR = {1.0f, -1.0f, -1.0f};
        // Front Face.
        gl.glNormal3f(0.0f, 0.0f, 1.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3fv(frontUR, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3fv(frontUL, 0);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3fv(frontLL, 0);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3fv(frontLR, 0);
        // Back Face.
        gl.glNormal3f(0.0f, 0.0f, -1.0f);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3fv(backUL, 0);
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3fv(backUR, 0);
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3fv(backLR, 0);
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3fv(backLL, 0);
        gl.glEnd();
        gl.glPopMatrix();
        
//------------------- end satelite---------------------





    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        final GL gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height);
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
        throw new UnsupportedOperationException("Changing display is not supported.");
    }

    private void setCamera(GL2 gl, GLU glu, float distance) {
        // Change to projection matrix.
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        // Perspective.
        float widthHeightRatio = (float) getWidth() / (float) getHeight();
        glu.gluPerspective(45, widthHeightRatio, 1, 1000);
        glu.gluLookAt(30, 0, distance*6f, 0, 0, 0, 0, 1, 0);

        // Change back to model view matrix.
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public final static void main(String[] args) {
        GLCapabilities capabilities = createGLCapabilities();
        OpenGL canvas = new OpenGL(capabilities, 800, 500);
        JFrame frame = new JFrame("Computação Gráfica - OPEN GL");
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        canvas.requestFocus();

    }

    public void dispose(GLAutoDrawable drawable)
    {
    }

}