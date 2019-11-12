
import java.awt.BorderLayout;

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

public class Canvas extends GLCanvas implements GLEventListener
{

   private static final long serialVersionUID = 1L;

   private GLU glu;

   private int fps = 60;

   private FPSAnimator animator;

   public Canvas(GLCapabilities capabilities, int width, int height)
   {
      addGLEventListener(this);
   }

   private static GLCapabilities createGLCapabilities()
   {
      GLCapabilities capabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
      capabilities.setRedBits(8);
      capabilities.setBlueBits(8);
      capabilities.setGreenBits(8);
      capabilities.setAlphaBits(8);
      return capabilities;
   }

   public void init(GLAutoDrawable drawable)
   {
      drawable.setGL(new DebugGL2(drawable.getGL().getGL2()));
      final GL gl = drawable.getGL();

      // Enable z- (depth) buffer for hidden surface removal.
      gl.glEnable(GL.GL_DEPTH_TEST);
      gl.glDepthFunc(GL.GL_LEQUAL);

      // Enable smooth shading.
      gl.getGL2().glShadeModel(GL2.GL_SMOOTH);

      // Define "clear" color.
      gl.glClearColor(0f, 0f, 0f, 0f);

      // We want a nice perspective.
      gl.getGL().glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST);

      // Create GLU.
      glu = new GLU();

      // Start animator.
      animator = new FPSAnimator(this, fps);
      animator.start();
   }

   public void display(GLAutoDrawable drawable)
   {
      if (!animator.isAnimating())
      {
         return;
      }
      final GL gl = drawable.getGL();

      // Clear screen.
      gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

      // Set camera.
      setCamera(gl, glu, 50);

      // Prepare light parameters.
      float SHINE_ALL_DIRECTIONS = 1;
      float[] lightPos = { -30, 0, 0, SHINE_ALL_DIRECTIONS };
      float[] lightColorAmbient = { 0.2f, 0.2f, 0.2f, 1f };
      float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1f };

      // Set light parameters.
      gl.getGL2().glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
      gl.getGL2().glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
      gl.getGL2().glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

      // Enable lighting in GL.
      gl.glEnable(GL2.GL_LIGHT1);
      gl.glEnable(GL2.GL_LIGHTING);

      // Set material properties.
      float[] rgba = { 0.3f, 0.5f, 1f };
      gl.getGL2().glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
      gl.getGL2().glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
      gl.getGL2().glMaterialf(GL2.GL_FRONT, GL2.GL_SHININESS, 0.5f);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric earth = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
      glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
      final float radius = 6.378f;
      final int slices = 16;
      final int stacks = 16;
      glu.gluSphere(earth, radius, slices, stacks);
      glu.gluDeleteQuadric(earth);
      
   }

   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
   {
      final GL gl = drawable.getGL();
      gl.glViewport(0, 0, width, height);
   }

   public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged)
   {
      throw new UnsupportedOperationException("Changing display is not supported.");
   }

   private void setCamera(GL gl, GLU glu, float distance)
   {
      // Change to projection matrix.
      gl.getGL2().glMatrixMode(GL2.GL_PROJECTION);
      gl.getGL2().glLoadIdentity();

      // Perspective.
      float widthHeightRatio = (float) getWidth() / (float) getHeight();
      glu.gluPerspective(45, widthHeightRatio, 1, 1000);
      glu.gluLookAt(0, 0, distance, 0, 0, 0, 0, 1, 0);

      // Change back to model view matrix.
      gl.getGL2().glMatrixMode(GL2.GL_MODELVIEW);
      gl.getGL2().glLoadIdentity();
   }

   public final static void main(String[] args)
   {
      GLCapabilities capabilities = createGLCapabilities();
      Canvas canvas = new Canvas(capabilities, 800, 500);
      JFrame frame = new JFrame("CG - OPENGL");
      frame.getContentPane().add(canvas, BorderLayout.CENTER);
      frame.setSize(800, 500);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
      canvas.requestFocus();
   }

   public void dispose(GLAutoDrawable arg0)
   {

   }

}