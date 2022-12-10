
// By C.W. Bennett
// 05/24/02
//
// Best viewed with WordPad, no Wrap
//
// This Program create the the 3D scene and
// handles most of the user interaction
// Utilizes kitUI, and zomeUI
//////////////////////////////////////


// Java Core
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;

// Java Swing
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.JOptionPane;
import javax.swing.JDialog;

//Property change stuff
import java.beans.*; 

// Applet stuff
import java.applet.*;
import java.applet.Applet; 

// utility libraries
import java.util.Enumeration;
import java.util.Arrays;

// Java3D
import javax.media.j3d.*;
import javax.vecmath.*; 
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;

// URL handling
import java.net.URL;
import java.net.MalformedURLException;

public class zome3d extends JPanel implements ActionListener, MouseListener, MouseMotionListener
{
//-----------------------------------------------------------------------------------------------------
//  ATTRIBUTE CONSTANTS - One stop shop for changing the settings
//-----------------------------------------------------------------------------------------------------

  // the maximum number of components ( strut-balls or struts )
  private static final int MAXITEMS = 500;

  // These attributes should keep the same ratio (else the balls and struts will not
  // align properly)
  private static final double BALLSCALE       = 0.0316; // Size of the ball
  private static final int    STRUTSCALE      = 6;      // Size of the Strut
  private static final int    POSTSCALE       = 3;      // Scalar for the positions

  private static final double HIGHLIGHTSCALE  = 1.50;   // Use to set the size of the ball highlight

  // Utility Colors
  private static final  Color3f WHITE   = new Color3f(1.0f, 1.0f, 1.0f);
  private static final  Color3f LGREY   = new Color3f(8.0f, 8.0f, 8.0f); 
  private static final  Color3f GREY    = new Color3f(5.0f, 5.0f, 5.0f);
  private static final  Color3f BLACK   = new Color3f(0.0f, 0.0f, 0.0f);
  private static final  Color3f AMBIENT = new Color3f(0.2f, 0.2f, 0.2f);

  // Strut Colors
  private static final  Color3f BLUE    = new Color3f(0.0f, 0.0f, 1.0f);
  private static final  Color3f RED     = new Color3f(1.0f, 0.0f, 0.0f);
  private static final  Color3f YELLOW  = new Color3f(1.0f, 0.8f, 0.0f);

  // BackGround Colors
  private static final  Color3f SKY     = new Color3f(0.4f, 0.7f, 0.9f);
  private static final  Color3f DARKSKY = new Color3f(0.1f, 0.19f, 0.21f );

  // Highlight Colors
  private static final  Color3f LHIGHLIGHT = new Color3f(0.0f, 1.0f, 0.0f );
  private static final  Color3f DHIGHLIGHT = new Color3f(0.1f, 0.2f, 0.0f );

  // Light Color
  private static final  Color3f LIGHT = WHITE;

  private static final float BACKGROUNDSIZE   = 200.0f; // the size of the backgrouns sphere
  private static final float BACKGROUNDSHINE  = 64.0f;  // the magnitude of the background shine (0-128)
  private static final float ACTIVATIONRADIUS = 100.0f; // the activation radius of the view

  private static final int CANVASWIDTH  = 300;  // the intial size of the canvas
  private static final int CANVASHEIGHT = 300;  // these dont really matter as the canvas size is
                                                // Usurped by the frame layout

  // the main light direction
  private static final Vector3f LIGHTDIRECTION   = new Vector3f(  1.0f, -1.0f, -1.0f );

  // the location of the point light and its atenuation - unsused as of now
  private static final Point3f  LIGHTLOCATION    = new Point3f( -20.0f,-20.0f,-20.0f );
  private static final Point3f  LIGHTATTENUATION = new Point3f(   1.0f,  0.0f,  0.0f );

  private static final float PHANTOMTRANSPARENCY   = 0.6f; // transparency of phantom objects (0-1)
  private static final float HIGHLIGHTTRANSPARENCY = 0.6f; // transparency of phantom objects (0-1)
  private static final float HIGHLIGHTSIZE         = 0.6f; // Highlight size

  private static final int CREASEANGLE = 5; // controls how sharp the edges are on the ball and struts
                                            // a higher number will smooth the edges

//-----------------------------------------------------------------------------------------------------
// FINAL CONSTANTS - DO NOT CHANGE!!
//-----------------------------------------------------------------------------------------------------

  // the Golden Ratio and the Golden Ratio squared
  private static final double TAU  = 1.618034;
  private static final double TAU2 = 2.618034;
  private static final float  TAUF = 1.6180339887499f;

  // Strut widths
  private static final float  SIDE  = 0.0450849710000f;
  private static final float  SIDE2 = TAUF * SIDE;

  // Yellow Strut Constants
  private static final float  SHORTYELLOWLENGTH = .866025f;
  private static final float  YELLOWRADIUS      = .118308f;
  private static final float  COS60F            = .500000f;
  private static final float  SIN60F            = .866025f;

  // Red Strut Constants
  private static final float SHORTREDLENGTH = .951057f;
  private static final float REDRADIUS = .119581f;

  // X, Y, Z coordinates
  private static final int X = 0;
  private static final int Y = 1;
  private static final int Z = 2;

  // Shape constants
  private static final int RECT  = 0;
  private static final int TRI   = 1;
  private static final int PENT  = 2;

  // Length constants
  private static final int SHORT = 0;
  private static final int MED   = 1;
  private static final int LONG  = 2;  

  // the endpoints of a strut
  private static final int START  = 0;  
  private static final int END    = 1;

  // Used by addOrder
  private static final int BALL  = 1;
  private static final int STRUT = 2;  

  // The cardinal axes
  private static final Vector3d XAXIS = new Vector3d( 1.0, 0.0, 0.0 );
  private static final Vector3d YAXIS = new Vector3d( 0.0, 1.0, 0.0 );
  private static final Vector3d ZAXIS = new Vector3d( 0.0, 0.0, 1.0 );

  // phantom states
  public static final boolean  GHOST = true;
  public static final boolean  REAL  = false;

  // common strings
  public static final String HIGHLIGHT = new String("highlight");
  public static final String PHANTOM   = new String("phantom");

  // reset transform (used to clear transforms)
  public static final Transform3D RESET = new Transform3D();

  // Used to change the current kit message
  private static final String[] KITMESSAGE = { "Pioneer Kit!!!",
                                               "Adventurer Kit!!!",
                                               "Explorer Kit!!!",
                                               "Creator Kit!!!",
                                               "Advanced Math Kit!!!" };
 
  // The number of parts in each kit (advanced math is maxed so anything greater than the creator kit will
  // show advanced math kit)
  private static final int[][] KIT = { { 25,  16, 10,  6 }, {  50,  32,  20,  12 }, { 100, 64, 40, 24 },
                                       {200, 128, 80, 48 }, { 500, 500, 500, 500 } };

//-----------------------------------------------------------------------------------------------------
// Ball Positions (from origin)
// NOTE: All numbers should be halved if the base length is 1.0
// R = Rectangle T = Triangle P = Pentagon
// A = TAU component B = integer component
// S = Short M = Medium L = Long
//-----------------------------------------------------------------------------------------------------
  
  // Rectangle                             0          1          2          3          4          5 
  //                                    x  y  z    x  y  z    x  y  z    x  y  z    x  y  z    x  y  z 
  private static final int[][] ASR = {{ 0, 0, 0},{-1, 0, 1},{ 1, 0, 1},{ 1, 0, 1},{-1, 0, 1},{ 0, 1, 1},
                                      { 0, 1, 1},{ 1, 1, 0},{ 1,-1, 0},{ 0,-1, 1},{ 0,-1, 1},{-1,-1, 0},
                                      {-1, 1, 0},{ 0, 0, 0},{ 0, 0, 0},{ 0, 0, 0},{ 0, 0, 0},{ 0, 1,-1},
                                      { 1, 1, 0},{ 1,-1, 0},{ 0,-1,-1},{ 0,-1,-1},{-1,-1, 0},{-1, 1, 0},
                                      { 0, 1,-1},{ 1, 0,-1},{ 1, 0,-1},{-1, 0,-1},{-1, 0,-1},{ 0, 0, 0}};                                                                                                                              
  private static final int[][] BSR = {{ 0, 0, 2},{ 1, 1, 0},{-1, 1, 0},{-1,-1, 0},{ 1,-1, 0},{-1, 0,-1},
                                      { 1, 0,-1},{ 0,-1, 1},{ 0, 1, 1},{ 1, 0,-1},{-1, 0,-1},{ 0, 1, 1},
                                      { 0,-1, 1},{ 0, 2, 0},{ 2, 0, 0},{ 0,-2, 0},{-2, 0, 0},{ 1, 0, 1},
                                      { 0,-1,-1},{ 0, 1,-1},{ 1, 0, 1},{-1, 0, 1},{ 0, 1,-1},{ 0,-1,-1},
                                      {-1, 0, 1},{-1, 1, 0},{-1,-1, 0},{ 1,-1, 0},{ 1, 1, 0},{ 0, 0,-2}};                            
      
  private static final int[][] AMR = {{ 0, 0, 2},{ 0, 1, 1},{ 0, 1, 1},{ 0,-1, 1},{ 0,-1, 1},{-1, 1, 0},
                                      { 1, 1, 0},{ 1, 0, 1},{ 1, 0, 1},{ 1,-1, 0},{-1,-1, 0},{-1, 0, 1},
                                      {-1, 0, 1},{ 0, 2, 0},{ 2, 0, 0},{ 0,-2, 0},{-2, 0, 0},{ 1, 1, 0},
                                      { 1, 0,-1},{ 1, 0,-1},{ 1,-1, 0},{-1,-1, 0},{-1, 0,-1},{-1, 0,-1},
                                      {-1, 1, 0},{ 0, 1,-1},{ 0,-1,-1},{ 0,-1,-1},{ 0, 1,-1},{ 0, 0,-2}};                                  
  private static final int[][] BMR = {{ 0, 0, 0},{-1, 0, 1},{ 1, 0, 1},{ 1, 0, 1},{-1, 0, 1},{ 0, 1, 1},
                                      { 0, 1, 1},{ 1, 1, 0},{ 1,-1, 0},{ 0,-1, 1},{ 0,-1, 1},{-1,-1, 0},
                                      {-1, 1, 0},{ 0, 0, 0},{ 0, 0, 0},{ 0, 0, 0},{ 0, 0, 0},{ 0, 1,-1},
                                      { 1, 1, 0},{ 1,-1, 0},{ 0,-1,-1},{ 0,-1,-1},{-1,-1, 0},{-1, 1, 0},
                                      { 0, 1,-1},{ 1, 0,-1},{ 1, 0,-1},{-1, 0,-1},{-1, 0,-1},{ 0, 0, 0}};

  private static final int[][] ALR = {{ 0, 0, 2},{-1, 1, 2},{ 1, 1, 2},{ 1,-1, 2},{-1,-1, 2},{-1, 2, 1},
                                      { 1, 2, 1},{ 2, 1, 1},{ 2,-1, 1},{ 1,-2, 1},{-1,-2, 1},{-2,-1, 1},
                                      {-2, 1, 1},{ 0, 2, 0},{ 2, 0, 0},{ 0,-2, 0},{-2, 0, 0},{ 1, 2,-1},
                                      { 2, 1,-1},{ 2,-1,-1},{ 1,-2,-1},{-1,-2,-1},{-2,-1,-1},{-2, 1,-1},
                                      {-1, 2,-1},{ 1, 1,-2},{ 1,-1,-2},{-1,-1,-2},{-1, 1,-2},{ 0, 0,-2}};                                          
  private static final int[][] BLR = {{ 0, 0, 2},{ 0, 1, 1},{ 0, 1, 1},{ 0,-1, 1},{ 0,-1, 1},{-1, 1, 0},
                                      { 1, 1, 0},{ 1, 0, 1},{ 1, 0, 1},{ 1,-1, 0},{-1,-1, 0},{-1, 0, 1},
                                      {-1, 0, 1},{ 0, 2, 0},{ 2, 0, 0},{ 0,-2, 0},{-2, 0, 0},{ 1, 1, 0},
                                      { 1, 0,-1},{ 1, 0,-1},{ 1,-1, 0},{-1,-1, 0},{-1,-0,-1},{-1, 0,-1},
                                      {-1, 1, 0},{ 0, 1,-1},{ 0,-1,-1},{ 0,-1,-1},{ 0, 1,-1},{ 0, 0,-2}};

//-----------------------------------------------------------------------------------------------------

  // Triangle                              0          1          2          3          4   
  //                                    x  y  z    x  y  z    x  y  z    x  y  z    x  y  z    
  private static final int[][] AST = {{ 0, 1, 1},{ 0,-1, 1},{ 0, 0, 0},{ 0, 0, 0},{ 1, 0, 1},
                                      { 0, 0, 0},{ 0, 0, 0},{-1, 0, 1},{-1, 1, 0},{ 1, 1, 0},
                                      { 1,-1, 0},{-1,-1, 0},{ 0, 0, 0},{ 0, 0, 0},{ 1, 0,-1},
                                      { 0, 0, 0},{ 0, 0, 0},{-1, 0,-1},{ 0, 1,-1},{ 0,-1,-1}};         
  private static final int[][] BST = {{ 0,-1, 0},{ 0, 1, 0},{-1, 1, 1},{ 1, 1, 1},{ 0, 0,-1},
                                      { 1,-1, 1},{-1,-1, 1},{ 0, 0,-1},{ 1, 0, 0},{-1, 0, 0},
                                      {-1, 0, 0},{ 1, 0, 0},{-1, 1,-1},{ 1, 1,-1},{ 0, 0, 1},
                                      { 1,-1,-1},{-1,-1,-1},{ 0, 0, 1},{ 0,-1, 0},{ 0, 1, 0}};

  private static final int[][] AMT = {{ 0, 0, 1},{ 0, 0, 1},{-1, 1, 1},{ 1, 1, 1},{ 1, 0, 0},
                                      { 1,-1, 1},{-1,-1, 1},{-1, 0, 0},{ 0, 1, 0},{ 0, 1, 0},
                                      { 0,-1, 0},{ 0,-1, 0},{-1, 1,-1},{ 1, 1,-1},{ 1, 0, 0},
                                      { 1,-1,-1},{-1,-1,-1},{-1, 0, 0},{ 0, 0,-1},{ 0, 0,-1}}; 
  private static final int[][] BMT = {{ 0, 1, 1},{ 0,-1, 1},{ 0, 0, 0},{ 0, 0, 0},{ 1, 0, 1},
                                      { 0, 0, 0},{ 0, 0, 0},{-1, 0, 1},{-1, 1, 0},{ 1, 1, 0},
                                      { 1,-1, 0},{-1,-1, 0},{ 0, 0, 0},{ 0, 0, 0},{ 1, 0,-1},
                                      { 0, 0, 0},{ 0, 0, 0},{-1, 0,-1},{ 0, 1,-1},{ 0,-1,-1}};

  private static final int[][] ALT = {{ 0, 1, 2},{ 0,-1, 2},{-1, 1, 1},{ 1, 1, 1},{ 2, 0, 1},
                                      { 1,-1, 1},{-1,-1, 1},{-2, 0, 1},{-1, 2, 0},{ 1, 2, 0},
                                      { 1,-2, 0},{-1,-2, 0},{-1, 1,-1},{ 1, 1,-1},{ 2, 0,-1},
                                      { 1,-1,-1},{-1,-1,-1},{-2, 0,-1},{ 0, 1,-2},{ 0,-1,-2}};  
  private static final int[][] BLT = {{ 0, 0, 1},{ 0, 0, 1},{-1, 1, 1},{ 1, 1, 1},{ 1, 0, 0},
                                      { 1,-1, 1},{-1,-1, 1},{-1, 0, 0},{ 0, 1, 0},{ 0, 1, 0},
                                      { 0,-1, 0},{ 0,-1, 0},{-1, 1,-1},{ 1, 1,-1},{ 1, 0, 0},
                                      { 1,-1,-1},{-1,-1,-1},{-1, 0, 0},{ 0, 0,-1},{ 0, 0,-1}};

//-----------------------------------------------------------------------------------------------------

  // Pentagon:                             0          1          2          3    
  //                                    x  y  z    x  y  z    x  y  z    x  y  z    
  private static final int[][] ASP = {{ 0, 1, 0},{ 0, 0, 1},{ 0,-1, 0},{ 0, 0, 1},
                                      {-1, 0, 0},{ 1, 0, 0},{ 1, 0, 0},{-1, 0, 0},
                                      { 0, 1, 0},{ 0, 0,-1},{ 0,-1,-0},{ 0, 0,-1}};
  private static final int[][] BSP = {{ 0, 0, 1},{ 1, 0, 0},{ 0, 0, 1},{-1, 0, 0},
                                      { 0, 1, 0},{ 0, 1, 0},{ 0,-1, 0},{ 0,-1, 0},
                                      { 0, 0,-1},{ 1, 0, 0},{ 0, 0,-1},{-1, 0, 0}};

  private static final int[][] AMP = {{ 0, 1, 1},{ 1, 0, 1},{ 0,-1, 1},{-1, 0, 1},
                                      {-1, 1, 0},{ 1, 1, 0},{ 1,-1, 0},{-1,-1, 0},
                                      { 0, 1,-1},{ 1, 0,-1},{ 0,-1,-1},{-1, 0,-1}};
  private static final int[][] BMP = {{ 0, 1, 0},{ 0, 0, 1},{ 0,-1, 0},{ 0, 0, 1},
                                      {-1, 0, 0},{ 1, 0, 0},{ 1, 0, 0},{-1, 0, 0},
                                      { 0, 1, 0},{ 0, 0,-1},{ 0,-1, 0},{ 0, 0,-1}};

  private static final int[][] ALP = {{ 0, 2, 1},{ 1, 0, 2},{ 0,-2, 1},{-1, 0, 2},
                                      {-2, 1, 0},{ 2, 1, 0},{ 2,-1, 0},{-2,-1, 0},
                                      { 0, 2,-1},{ 1, 0,-2},{ 0,-2,-1},{-1, 0,-2}};
  private static final int[][] BLP = {{ 0, 1, 1},{ 1, 0, 1},{ 0,-1, 1},{-1, 0, 1},
                                      {-1, 1, 0},{ 1, 1, 0},{ 1,-1, 0},{-1,-1, 0},
                                      { 0, 1,-1},{ 1, 0,-1},{ 0,-1,-1},{-1, 0,-1}};

//-----------------------------------------------------------------------------------------------------
// Directional Data 
// VECTOR is a vector in the direction of the rotational axis, ANGLE is the angle rotated in radians
//-----------------------------------------------------------------------------------------------------

  // Rectangle:                              
  private static final double[] ANGLER = { 1.570796, 1.047198, 1.047198, 2.094395, 2.094395, 
                                           0.628319, 0.628319, 1.256664, 1.884956, 2.513274, 
                                           2.513274, 1.884956, 1.256664, 0.000000,-1.570796,
                                           3.141593, 1.570796,-0.628319,-1.256664,-1.884956,
                                          -2.513274,-2.513274,-1.884956,-1.256664,-0.628319,
                                          -1.047198,-2.094395,-2.094395,-1.047198,-1.570796 };

  private static final Vector3d RECT1 = new Vector3d( TAU2, 0.0, 1.0 );
  private static final Vector3d RECT2 = new Vector3d( TAU2, 0.0,-1.0 );
  private static final Vector3d RECT3 = new Vector3d(  1.0, 0.0, TAU );
  private static final Vector3d RECT4 = new Vector3d(  1.0, 0.0,-TAU );
  private static final Vector3d RECT5 = new Vector3d( TAU , 0.0, TAU2); 
  private static final Vector3d RECT6 = new Vector3d( TAU , 0.0,-TAU2);     

  private static final Vector3d[] VECTORR = { XAXIS, RECT1, RECT2, RECT2, RECT1, 
                                              RECT3, RECT4, RECT6, RECT6, RECT4,
                                              RECT3, RECT5, RECT5, ZAXIS, ZAXIS, 
                                              ZAXIS, ZAXIS, RECT3, RECT5, RECT5,
                                              RECT3, RECT4, RECT6, RECT6, RECT4, 
                                              RECT1, RECT1, RECT2, RECT2, XAXIS };

//-----------------------------------------------------------------------------------------------------

  // Triangle:                                
  private static final double[] ANGLET = { 1.205932, 1.935660, 0.955317,-0.955317, 
                                          -1.570796,-2.186276, 2.186276, 1.570796,
                                           0.364864,-0.364864,-2.776729, 2.776729,
                                           0.955317,-0.955317,-1.570796,-2.186276, 
                                           2.186276, 1.570796,-1.205932,-1.935660 };
  
  private static final Vector3d TRIG1 = new Vector3d( 1.0, 0.0, 1.0 );         
  private static final Vector3d TRIG2 = new Vector3d(-1.0, 0.0, 1.0 );
  private static final Vector3d TRIG3 = new Vector3d(-1.0, 0.0, TAU2);
  private static final Vector3d TRIG4 = new Vector3d( 1.0, 0.0, TAU2);
 
  private static final Vector3d[] VECTORT = { XAXIS, XAXIS, TRIG1, TRIG2, TRIG3, 
                                              TRIG2, TRIG1, TRIG4, ZAXIS, ZAXIS, 
                                              ZAXIS, ZAXIS, TRIG2, TRIG1, TRIG4, 
                                              TRIG1, TRIG2, TRIG3, XAXIS, XAXIS }; 
            

//-----------------------------------------------------------------------------------------------------

  // Pentagon                               
  private static final double[] ANGLEP = { 0.553574,-1.570796, 2.588018, 1.570796, 
                                           1.017222,-1.017222,-2.124370, 2.124370,
                                          -0.553574,-1.570796,-2.588018, 1.570796};

  private static final Vector3d PENT1 = new Vector3d(-TAU2, 0.0, TAU ); 
  private static final Vector3d PENT2 = new Vector3d( TAU2, 0.0, TAU ); 

  private static final Vector3d[] VECTORP = { XAXIS, PENT1, XAXIS, PENT2,
                                              ZAXIS, ZAXIS, ZAXIS, ZAXIS,  
                                              XAXIS, PENT2, XAXIS, PENT1};

//-----------------------------------------------------------------------------------------------------
// Spin Data 
// Some struts need to be rotated about their own axis to fit properly, the angle rotated in radians
//-----------------------------------------------------------------------------------------------------

  private static final double[] RSPIN = { 1.571,-0.728, 0.728, 0.000, 0.000, 1.100,
                                         -1.100, 0.000,-1.100, 0.000, 0.000, 1.100,
                                          0.000, 0.000, 1.571, 0.000, 1.571, 1.100, 
                                          0.000, 1.100, 0.000, 0.000,-1.100, 0.000, 
                                         -1.100,-0.728, 0.000, 0.000, 0.728, 1.571 };

  private static final double[] TSPIN = { 3.142, 0.000, 1.691,-1.691,-0.350, 
                                          0.120,-0.120, 0.350,-1.571, 1.571,
                                         -1.571, 1.571, 1.451,-1.451,-2.792,
                                          3.022,-3.022, 2.792, 0.000, 3.142 };

  private static final double[] PSPIN = { 3.142, 0.250, 0.000,-0.250,-0.300, 0.300,
                                         -0.300, 0.300, 0.000, 0.400, 3.142,-0.400 };

//-----------------------------------------------------------------------------------------------------
// Geometry Data
// for 3d objects
//-----------------------------------------------------------------------------------------------------  

  // Zome Ball:

  // ball coordinates
  private static final Point3f[] BALLC = {new Point3f( -2*TAUF-1, 10*TAUF+6, -3*TAUF-2 ),new Point3f( -3*TAUF-2,  8*TAUF+5, -6*TAUF-4 ),
                                          new Point3f(  2*TAUF+1, 10*TAUF+6, -3*TAUF-2 ),new Point3f(  3*TAUF+2,  8*TAUF+5, -6*TAUF-4 ),
                                          new Point3f(         0,  7*TAUF+4, -8*TAUF-5 ),new Point3f(  2*TAUF+1,-10*TAUF-6,  3*TAUF+2 ),
                                          new Point3f(  3*TAUF+2, -8*TAUF-5,  6*TAUF+4 ),new Point3f(         0, -7*TAUF-4,  8*TAUF+5 ),
                                          new Point3f( -3*TAUF-2, -8*TAUF-5,  6*TAUF+4 ),new Point3f( -2*TAUF-1,-10*TAUF-6,  3*TAUF+2 ),
                                          new Point3f( -3*TAUF-2, -2*TAUF-1,-10*TAUF-6 ),new Point3f( -3*TAUF-2,  2*TAUF+1,-10*TAUF-6 ),
                                          new Point3f( -6*TAUF-4, -3*TAUF-2, -8*TAUF-5 ),new Point3f( -8*TAUF-5,         0, -7*TAUF-4 ),
                                          new Point3f( -6*TAUF-4,  3*TAUF+2, -8*TAUF-5 ),new Point3f(  3*TAUF+2,  2*TAUF+1, 10*TAUF+6 ),
                                          new Point3f(  3*TAUF+2, -2*TAUF-1, 10*TAUF+6 ),new Point3f(  6*TAUF+4, -3*TAUF-2,  8*TAUF+5 ),
                                          new Point3f(  8*TAUF+5,         0,  7*TAUF+4 ),new Point3f(  6*TAUF+4,  3*TAUF+2,  8*TAUF+5 ),
                                          new Point3f( 10*TAUF+6, -3*TAUF-2,  2*TAUF+1 ),new Point3f( 10*TAUF+6, -3*TAUF-2, -2*TAUF-1 ),
                                          new Point3f(  8*TAUF+5, -6*TAUF-4,  3*TAUF+2 ),new Point3f(  7*TAUF+4, -8*TAUF-5,         0 ),
                                          new Point3f(  8*TAUF+5, -6*TAUF-4, -3*TAUF-2 ),new Point3f(-10*TAUF-6,  3*TAUF+2, -2*TAUF-1 ),
                                          new Point3f(-10*TAUF-6,  3*TAUF+2,  2*TAUF+1 ),new Point3f( -8*TAUF-5,  6*TAUF+4,  3*TAUF+2 ),
                                          new Point3f( -7*TAUF-4,  8*TAUF+5,         0 ),new Point3f( -8*TAUF-5,  6*TAUF+4, -3*TAUF-2 ),
                                          new Point3f(  3*TAUF+2, -2*TAUF-1,-10*TAUF-6 ),new Point3f(  6*TAUF+4, -3*TAUF-2, -8*TAUF-5 ),
                                          new Point3f(  3*TAUF+2,  2*TAUF+1,-10*TAUF-6 ),new Point3f(  6*TAUF+4,  3*TAUF+2, -8*TAUF-5 ),
                                          new Point3f(  8*TAUF+5,         0, -7*TAUF-4 ),new Point3f( -3*TAUF-2,  2*TAUF+1, 10*TAUF+6 ),
                                          new Point3f( -6*TAUF-4,  3*TAUF+2,  8*TAUF+5 ),new Point3f( -8*TAUF-5,         0,  7*TAUF+4 ),
                                          new Point3f( -6*TAUF-4, -3*TAUF-2,  8*TAUF+5 ),new Point3f( -3*TAUF-2, -2*TAUF-1, 10*TAUF+6 ),
                                          new Point3f( -8*TAUF-5, -6*TAUF-4,  3*TAUF+2 ),new Point3f( -7*TAUF-4, -8*TAUF-5,         0 ),
                                          new Point3f(-10*TAUF-6, -3*TAUF-2,  2*TAUF+1 ),new Point3f(-10*TAUF-6, -3*TAUF-2, -2*TAUF-1 ),
                                          new Point3f( -8*TAUF-5, -6*TAUF-4, -3*TAUF-2 ),new Point3f(  8*TAUF+5,  6*TAUF+4, -3*TAUF-2 ),
                                          new Point3f(  7*TAUF+4,  8*TAUF+5,         0 ),new Point3f(  8*TAUF+5,  6*TAUF+4,  3*TAUF+2 ),
                                          new Point3f( 10*TAUF+6,  3*TAUF+2,  2*TAUF+1 ),new Point3f( 10*TAUF+6,  3*TAUF+2, -2*TAUF-1 ),
                                          new Point3f(         0, -7*TAUF-4, -8*TAUF-5 ),new Point3f( -3*TAUF-2, -8*TAUF-5, -6*TAUF-4 ),
                                          new Point3f(  3*TAUF+2, -8*TAUF-5, -6*TAUF-4 ),new Point3f(  2*TAUF+1,-10*TAUF-6, -3*TAUF-2 ),
                                          new Point3f( -2*TAUF-1,-10*TAUF-6, -3*TAUF-2 ),new Point3f(         0,  7*TAUF+4,  8*TAUF+5 ),
                                          new Point3f(  3*TAUF+2,  8*TAUF+5,  6*TAUF+4 ),new Point3f(  2*TAUF+1, 10*TAUF+6,  3*TAUF+2 ),
                                          new Point3f( -2*TAUF-1, 10*TAUF+6,  3*TAUF+2 ),new Point3f( -3*TAUF-2,  8*TAUF+5,  6*TAUF+4 )};

  // Ball Strip Counts
  private static final int[] BALLSC = { 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 3, 3, 3, 3, 3, 3, 3, 3,
                                        3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 
                                        4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 };

  //Ball Coordinate Indices
  private static final int[] BALLCI = { 2, 3, 0, 4, 1, //P0+
                                        6, 7, 5, 8, 9, //P0-
                                       32,33,30,34,31, //P1+
                                       36,37,35,38,39, //P1-
                                       12,13,10,14,11, //P2+
                                       16,17,15,18,19, //P2-
                                       26,27,25,28,29, //P3+
                                       22,23,20,24,21, //P3-
                                       56,57,55,58,59, //P4+
                                       52,53,50,54,51, //P4-
                                       46,47,45,48,49, //P5+
                                       42,43,40,44,41, //P5-
                                        3,45,33,       //T0+
                                        8,38,40,       //T0-
                                       32,11, 4,       //T1+
                                       39, 7,16,       //T1-
                                        1,14,29,       //T2+
                                        6,22,17,       //T2-
                                       28,58, 0,       //T3+
                                       23, 5,53,       //T3-
                                       57,46, 2,       //T4+
                                       54, 9,41,       //T4-
                                       49,21,34,       //T5+
                                       42,37,26,       //T5-
                                       50,10,30,       //T6+
                                       55,35,15,       //T6-
                                       25,13,43,       //T7+
                                       20,48,18,       //T7-
                                       36,59,27,       //T8+
                                       31,24,52,       //T8-
                                       47,56,19,       //T9+
                                       44,12,51,       //T9-
                                       32, 4,33, 3,    //R0+
                                       38, 8,39, 7,    //R0-
                                       14, 1,11, 4,    //R1+
                                       16, 7,17, 6,    //R1-
                                       28, 0,29, 1,    //R2+
                                       57, 2,58, 0,    //R3+
                                       53, 5,54, 9,    //R3-
                                       45, 3,46, 2,    //R4+
                                       41, 9,40, 8,    //R4-
                                       49,34,45,33,    //R5+
                                       40,38,42,37,    //R5-
                                       30,10,32,11,    //R6+
                                       39,16,35,15,    //R6-
                                       13,25,14,29,    //R7+
                                       17,22,18,20,    //R7-
                                       27,59,28,58,    //R8+
                                       23,53,24,52,    //R8-
                                       56,47,57,46,    //R9+
                                       54,41,51,44,    //R9-
                                       48,20,49,21,    //R10+
                                       42,26,43,25,    //R10-
                                       24,31,21,34,    //R11+
                                       26,37,27,36,    //R11-
                                       31,52,30,50,    //R12+
                                       35,55,36,59,    //R12-
                                       51,12,50,10,    //R13+
                                       55,15,56,19,    //R13-
                                       12,44,13,43,    //R14+
                                       18,48,19,47,    //R14-
                                       22, 6,23, 5,    //R2-
                                      };

// Strut Geometry Data
//----------------------------------------------------------------------------------------------------- 

  // Blue strut face counts
  private static final int[] BLUESC = {4,4,4,4,4,4};

  // Blue Strut Coordinate Indices
  private static final int[] BLUECI = {4,5,7,6,
                                       1,5,0,4,
                                       0,4,3,7,
                                       7,6,3,2,
                                       2,6,1,5,
                                       3,2,0,1};

  // Yellow Strut Face Counts
  private static final int[] YELLOWSC = {3,4,4,4,3,4,4,4,3,3,3,3,3,3};

  // Yellow Strut Coordinate Indices
  private static final int[] YELLOWCI = {0, 2, 1,    
                                         0, 1, 3, 4, 
                                         0, 3, 2, 5,
                                         2, 5, 1, 4,
                                         9,10,11,
                                         6, 9, 8,11,
                                         8,11, 7,10,
                                         7,10, 6, 9,
                                         3, 7, 5,
                                         3, 8, 7,
                                         4, 8, 3,
                                         4, 6, 8,
                                         5, 6, 4,
                                         7, 6, 5};

  // Red Strut Face Counts
  private static final int[] REDSC = {5,4,4,4,4,4, 5,4,4,4,4,4, 3,3,3,3,3, 3,3,3,3,3};

  // Red Strut Coordinate Indices
  private static final int[] REDCI = { 1, 0, 2, 4, 3,
                                       0, 5, 4, 9,
                                       4, 9, 3, 8,
                                       3, 8, 2, 7,
                                       2, 7, 1, 6,
                                       1, 6, 0, 5,
                                      15,16,19,17,18,
                                      10,15,14,19,
                                      14,19,13,18,
                                      13,18,12,17,
                                      12,17,11,16,
                                      11,16,10,15,
                                       5,13,12,
                                       6,14,13,
                                       7,10,14,
                                       8,11,10,
                                       9,12,11,
                                      10, 7, 8,
                                      11, 8, 9,
                                      12, 9, 5,
                                      13, 5, 6,
                                      14, 6, 7};


//-----------------------------------------------------------------------------------------------------
// GLOBALS
//-----------------------------------------------------------------------------------------------------  

 
  // Arrays
  private static gold3d[] pointIndex = new gold3d[MAXITEMS];    // holds the locations of all endpoints
  private static int[][]  strutIndex = new int[MAXITEMS][2];    // holds the endpoint pair for each strut
  public static int strutCount[][] = new int[3][3];             // Strut Counter for the kitUI
  private static final int addOrder[][] = new int[MAXITEMS][3]; // Keeps the order and type of parts added
                                                                // used by undo function

  // BranchGroups
  public static BranchGroup scenebg       = null;  // main branch group
  private static TransformGroup movetg    = null;  // move transform (used to focus)
  private static TransformGroup zoomtg    = null;  // view transfrom (used to zoom)
  private static TransformGroup rotatetg  = null;  // rotation transform
  private static TransformGroup alphatg   = null;  // used by the rotation interpolator

  // control integers
  private static int current          =  0; // current ball
  public  static int currentKit       =  0; // current kit type
  private static int indexLength      =  0; // number of components (not counting origin ball)
  public  static int ballCount        =  1; // number of balls
  private static int oldcurrent       = -1; // the last picked position ( used by the mouseClicked listener )

  // holds the last mouse location during a mouse drag
  private int oldX = 0;
  private int oldY = 0; 

  // Swing parts
  private static JSlider zslider; // zoom slider
  private static JSlider xslider; // x rotate slider
  private static JSlider yslider; // y rotate slider

  private static JButton undoJB;   // undo button
  private static JButton focusJB;  // focus button
  private static JButton resetJB;  // reset button
  private static JButton rotateJB; // rotate button

  private static JFrame frame = null; // the Frame

  // user interface panel
  public static zomeUI ui = new zomeUI();
  public static kitUI kui = new kitUI();

  // Canvases
  private static PickCanvas pickCanvas = null; // canvas where picking is enabled
  private static Canvas3D c3d          = null; // 3d canvas for rendering

  // used to rotate the scene graph
  private static RotationInterpolator rotator = null; 


//-----------------------------------------------------------------------------------------------------
// INITIALIZATION FUNCTIONS
//-----------------------------------------------------------------------------------------------------  
  
// init
//-----------------------------------------------------------------------------------------------------
// initializes java3d

  public void init()
  {

  // initialize the all arrays to zero
  //---------------------------------------------------
    for ( int i = 0; i < MAXITEMS; i++ )
    {
      pointIndex[i] = new gold3d();
      addOrder[i][0] = 0;
      addOrder[i][1] = 0;
      addOrder[i][2] = 0;
      strutIndex[i][START] = 0;
      strutIndex[i][END]   = 0;
    }

    for ( int i = RECT; i <= PENT; i++ )
    {
      for ( int j = SHORT; j <= LONG; j++ )
      {
        strutCount[i][j] = 0;
      }
    }

  // Basics - This sets up the 3d universe
  //---------------------------------------------------
    // create universe
    VirtualUniverse universe = new VirtualUniverse();
    
    // create locale
    Locale locale = new Locale(universe);

    // create an infinite bounding sphere 
    BoundingSphere bounds = new BoundingSphere(new Point3d(0.0,0.0,0.0), Double.MAX_VALUE);

    // creates the root of the scene (main viewing tramsforms are added to this)  
    BranchGroup sceneroot = createSceneBG();

  // create the view platform
  //---------------------------------------------------
    // create a branch group to hold the view platform
    BranchGroup viewbg = new BranchGroup();

    ViewPlatform vp = new ViewPlatform();
    vp.setViewAttachPolicy( View.RELATIVE_TO_FIELD_OF_VIEW );
    vp.setActivationRadius( ACTIVATIONRADIUS );

    // move the view back (so the initial view isn't inside the ball)
    Transform3D viewZoom = new Transform3D();
    viewZoom.setScale( 1.0f );
    viewZoom.setTranslation( new Vector3d( 0.0, 0.0, 40.0 ) );

    // create the zoom TransformGroup and set intitial value
    zoomtg = new TransformGroup( viewZoom );
    zoomtg.setCapability( TransformGroup.ALLOW_TRANSFORM_READ );
    zoomtg.setCapability( TransformGroup.ALLOW_TRANSFORM_WRITE );      

    // wire it back to the view branch group
    zoomtg.addChild( vp );
    viewbg.addChild( zoomtg );

  // create the view
  //---------------------------------------------------
    View view = new View();

    // crap you have to do with every view
    PhysicalBody pb        = new PhysicalBody();
    PhysicalEnvironment pe = new PhysicalEnvironment();
    view.setPhysicalBody( pb );
    view.setPhysicalEnvironment( pe );
    view.attachViewPlatform( vp );

    // set the clip distances
    view.setBackClipDistance( 100 );
    view.setFrontClipDistance( 0.1 );

    // find the best graphics setting
    GraphicsConfigTemplate3D gc3D = new GraphicsConfigTemplate3D();
    gc3D.setSceneAntialiasing( GraphicsConfigTemplate.PREFERRED );

    //list of all the screen devices for the local graphics environment
    GraphicsDevice gd[] = GraphicsEnvironment.
                          getLocalGraphicsEnvironment().
                          getScreenDevices();

    // select the best configuration and create a canvas
    c3d = new Canvas3D( gd[0].getBestConfiguration( gc3D ) );

    // set the canvas size
   // c3d.setSize( CANVASWIDTH, CANVASHEIGHT );

    // attach the canvas to the view
    view.addCanvas3D( c3d );

  // wire everything back to the locale
  //---------------------------------------------------
    locale.addBranchGraph( sceneroot );
    locale.addBranchGraph( viewbg );

  } // end Init

//-------------------------------------------------------------------

  //Callback to allow the Canvas3D to be added to a Panel
  protected void addCanvas3D( Canvas3D c3d )
  {
    add( "center", c3d );
  }

// createSceneBG
//----------------------------------------------------------------------------------------------------- 
// creates the initial scenegraph

  protected BranchGroup createSceneBG()
  {
    // create the root node
    BranchGroup root = new BranchGroup();

    //Create an infinite spherical bounding volume that will define the volume.
    BoundingSphere bounds = new BoundingSphere( new Point3d(0.0,0.0,0.0), Double.MAX_VALUE );


  // create trasformgroups (used by the sliders, mouse rotate, and focus)
  //---------------------------------------------------
    rotatetg = new TransformGroup();
    rotatetg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    rotatetg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    movetg = new TransformGroup();
    movetg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    movetg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
 
  // create the rotation interpolator (spins the structure)
  //---------------------------------------------------
    // interpolator transfromgroup
    alphatg = new TransformGroup();
    alphatg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    alphatg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    //Create a transformation matrix for the interpolator
    Transform3D yAxis = new Transform3D();

    // alpha is a time function to make rotation automatic
    Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0, 4000, 0, 0, 0, 0, 0);

    // rotator controls the rotation effect
    rotator = new RotationInterpolator( rotationAlpha, alphatg, yAxis, 0.0f, (float) Math.PI*2.0f );

    //Set the scheduling bounds on the behavior. This defines the
    //volume within which this behavior will be active.
    rotator.setSchedulingBounds( bounds );
    rotator.setEnable(false);

    //Add the behavior to the scenegraph so that Java 3D
    //can schedule it for activation.
    alphatg.addChild(rotator);

  // initialize the main branchgroup off which all others will be built
  //-----------------------------------------------------------------------
    scenebg = new BranchGroup();

    //Allow the BranchGroup to have children added and removed at runtime
    scenebg.setCapability( Group.ALLOW_CHILDREN_EXTEND );
    scenebg.setCapability( Group.ALLOW_CHILDREN_READ );
    scenebg.setCapability( Group.ALLOW_CHILDREN_WRITE );


  // Create Origin Ball - that first ball in the middle of the sceen
  //---------------------------------------------------
    Vector3d origin = new Vector3d( 0, 0, 0 );
    Group originBall = createBall ( origin, REAL );
    scenebg.addChild( originBall );

    // highlight it
    Highlight();
     
  // Create lighting elements
  //---------------------------------------------------

  /* - These lights suck
    //Create an ambient light
    AmbientLight ambientLight = new AmbientLight( AMBIENT );
    ambientLight.setInfluencingBounds( bounds );

    // create a point light
    //PointLight pointLight = new PointLight( LGREY, LIGHTLOCATION, LIGHTATTENUATION );
    //pointLight.setInfluencingBounds( bounds );
  */ 

    //Create a directional light
    DirectionalLight directionLight = new DirectionalLight( LIGHT, LIGHTDIRECTION );
    directionLight.setInfluencingBounds( bounds );

    //Add the lights to the scenegraph
    //root.addChild(ambientLight);  // yuck!
    //root.addChild(pointLight);    // bleh!
    root.addChild(directionLight);


  // Create a background - A giant sphere which we are inside
  //---------------------------------------------------   
    // create a BranchGroup that will hold the background geometry
    BranchGroup bgGeometry = new BranchGroup();

    // create appearance for the background sphere
    Appearance appearance = new Appearance();
    appearance.setCapability(Appearance.ALLOW_MATERIAL_READ);
    appearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

    // set the attributes
    PolygonAttributes pa = new PolygonAttributes(PolygonAttributes.POLYGON_FILL, 
                                                 PolygonAttributes.CULL_BACK, 0.0f );
    RenderingAttributes ra = new RenderingAttributes(true, true, 0.0f, 
                                                     RenderingAttributes.ALWAYS, 
                                                     true, true, false, 
                                                     RenderingAttributes.ROP_COPY );
    appearance.setPolygonAttributes(pa);
    appearance.setRenderingAttributes(ra);

    // create a material for the appearance
    Material m = new Material(DARKSKY, DARKSKY, SKY, WHITE, BACKGROUNDSHINE );
    m.setCapability( Material.ALLOW_COMPONENT_READ);
    m.setCapability( Material.ALLOW_COMPONENT_WRITE);
    m.setLightingEnable(true);
    appearance.setMaterial(m);

    // create the sphere
    Sphere sphere = new Sphere( BACKGROUNDSIZE, 
                                Sphere.GENERATE_NORMALS_INWARD | 
                                Sphere.GENERATE_NORMALS , appearance );

    // add the sphere to a branchgroup
    bgGeometry.addChild( sphere );

  // Wire it all together
  //---------------------------------------------------
    movetg.addChild( scenebg );
    rotatetg.addChild( movetg );
    alphatg.addChild( rotatetg );
    root.addChild( alphatg );
    root.addChild( bgGeometry );
  
    //Return the root of the scene side of the scenegraph
    return root;

  } // end createSceneBG

//-----------------------------------------------------------------------------------------------------
// MAIN
//-----------------------------------------------------------------------------------------------------

  public zome3d(JFrame frame) 
  {
    // uses the provided frame
    this.frame = frame;

    // initialize java3d
    init();

    // set the frame layout
    frame.getContentPane().setLayout(new BorderLayout());

  // create the menu
  //-------------------------------------------------------------------
    JPopupMenu.setDefaultLightWeightPopupEnabled( false );
    ToolTipManager ttm = ToolTipManager.sharedInstance();
    ttm.setLightWeightPopupEnabled( false );

    JMenuBar menuBar = new JMenuBar();
    JMenu menu = null;

    //Create some menu items and add them to the JMenuBar
    menu = new JMenu( "File" );
    menu.add( createMenuItem( "File", "Clear", this ) );
    menu.add( createMenuItem( "File", "Exit", this ) );
    menuBar.add( menu );

    menu = new JMenu( "Add" );
    menu.add( createMenuItem( "Add", "Rectangle Burst", this ) );
    menu.add( createMenuItem( "Add", "Triangle Burst", this ) );
    menu.add( createMenuItem( "Add", "Pentagon Burst", this ) );
    menuBar.add( menu );

    menu = new JMenu( "Help" );
    menu.add( createMenuItem( "Help", "Instructions", this ) );
    menu.add( createMenuItem( "Help", "Tips", this ) );
    menu.add( createMenuItem( "Help", "About", this ) );
    menuBar.add( menu );

    //Assign the JMenuBar to the parent frame.
    frame.setJMenuBar( menuBar );

  // create the left bottom panel
  //-------------------------------------------------------------------
    // create border types
    Border raisedBevel = BorderFactory.createRaisedBevelBorder();
    Border etched = BorderFactory.createEtchedBorder();


    // the buttons
    undoJB = new JButton( "Undo" );
    undoJB.setBorder(etched);
    undoJB.addActionListener(this);
    undoJB.setToolTipText("Click to undo stick additions.");

    focusJB = new JButton( "     Focus     " );
    focusJB.setBorder(etched);
    focusJB.addActionListener(this);
    focusJB.setToolTipText( "Centers the view on the hightlighted ball." );

    resetJB = new JButton( "Re-Center" );
    resetJB.setBorder(etched);
    resetJB.addActionListener(this);
    resetJB.setToolTipText( "Re-centers the view on the first ball and brings rotations back to Zero." );

    // add buttons to panel
    JPanel buttonpanel = new JPanel();
    buttonpanel.setLayout( new BorderLayout() );
    buttonpanel.add( undoJB, BorderLayout.CENTER );
    buttonpanel.add( focusJB, BorderLayout.NORTH );   
    buttonpanel.add( resetJB, BorderLayout.SOUTH ); 

    // the y rotation slider
    yslider = new JSlider(JSlider.HORIZONTAL, -180, 180, 0);
    yslider.addChangeListener( new SliderListener() );
    yslider.setBorder(BorderFactory.createTitledBorder("Rotate") );
    yslider.setMajorTickSpacing(45);
    yslider.setMinorTickSpacing(15);
    yslider.setPaintTicks(true);
    yslider.setPaintLabels(true);
    yslider.setToolTipText("Rotates the model left and right.");

    // add buttonpanel and slider to panel
    JPanel ypanel = new JPanel();
    ypanel.setLayout( new BorderLayout() );
    ypanel.add(yslider, BorderLayout.CENTER);
    ypanel.add(buttonpanel, BorderLayout.WEST );
    ypanel.setBorder(raisedBevel);


  // create right bottom panel
  //------------------------------------------------------------------
    // the zoom slider
    zslider = new JSlider(JSlider.HORIZONTAL, 0, 200, 40);
    zslider.addChangeListener( new SliderListener() );
    zslider.setBorder(BorderFactory.createTitledBorder("Zoom") );
    zslider.setMajorTickSpacing(20);
    zslider.setMinorTickSpacing(5);
    zslider.setPaintTicks(false);
    zslider.setToolTipText("Zooms in and Out.");

    // the button
    rotateJB = new JButton( "Rotation On" );
    rotateJB.setBorder(etched);
    rotateJB.addActionListener(this);
    rotateJB.setToolTipText("Turns automatic rotation on.");
    rotateJB.setMnemonic(KeyEvent.VK_R);

    // add to panel
    JPanel zpanel = new JPanel();
    zpanel.setLayout( new BorderLayout() );
    zpanel.setBorder(raisedBevel);
    zpanel.add( zslider, BorderLayout.NORTH );
    zpanel.add( rotateJB, BorderLayout.SOUTH );   

  // wire both bottom panels together 
  //-------------------------------------------------------------------

    JPanel bottompanel = new JPanel();
    bottompanel.setLayout( new BorderLayout() );
    bottompanel.add( ypanel, BorderLayout.CENTER );
    bottompanel.add( zpanel, BorderLayout.EAST );
    bottompanel.add( kui, BorderLayout.SOUTH ); 

  // create the right panel
  //-------------------------------------------------------------------
    // the x slider
    xslider = new JSlider(JSlider.VERTICAL, -180, 180, 0);
    xslider.addChangeListener( new SliderListener() );
    xslider.setBorder(BorderFactory.createTitledBorder("Rotate") );
    xslider.setMajorTickSpacing(45);
    xslider.setMinorTickSpacing(15);
    xslider.setPaintTicks(true);
    xslider.setPaintLabels(true);
    xslider.setToolTipText("Rotates the model up and down.");

    // panel for the slider
    JPanel xpanel = new JPanel();
    xpanel.setLayout( new BorderLayout() );
    xpanel.add(xslider, BorderLayout.CENTER);
    xpanel.setBorder(raisedBevel);
   

    // panel for the canavs
    JPanel c3dp = new JPanel();
    c3dp.setLayout( new BorderLayout() );
    c3dp.add(c3d, BorderLayout.CENTER);
    c3dp.setBorder(raisedBevel);
    c3dp.setSize( 300, 300 );

 
  // add panels, canvas, and user interface to frame
  //-------------------------------------------------------------------

    frame.getContentPane().add( bottompanel, BorderLayout.SOUTH );
    frame.getContentPane().add( xpanel, BorderLayout.WEST  );
    frame.getContentPane().add( ui, BorderLayout.EAST );
    frame.getContentPane().add( c3dp, BorderLayout.CENTER );

  // set up the pick tool and add listeners for the canvas
  //-------------------------------------------------------------------
    c3d.addMouseListener( this );
    c3d.addMouseMotionListener( this );
    pickCanvas = new PickCanvas( c3d, scenebg );
    pickCanvas.setMode( PickTool.GEOMETRY );
    pickCanvas.setTolerance( 0.1f );
    c3d.setCursor( new Cursor( Cursor.HAND_CURSOR ) );

    frame.pack();

  } // end main

//-------------------------------------------------------------------

  //Helper method to creates a Swing JmenuItem and set the action
  //command to something we can distinguish while handling menu events.
  private JMenuItem createMenuItem( String menuText,
                                    String buttonText,
                                    ActionListener listener )
  {
    JMenuItem menuItem = new JMenuItem( buttonText );
    menuItem.addActionListener( listener );
    menuItem.setActionCommand( menuText + "|" + buttonText );
    return menuItem;
  }

// This would be used if the program could be run as an application
// to do later?
//-------------------------------------------------------------------
//
//  public static void main( String[] args )
//  {
//    zome3d zome = new zome3d(frame);
//  }


//----------------------------------------------------------------------------------------------------- 
// MOUSE LISTENER
//----------------------------------------------------------------------------------------------------- 

// mouseClicked
//-----------------------------------------------------------------------------------------------------
// checks for mouse clicks, used to pick balls

  public void mouseClicked(MouseEvent e)
  {
    // if it was a single click select the new ball
    if ( e.getClickCount() == 1 )
    {
      // get the ball
      pickCanvas.setShapeLocation( e );

      // get the closest ball
      PickResult pickResult = pickCanvas.pickClosest();
      if( pickResult != null )
      {
        // get the node
        Node actualNode = pickResult.getObject();

        // if it has user data, we are interested in it
        if( actualNode.getUserData() != null )
        {
          // set the old value ( in case of a double click )
          oldcurrent = current;

          // set the ball number as the current location
          current = Integer.parseInt( actualNode.getUserData().toString() );

          // highlight it
          Highlight(); 

          // update the user interface
          ui.frontBall.repaint();
          ui.backBall.repaint();
    
        } // end if
      } // end if
    } // end if

    // if it was a double click try to connect the balls
    else if ( e.getClickCount() > 1 )
    {
      // get the ball
      pickCanvas.setShapeLocation( e );

      // get the closest ball
      PickResult pickResult = pickCanvas.pickClosest();
      if( pickResult != null )
      {
        // get the node
        Node actualNode = pickResult.getObject();

        if( actualNode.getUserData() != null )
        {
          if ( oldcurrent >= 0 ) // if the old current is valid, change back to it
          {
             current = oldcurrent;
             Highlight();
             System.out.println( "current: " + current);
          }

          // get the ball number
          int ball = Integer.parseInt( actualNode.getUserData().toString() );
         
          // attempt a connection
          connectBall( ball );

          // turn off the oldcurrent
          oldcurrent = -1;

        } // end if actualnode
      } // end if pickresult
    } // end if clickcount
  } // end mouseClicked

// mousePressed
//-----------------------------------------------------------------------------------------------------
// sets oldC and oldY for mouseDragged

  public void mousePressed (MouseEvent e)
  {
    oldX = e.getX();
    oldY = e.getY(); 
  }

// other mouse stuff
//-----------------------------------------------------------------------------------------------------
// mouse events I don't care about atm

  public void mouseEntered (MouseEvent e) {}
  public void mouseExited  (MouseEvent e) {}
  public void mouseReleased(MouseEvent e) {}

//-----------------------------------------------------------------------------------------------------
// MOUSEMOTIONLISTENER
//----------------------------------------------------------------------------------------------------- 
  
// mouseDragged
//-----------------------------------------------------------------------------------------------------
// used to change rotation with the mouse
  
  public void mouseDragged (MouseEvent e)
  {
    // get the new coordinates
    int newX = e.getX(); 
    int newY = e.getY();

    // the angle in degrees is just the pixel differences
    int angleX = newX-oldX;
    int angleY = newY-oldY;

    // set the old values
    oldX = newX;
    oldY = newY;

  // process X change
  //---------------------------------------------------
    if ( angleX  != 0 )
    {
      // get the current slider value
      int oldAngleX = yslider.getValue();
   
      // add the change to it
      int newAngleX = angleX+oldAngleX;

      // if it goes out of bounds put it back
      if ( newAngleX > 180 )
        newAngleX -= 360;
      if ( newAngleX < -180 )
        newAngleX += 360;

      // change the slider
      // (doing this will enact the changeListener which will do the work)
      yslider.setValue( newAngleX );
      
    }

  // ditto for the Y rotation
  //---------------------------------------------------
    if ( angleY != 0 )
    {
      int oldAngleY = xslider.getValue();
      int newAngleY = angleY+oldAngleY;

      if ( newAngleY > 180 )
        newAngleY -= 360;
      if ( newAngleY < -180 )
        newAngleY += 360;

      xslider.setValue( newAngleY );
    }

  } // end mouseDragged

  // I don't care about this
  public void mouseMoved (MouseEvent event){}

//----------------------------------------------------------------------------------------------------- 
// CHANGELISTENER
//----------------------------------------------------------------------------------------------------- 
// processes slider changes

  // inner class
  class SliderListener implements ChangeListener
  {
    public void stateChanged( ChangeEvent event )
    {
      // which slider moved?
      JSlider source = (JSlider)event.getSource();

    // zoom slider
    //------------------------------------------------------
      if ( source == zslider )
      {
        // get the slider value
        double distance = (double)source.getValue();

        // create a new trasfrom matrix
        Transform3D zoom = new Transform3D();
        zoom.setTranslation( new Vector3d( 0, 0, distance) );

        // change the view transform
        zoomtg.setTransform(zoom);
      }

    //  rotation sliders
    //------------------------------------------------------
      else if ( source == yslider || source == xslider )
      {
        // reset the alpha transform
        // (the alpha transform and the sliders cannot interact atm) 
        alphatg.setTransform(RESET);

        // focus on the selected ball
        Transform3D move = new Transform3D();
        move.setTranslation( new Vector3d( -pointIndex[current].getX(), 
                                           -pointIndex[current].getY(),
                                           -pointIndex[current].getZ() ) );
        movetg.setTransform( move );

        // create rotation trasforms 
        Transform3D rotYTrans = new Transform3D();
        Transform3D rotXTrans = new Transform3D();

        // all rotation must be in radians
        rotYTrans.rotY( Math.toRadians(yslider.getValue()) );
        rotXTrans.rotX( Math.toRadians(xslider.getValue()) );

        // matrix multiplication to set both rotations at the same time
        Transform3D temp = new Transform3D(); 
        temp.mul(rotYTrans);
        temp.mul(rotXTrans);

        // update transform
        rotatetg.setTransform(temp);
 
      } // end rotation sliders
    } // end stateChanged
  } // end ChangeListener

//----------------------------------------------------------------------------------------------------- 
// Action Listener
//----------------------------------------------------------------------------------------------------- 

// actionPerformed
//----------------------------------------------------------------------------------------------------- 
// controls response to menu bar and button presses

  public void actionPerformed( ActionEvent ae )
  {
 
  // check for a button push first
  //-----------------------------------------------------------
    if ( ae.getActionCommand().equals("Undo")  )
    {
      undo(); // yup
    }  
    else if ( ae.getActionCommand().equals("     Focus     ")  )
    {
      focus(); // duh
    } 
    else if ( ae.getActionCommand().equals("Re-Center")  )
    {
      reset(); // guess what it does
    }
    else if ( ae.getActionCommand().equals("Rotation On")  )
    {
      // changes button text
      rotateJB.setText("Rotation Off");
      rotator.setEnable( true );
      rotateJB.setToolTipText("Turns atomatic rotation off.");

      // disables sliders (to prevent crappy flickering)
      xslider.setEnabled(false);
      yslider.setEnabled(false);
    } 
    else if ( ae.getActionCommand().equals("Rotation Off")  )
    {
      rotateJB.setText("Rotation On");
      rotator.setEnable( false );
      rotateJB.setToolTipText("Turns atomatic rotation on.");

      // re-enable sliders
      xslider.setEnabled(true);
      yslider.setEnabled(true);
    } 

  // it must've been a menu command
  //-----------------------------------------------------------
    else 
    {
      // breaks down the action command into two parts
      java.util.StringTokenizer toker = new java.util.StringTokenizer( ae.getActionCommand(), "|" );
      String menu = toker.nextToken();
      String command = toker.nextToken();

      if ( menu.equals( "File" ) )
      {
         if ( command.equals( "Exit" ) )
         {
           clear();
           frame.setVisible(false);
         }
         else if ( command.equals( "Clear" )  )
         {
           clear();
         }
      } // end if

      else if ( menu.equals( "Add" ) )
      {
        if ( command.equals( "Rectangle Burst" ) )
        {
          // play sound
          runZome3d.starBurstSound = runZome3d.soundList.getClip(runZome3d.starBurstString);
          if ( runZome3d.starBurstSound != null )
            runZome3d.starBurstSound.play();

          // create starburst
          for ( int i = 0; i < 30; i++ )
            addStrutBall( RECT,  zomeUI.getStrutSize() , i, REAL );
        }
        else if ( command.equals( "Triangle Burst" ) )
        {
          runZome3d.starBurstSound = runZome3d.soundList.getClip(runZome3d.starBurstString);
          if ( runZome3d.starBurstSound != null )
            runZome3d.starBurstSound.play();

          for ( int i = 0; i < 20; i++ )
            addStrutBall( TRI,  zomeUI.getStrutSize(), i, REAL );
        }
        else if ( command.equals( "Pentagon Burst" ) )
        {
          runZome3d.starBurstSound = runZome3d.soundList.getClip(runZome3d.starBurstString);
          if ( runZome3d.starBurstSound != null )
            runZome3d.starBurstSound.play();

          for ( int i = 0; i < 12; i++ )
            addStrutBall( PENT,  zomeUI.getStrutSize(), i, REAL );
        }
      }

      else if ( menu.equals( "Help" ) )
      {
        if ( command.equals( "Instructions" ) )
        {
          JFrame help = new JFrame();

          Container contentPane = help.getContentPane();
          contentPane.setLayout(new BorderLayout());

          //Create an editor pane.
          JEditorPane editorPane = createEditorPane(true);
          JScrollPane editorScrollPane = new JScrollPane(editorPane);
          editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          editorScrollPane.setMinimumSize(new Dimension(10, 10));

          contentPane.add(editorScrollPane, BorderLayout.CENTER );

          help.setSize(500, 500);
          help.setVisible(true);

        }
        else if ( command.equals( "Tips" ) )
        {
          JFrame help = new JFrame();

          Container contentPane = help.getContentPane();
          contentPane.setLayout(new BorderLayout());

          //Create an editor pane.
          JEditorPane editorPane = createEditorPane(false);
          contentPane.add(editorPane, BorderLayout.CENTER );
          JScrollPane editorScrollPane = new JScrollPane(editorPane);
          editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          editorScrollPane.setMinimumSize(new Dimension(10, 10));

          help.setSize(500, 500);
          help.setVisible(true);
        }
        // about box
        else if ( command.equals( "About" ) )
        {
          ImageIcon triarc = new ImageIcon( runZome3d.mines );
          JOptionPane.showMessageDialog( null,  
                                          "  Zome 3D version 1.0 \n "
                                        + " \n"
                                        + "  Designed by C.W. Bennett, Mike Wilson, \n"
                                        + "  Luke Misgen, Francisco Garcia, and Paul Hildebrandt\n"
                                        + " \n"
                                        + "  Coded by C.W Bennett and Mike Wilson \n"
                                        + "  Documentation by Francisco Garcia and Luke Misgen \n"
                                        + " \n"      
                                        + "  Special Thanks to Walter Venable, Will Ackle, \n"  
                                        + "  Scott Vorthmann, Steve Rogers, Marc Pelletier, \n"
                                        + "  and Paul Hildebrandt\n"  
                                        + " \n"        
                                        + "  Copyright (C) 2002 Zometool Inc. \n"
                                        + "  For Great Justice."
                                        + " \n",
                                          "Zome 3D ver 1.0",
                                          JOptionPane.PLAIN_MESSAGE,
                                          triarc );   
        }
      } // end else if
    } // end else 
  } // end actionPerformed

// helper functions for creating the JEditorPane
//-----------------------------------------------------------------------------------------------------

  // creates the editor pane and sets the URL
  private JEditorPane createEditorPane( boolean help)
  {
    String filename;

    if ( help )
      filename = new String("Help.rtf");
    else
      filename = new String("Tips.rtf");

    JEditorPane editorPane = new JEditorPane();
    editorPane.setEditable(false);
    String s = null;

    try
    {
      s = runZome3d.codeBase + filename;
      URL helpURL = new URL(s);
      displayURL(helpURL, editorPane);
    }
    catch (Exception e)
    {
      System.err.println("Couldn't create help URL: " + s);
    }

      return editorPane;
  }

  // Gets the URL
  private void displayURL(URL url, JEditorPane editorPane)
  {
    try 
    {
      editorPane.setPage(url);
    }
    catch (IOException e)
    {
      System.err.println("Attempted to read a bad URL: " + url);
    }
  }

//-----------------------------------------------------------------------------------------------------
// OBJECT CREATION FUNCTIONS
//-----------------------------------------------------------------------------------------------------

// createBall 
//-----------------------------------------------------------------------------------------------------
// creates a ball and moves it the given location

  private static BranchGroup createBall( Vector3d location, boolean phantom )
  {
    // holder for ball group
    BranchGroup bg = new BranchGroup();

  // Create a transform group node to scale and position the object.
  //-------------------------------------------------------------------
    Transform3D translate = new Transform3D();
    translate.setScale( BALLSCALE );

    // Transfrom to the ball to proper spot
    translate.setTranslation( location );

    // make the TransformGroop
    TransformGroup translatetg = new TransformGroup(translate);
    translatetg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    translatetg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
   
  // create appearance to attach to ball shape
  //-------------------------------------------------------------------
    Appearance appearance = new Appearance();
    appearance.setCapability(Appearance.ALLOW_MATERIAL_READ);
    appearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

    PolygonAttributes pa = new PolygonAttributes(PolygonAttributes.POLYGON_FILL, 
                                                 PolygonAttributes.CULL_BACK, 0.0f );
    RenderingAttributes ra = new RenderingAttributes(true, true, 0.0f, 
                                                     RenderingAttributes.ALWAYS, 
                                                     true, true, false, 
                                                     RenderingAttributes.ROP_COPY );
    appearance.setPolygonAttributes(pa);
    appearance.setRenderingAttributes(ra);

    // if the ball is phantom, make it transparent
    if ( phantom )
    {
      TransparencyAttributes ta = new TransparencyAttributes();
      ta.setTransparencyMode(TransparencyAttributes.BLENDED);
      ta.setTransparency(PHANTOMTRANSPARENCY);
      appearance.setTransparencyAttributes(ta);
    }

    // create a material for the appearance
    Material m = new Material(BLACK, BLACK, WHITE, BLACK, 0.0f );
    m.setCapability( Material.ALLOW_COMPONENT_READ);
    m.setCapability( Material.ALLOW_COMPONENT_WRITE);
    m.setLightingEnable(true);
    appearance.setMaterial(m);

  // get geometry info to calculate normals
  //-------------------------------------------------------------------
    GeometryInfo gi = new GeometryInfo( GeometryInfo.TRIANGLE_STRIP_ARRAY );
    gi.setCoordinateIndices( BALLCI );
    gi.setCoordinates( BALLC );
    gi.setStripCounts( BALLSC );

    // calculate normals
    NormalGenerator ng = new NormalGenerator();
    ng.setCreaseAngle((float) Math.toRadians(CREASEANGLE));
    ng.generateNormals(gi);

    // get geometry array back
    GeometryArray ball = gi.getIndexedGeometryArray(false);

  // create Shape3d node
  //-------------------------------------------------------------------
    Shape3D ballShape = new Shape3D(ball);
    ballShape.setAppearance(appearance);
    ballShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    ballShape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    ballShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    ballShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    ballShape.setCapability(Shape3D.ENABLE_PICK_REPORTING);

    // set the ball number
    ballShape.setUserData(""+indexLength);

    // allow picking to work
    pickCanvas.setCapabilities(ballShape, PickTool.INTERSECT_FULL);

    // add ball to transform group
    translatetg.addChild(ballShape);

    // add transform group to branch group holder
    bg.addChild(translatetg);

    return bg;

  } // end createBall

// createStrut
//----------------------------------------------------------------------------------------------------- 
// creates and orients a strut

  private static BranchGroup createStrut( int type, int size, int hole, boolean phantom )
  {
    double length; // temporary

    // holder for strut
    BranchGroup bg = new BranchGroup();

  // Create a transform group node to rotate the object.
  //-------------------------------------------------------------------
    Transform3D rotation = new Transform3D();
    Transform3D spin = new Transform3D();

    double angle = 0;               // angle of rotation
    Vector3d axis = new Vector3d(); // axis of rotation
    Material m = null;              // material for strut

    // select proper axis, material, spin and angle
    if ( type == RECT )
    {
       angle = ANGLER[hole];
       axis = VECTORR[hole];
       m = new Material(BLACK, BLACK, BLUE, WHITE, 80.0f); 
       spin.rotY(RSPIN[hole]);
    }
    else if ( type == TRI )
    {
       angle = ANGLET[hole];
       axis = VECTORT[hole];
       m = new Material(BLACK, BLACK, YELLOW, WHITE, 80.0f); 
       spin.rotY(TSPIN[hole]);
    }
    else if ( type == PENT )
    {
       angle = ANGLEP[hole];
       axis = VECTORP[hole];
       m = new Material(BLACK, BLACK, RED, WHITE, 80.0f); 
       spin.rotY(PSPIN[hole]);
    }       

    // create an axis angle
    AxisAngle4d aa = new AxisAngle4d( axis, angle );

    // set the rotation
    rotation.setRotation( aa );
    rotation.setScale( STRUTSCALE );

    // set the spin
    TransformGroup spintg = new TransformGroup( spin ); 

    // set transformgroup for rotation 
    TransformGroup rotg = new TransformGroup(rotation);
    rotg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    rotg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
   
  // create appearance to attach to strut shape
  //-------------------------------------------------------------------
    Appearance appearance = new Appearance();

    if ( phantom ) // if phantom make transparent
    {
      TransparencyAttributes ta = new TransparencyAttributes();
      ta.setTransparencyMode(TransparencyAttributes.BLENDED);
      ta.setTransparency(PHANTOMTRANSPARENCY);
      appearance.setTransparencyAttributes(ta);
    }
 
    // set material for the appearance       
    m.setLightingEnable(true) ;
    appearance.setMaterial(m) ;

  // get geometry info to calculate normals
  //-------------------------------------------------------------------   
    GeometryInfo gi    = createStrutGeom( type, size ); 
    NormalGenerator ng = new NormalGenerator(); 
    ng.setCreaseAngle((float) Math.toRadians(CREASEANGLE));
    ng.generateNormals(gi); 

    GeometryArray strutGeom = gi.getIndexedGeometryArray(false);

  // create Shape3d node
  //-------------------------------------------------------------------
    Shape3D strutshape = new Shape3D(strutGeom);
    strutshape.setAppearance(appearance);
    strutshape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    strutshape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
    strutshape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    strutshape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    strutshape.setPickable(false);

    spintg.addChild(strutshape);
    rotg.addChild(spintg);

    bg.addChild(rotg);
   
    return bg;

  } // end createStrut  

// addStrutBall
//----------------------------------------------------------------------------------------------------- 
// creates a ball and strut component

  public static void addStrutBall( int type, int size, int hole, boolean phantom )
  {
    // if there is a strut already in that hole, forget about it
    if ( !isStrut( type, hole ) )
    {
      // finds out if there is another ball that will be reached
      int anotherBall = isBall(type, size, hole);

      if ( anotherBall != -1 && !phantom )
      {
        // if so, just add a strut and not a ball
        addStrut( type, size, hole, anotherBall );
      }
      else // this adds the ball
      {
        // create a detachable group to hold the component
        BranchGroup bg = new BranchGroup();
        bg.setCapability( BranchGroup.ALLOW_DETACH );

        // if the strut is phantom leave the the counters alone
        if ( !phantom )
        {
          strutIndex[indexLength][START] = current;
          strutIndex[indexLength][END]   = indexLength+1;

          strutCount[type][size]++;
          addOrder[indexLength+1][0] = BALL;
          addOrder[indexLength+1][1] = type;
          addOrder[indexLength+1][2] = size;
          ballCount++;
        }

        indexLength++; // adding a ball

        // set location for ball
        pointIndex[indexLength] = setGold( size, type, hole );

      // Create ball and strut and holder for them both   
      //-------------------------------------------------------------------
        BranchGroup newBall = createBall( pointIndex[indexLength].vector3d(), phantom );
        BranchGroup newStrut = createStrut( type, size, hole, phantom );
        BranchGroup newComponent = new BranchGroup();

        // add strut and ball to component
        newComponent.addChild(newBall);
        newComponent.addChild(newStrut); 

      // move ball and strut to proper location
      //-------------------------------------------------------------------
   
        // calculate the next point
        pointIndex[indexLength].add(pointIndex[current]);
    
        // set a translation to move from origin to new point
        Transform3D componentTrans = new Transform3D();
        componentTrans.setTranslation( pointIndex[current].vector3d() );

        // create transform group and add component to it
        TransformGroup compTransTG = new TransformGroup( componentTrans );
        compTransTG.addChild( newComponent );

        // add transform to holder
        bg.addChild( compTransTG );
     
        if ( phantom ) // if the strut created was a phantom one, decrement the indexLength
        {
          bg.setUserData( PHANTOM ); // tag it for deletion
          indexLength--;
        }
        else
        {
          bg.setUserData( "component" + indexLength ); // tag it
  
          // set the kit
          setKit();
        }

        scenebg.addChild(bg); // add new branchgroup to main

        // update the user interface
        ui.frontBall.repaint();
        ui.backBall.repaint();
  
      } // end else
    } // end if 
  } // end addStrutBall

// addStrut
//----------------------------------------------------------------------------------------------------- 
// creates a strut and places it - uses when connecting balls

  public static void addStrut( int type, int size, int hole, int ball )
  {
    BranchGroup bg = new BranchGroup();
    bg.setCapability( BranchGroup.ALLOW_DETACH );

    // set indices
    strutIndex[indexLength][START] = current;
    strutIndex[indexLength][END]   = ball;
    indexLength++; 

    strutCount[type][size]++;

    addOrder[indexLength][0] = STRUT;
    addOrder[indexLength][1] = type;
    addOrder[indexLength][2] = size;

    // detachable branchgroup
    BranchGroup newStrut = createStrut( type, size, hole, REAL );
    BranchGroup newComponent = new BranchGroup();

    // add strut to component
    newComponent.addChild(newStrut); 

  // move strut to proper location
  //-------------------------------------------------------------------
   
    // calculate the next point
    pointIndex[indexLength].add(pointIndex[current]);
    
    // set a translation to move from origin to new point
    Transform3D componentTrans = new Transform3D();
    componentTrans.setTranslation( pointIndex[current].vector3d() );

    // create transform group and add component to it
    TransformGroup compTransTG = new TransformGroup( componentTrans );
    compTransTG.addChild( newComponent );

    // add transform to holder
    bg.addChild( compTransTG );   
    bg.setUserData( "component" + indexLength ); // tag it

    // set the kit
    setKit();

    // add to the picture
    scenebg.addChild(bg); 

    // update UI
    ui.frontBall.repaint();
    ui.backBall.repaint();
 
  } // end addStrut

// connectBall 
//----------------------------------------------------------------------------------------------------- 
// creates a strut between the current ball and given ball

  public void connectBall( int ball )
  {
    // stays zero if there is no connection
    int none = 0;

    // holder for the different ball possibilities
    gold3d temp = new gold3d();

    // test for rectangle struts
    for ( int i = 0; i < 30; i ++ ) // for every hole
    {
      for ( int j = 0; j < 3; j++ ) // for every length
      {
        // get the endpoint location
        temp = setGold( j, RECT, i ); 
        temp.add(pointIndex[current]);

        // if its equal to the ball location, add a strut
        if ( gold3d.isEqual(temp, pointIndex[ball])) 
        {
          if ( !isStrut( RECT, i ) )
          {
            addStrut( RECT, j, i, ball );  
            none = 1;
          }
        } // end if
      } // end for
    } // end for

    // test for triangles
    for ( int i = 0; i < 20; i ++ ) // for every ball
    {
      for ( int j = 0; j < 3; j++ ) // for every length
      {
        // get the endpoint location
        temp = setGold( j, TRI, i ); 
        temp.add(pointIndex[current]);

        // if its equal to the ball location, add a strut
        if ( gold3d.isEqual(temp, pointIndex[ball])) 
        {
          if ( !isStrut( TRI, i ) )
          {
            addStrut( TRI, j, i, ball );  
            none = 1;
          }
        } // end if
      } // end for
    } // end for

    // test for pentagons
    for ( int i = 0; i < 12; i ++ )
    {  
      for ( int j = 0; j < 3; j++ ) // for every length
      {
        // get the endpoint location
        temp = setGold( j, PENT, i ); 
        temp.add(pointIndex[current]);

        // if its equal to the ball location, add a strut
        if ( gold3d.isEqual(temp, pointIndex[ball])) 
        {
          if ( !isStrut( PENT, i ) )
          {
            addStrut( PENT, j, i, ball );
            none = 1;
          }
        } // end if  
      } // end for
    } // end for

    // play sounds
    if ( none == 0 )
    {
      // there was no connection
      runZome3d.errorSound = runZome3d.soundList.getClip(runZome3d.errorString);
      runZome3d.errorSound.play();
    }
    else if ( none == 1 )
    {
      // yay, a connection!
      runZome3d.addStrutSound = runZome3d.soundList.getClip(runZome3d.addStrutString);
      runZome3d.addStrutSound.play();
    }

    // update UI
    ui.frontBall.repaint();
    ui.backBall.repaint();

  } // end connectBall

// Highlight 
//----------------------------------------------------------------------------------------------------- 
// creates a translucent sphere to highlight the current ball
// I didn't feel like moving one sphere all over the place, so this just destroys the old one and creates 
// a new one -- To do later, move one sphere

  private static void Highlight()
  {
    // remove the last highlight
    remove( HIGHLIGHT );

    // create a detachable holder
    BranchGroup bg = new BranchGroup();
    bg.setCapability( BranchGroup.ALLOW_DETACH );
    bg.setUserData( HIGHLIGHT );

  // create transform to place ball correctly
  //-------------------------------------------------------------------
    // Create a transform group node to scale and position the object.
    Transform3D translate = new Transform3D();
    translate.setScale( HIGHLIGHTSCALE );
    translate.setTranslation( pointIndex[current].vector3d() );

    TransformGroup translatetg = new TransformGroup(translate);
    translatetg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    translatetg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

  // create appearance to attach to ball shape
  //-------------------------------------------------------------------
    Appearance appearance = new Appearance();
    appearance.setCapability(Appearance.ALLOW_MATERIAL_READ);
    appearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

    PolygonAttributes pa = new PolygonAttributes(PolygonAttributes.POLYGON_FILL, 
                                                 PolygonAttributes.CULL_BACK, 0.0f );
    RenderingAttributes ra = new RenderingAttributes(true, true, 0.0f, 
                                                     RenderingAttributes.ALWAYS, 
                                                     true, true, false, 
                                                     RenderingAttributes.ROP_COPY );
    appearance.setPolygonAttributes(pa);
    appearance.setRenderingAttributes(ra);

    // make transparent
    TransparencyAttributes ta = new TransparencyAttributes();
    ta.setTransparencyMode(TransparencyAttributes.BLENDED);
    ta.setTransparency(HIGHLIGHTTRANSPARENCY);
    appearance.setTransparencyAttributes(ta);

    // create a material for the appearance
    Material m = new Material(DHIGHLIGHT, BLACK, LHIGHLIGHT, WHITE, 100.0f );
    m.setCapability( Material.ALLOW_COMPONENT_READ);
    m.setCapability( Material.ALLOW_COMPONENT_WRITE);
    m.setLightingEnable(true);
    appearance.setMaterial(m);

  // create the sphere
  //-------------------------------------------------------------------
    Sphere highlight = new Sphere( HIGHLIGHTSIZE, Sphere.GENERATE_NORMALS, appearance );   
    highlight.setPickable(false);
  
  // wire it together
  //-------------------------------------------------------------------
    // add highlight to transform group
    translatetg.addChild(highlight);
    bg.addChild(translatetg);

    // add branchgroup to scene
    scenebg.addChild(bg);
   
  } // end Highlight

//----------------------------------------------------------------------------------------------------- 
// DELETE FUNCTIONS
//----------------------------------------------------------------------------------------------------- 

// clear
//----------------------------------------------------------------------------------------------------- 
// clears all struts and balls

  public void clear()
  {
    try
    {
      // get all the child Nodes from the parent 
      java.util.Enumeration kids = scenebg.getAllChildren();
      int index = 0;

      //iterate through the Nodes 
      while ( kids.hasMoreElements() == true )
      {
        // get the node
        SceneGraphObject sgObject = (SceneGraphObject)
                kids.nextElement();
  
        // get the user data
        Object userData = sgObject.getUserData();

        // if detach capability is set and its not the highlight, delete the node
        if ( sgObject.getCapability(BranchGroup.ALLOW_DETACH) &&
             userData instanceof String && ((String) userData).compareTo( HIGHLIGHT ) != 0 )
        {
          scenebg.removeChild( index );
        }
        else
          index++;

      } // end while
    } // end try

    catch (Exception e)
    {
      // do nothing
    }  

    // reset values
    current = 0;
    Highlight();
    focus();
    indexLength = 0;

    ballCount = 1;

    for ( int i = RECT; i <= PENT; i++ )
    {
      for ( int j = SHORT; j <= LONG; j++ )
        strutCount[i][j] = 0;
    }

    // set the kit
    setKit();

    // update UI
    ui.frontBall.repaint();
    ui.backBall.repaint();

  } // end clear

// remove
//-----------------------------------------------------------------------------------------------------  
// removes one components of a set name

  public static void remove( String name )
  {
    try
    {
      // get all the child Nodes from the parent 
      java.util.Enumeration kids = scenebg.getAllChildren();
      int index = 0;

      //iterate through the Nodes 
      while ( kids.hasMoreElements() == true )
      {
        // get the object
        SceneGraphObject sgObject = (SceneGraphObject)
                kids.nextElement();

        // get the objects user data
        Object userData = sgObject.getUserData();

        // if the object has the user data we want, delete it
        if ( userData instanceof String && ((String) userData).compareTo( name ) == 0 )
          scenebg.removeChild( index );

        index++;
      } // end while
    } // end try

    catch (Exception e)
    {
      // do nothing
    }  
  } // end remove

// undo
//-----------------------------------------------------------------------------------------------------  
// removes the most recently added node

  public static void undo()
  {
    if ( indexLength > 0 ) // no need to undo if there is nothing left
    {
      // play sound
      runZome3d.undoSound = runZome3d.soundList.getClip(runZome3d.undoString);
      runZome3d.undoSound.play();

      // create the name of the most recent component
      String mostRecent = new String( "component" + indexLength );

      // remove it
      remove( mostRecent );

      if ( addOrder[indexLength][0] == BALL )
        ballCount--;

      strutCount[ addOrder[indexLength][1] ][ addOrder[indexLength][2] ]--;

      // if it happened to be current, pick a new current
      if ( indexLength == current )
      {
        current = indexLength-1;
        Highlight();
        focus();
      }

      // decrement the index
      indexLength--;

      // set the kit
      setKit();

      // update UI
      ui.frontBall.repaint();
      ui.backBall.repaint();

    } // end if

    else // play error sound
    {
      runZome3d.errorSound = runZome3d.soundList.getClip(runZome3d.errorString);
      runZome3d.errorSound.play();
    }
  } // end undo
  
//-----------------------------------------------------------------------------------------------------
// UTILITY FUNCTIONS
//-----------------------------------------------------------------------------------------------------

// focus 
//----------------------------------------------------------------------------------------------------- 
// moves view to a selected ball

  public static void focus()
  {
    // in case the scene was just rotated
    alphatg.setTransform(RESET);
    
   // cheater way to reset the transforms in the slider listener
   xslider.setValue( xslider.getValue()+0 );
   xslider.setValue( xslider.getValue()-1 );
  
   // move the view to is new position  
   Transform3D move = new Transform3D();
   move.setTranslation( new Vector3d( -pointIndex[current].getX(), -pointIndex[current].getY(),
                                      -pointIndex[current].getZ() ) );
   movetg.setTransform( move );

  } // end focus

// reset 
//----------------------------------------------------------------------------------------------------- 
// sets rotations and current ball to original positions

  public static void reset()
  {
    current = 0; // select origin
    Highlight(); // highlight it

    movetg.setTransform(RESET);  // reset view transform
    alphatg.setTransform(RESET); // reset interpolator transform

    zslider.setValue( 40 ); // reset the zoom
    yslider.setValue( 0 );  // reset y rotation
    xslider.setValue( 0 );  // reset x rotation

    // update the user interface
    ui.frontBall.repaint();
    ui.backBall.repaint();

  } // end reset

// isBall 
//----------------------------------------------------------------------------------------------------- 
// tests to see if there is a ball at a given location

  public static int isBall( int type, int size, int hole )
  {
    // holder
    gold3d location = new gold3d();

    // get the location
    location = setGold( size, type, hole );
    location.add( pointIndex[current] );

    // compare to location of every ball already there
    for ( int i = 0; i < indexLength; i++ )
    {
      if ( gold3d.isEqual( location, pointIndex[i] ) )
        return i; // found one!
    }

    return -1; // found jack shit

  } // end isBall

// isStrut 
//----------------------------------------------------------------------------------------------------- 
// checks for another strut already in that position

  public static boolean isStrut( int type, int hole )
  {
    // holders for short, medium, and long struts
    gold3d s = new gold3d();
    gold3d m = new gold3d();
    gold3d l = new gold3d();

    // set the positions
    s = setGold( SHORT, type, hole );
    m = setGold(   MED, type, hole );
    l = setGold(  LONG, type, hole );

    // makes positions relative to current
    s.add(pointIndex[current]);
    m.add(pointIndex[current]);
    l.add(pointIndex[current]);

    for ( int i = 0; i < indexLength; i ++ ) // for every component
    {
      if ( strutIndex[i][START] == current ) // if there is a matching start point
      {
        // is there a matching endpoint?
        if ( gold3d.isEqual(s, pointIndex[strutIndex[i][END]]) ||
             gold3d.isEqual(m, pointIndex[strutIndex[i][END]]) ||
             gold3d.isEqual(l, pointIndex[strutIndex[i][END]]) )
          return true;
       
      }
 
      if ( strutIndex[i][END] == current ) // if there is a matching  end point
      {
        // is there a matching start point?
        if ( gold3d.isEqual(s, pointIndex[strutIndex[i][START]]) ||
             gold3d.isEqual(m, pointIndex[strutIndex[i][START]]) ||
             gold3d.isEqual(l, pointIndex[strutIndex[i][START]]) )  
          return true;
       
      }
    }

    return false; // guess not

  } // end isStrut

// setKit
//----------------------------------------------------------------------------------------------------- 
// sets the gold number for a strut of a given size, type, and hole

  private static void setKit()
  {

  // first, make sure the current kit size is correct
  //----------------------------------------------------------------------------
    // if all conditions are true it can decrement the kit size
    int alltrue = 0;

    // checks the ball count first
    if ( ballCount > KIT[currentKit][0] )
    {
      currentKit++;
    }
    else if ( currentKit != 0 && ballCount <= KIT[currentKit-1][0] )
    {
      alltrue++;
    }

    // checks all the strutCounts
    for ( int i = RECT; i <= PENT; i++ )
    {
      for ( int j = SHORT; j <= LONG; j++ )
      {
        if ( strutCount[i][j] > KIT[currentKit][i+1] )
        {
          currentKit++;
        }
        else if ( currentKit != 0 && strutCount[i][j] <= KIT[currentKit-1][i+1] )
        {
          alltrue++;
        }
      }
    }

    // all elements were low enought to drecrement the kit
    if ( alltrue == 10 )
    {
      currentKit--;
      setKit();
    }
  
  // this section updates the text on the kitUI
  //----------------------------------------------------------------------------
    // the extra spaces help keep alignment
    if ( ballCount < 10 )
      kui.ballLabel.setText( "  " + ballCount );
    else if ( ballCount < 100 )
      kui.ballLabel.setText( " " + ballCount );
    else
      kui.ballLabel.setText( "" + ballCount );

    for ( int i = 0; i < 3; i++ )
    {
      if ( strutCount[RECT][i] < 10 )
        kui.blue[i].setText(  "  " + strutCount[RECT][i]); 
      else if ( strutCount[RECT][i] < 100 )
        kui.blue[i].setText(  " " + strutCount[RECT][i]);
      else
        kui.blue[i].setText(  "" + strutCount[RECT][i]);

      if ( strutCount[TRI][i] < 10 )
        kui.yellow[i].setText(  "  " + strutCount[TRI][i]); 
      else if ( strutCount[TRI][i] < 100 )
        kui.yellow[i].setText(  " " + strutCount[TRI][i]);
      else
        kui.yellow[i].setText(  "" + strutCount[TRI][i]);

      if ( strutCount[PENT][i] < 10 )
        kui.red[i].setText(  "  " + strutCount[PENT][i]); 
      else if ( strutCount[PENT][i] < 100 )
        kui.red[i].setText(  " " + strutCount[PENT][i]);
      else
        kui.red[i].setText(  " " + strutCount[PENT][i]);
    }

    kui.kitLabel.setText( KITMESSAGE[currentKit] );
    kui.currentKit = currentKit;  

  }

// Debug function - not used
private static void printStrut()
{
  System.out.println("balls: " + ballCount);

  for ( int i = 0; i < indexLength; i++ )
    System.out.println( "[" + strutIndex[i][0] + "," + strutIndex[i][1] + "]" );

  System.out.println( "Index: " + indexLength );
}

// setGold
//----------------------------------------------------------------------------------------------------- 
// sets the gold number for a strut of a given size, type, and hole

private static gold3d setGold( int size, int type, int hole )
{
  gold3d gold = new gold3d();

  // not much to it, it just looks up the correct position from those
  // huge data arrays at the beginning of the file
  // multiplies the position by the position scale (since arrays are scaled to .5)
  if ( type == RECT )
  {
    if ( size == SHORT )
       gold.set( POSTSCALE*ASR[hole][X], POSTSCALE*BSR[hole][X], 
                 POSTSCALE*ASR[hole][Y], POSTSCALE*BSR[hole][Y],
                 POSTSCALE*ASR[hole][Z], POSTSCALE*BSR[hole][Z] );
    else if ( size == MED )
      gold.set( POSTSCALE*AMR[hole][X], POSTSCALE*BMR[hole][X], 
                POSTSCALE*AMR[hole][Y], POSTSCALE*BMR[hole][Y],
                POSTSCALE*AMR[hole][Z], POSTSCALE*BMR[hole][Z] );                                
    else if ( size == LONG )
      gold.set( POSTSCALE*ALR[hole][X], POSTSCALE*BLR[hole][X], 
                POSTSCALE*ALR[hole][Y], POSTSCALE*BLR[hole][Y],
                POSTSCALE*ALR[hole][Z], POSTSCALE*BLR[hole][Z] );
  }

  else if ( type == TRI )
  {
    if ( size == SHORT )
      gold.set( POSTSCALE*AST[hole][X], POSTSCALE*BST[hole][X], 
                POSTSCALE*AST[hole][Y], POSTSCALE*BST[hole][Y],
                POSTSCALE*AST[hole][Z], POSTSCALE*BST[hole][Z] );
    else if ( size == MED )
      gold.set( POSTSCALE*AMT[hole][X], POSTSCALE*BMT[hole][X], 
                POSTSCALE*AMT[hole][Y], POSTSCALE*BMT[hole][Y],
                POSTSCALE*AMT[hole][Z], POSTSCALE*BMT[hole][Z] );                                
    else if ( size == LONG )
      gold.set( POSTSCALE*ALT[hole][X], POSTSCALE*BLT[hole][X], 
                POSTSCALE*ALT[hole][Y], POSTSCALE*BLT[hole][Y],
                POSTSCALE*ALT[hole][Z], POSTSCALE*BLT[hole][Z] );
  }   
  
  else if ( type == PENT )
  {
    if ( size == SHORT )
      gold.set( POSTSCALE*ASP[hole][X], POSTSCALE*BSP[hole][X], 
                POSTSCALE*ASP[hole][Y], POSTSCALE*BSP[hole][Y],
                POSTSCALE*ASP[hole][Z], POSTSCALE*BSP[hole][Z] );
    else if ( size == MED )
      gold.set( POSTSCALE*AMP[hole][X], POSTSCALE*BMP[hole][X], 
                POSTSCALE*AMP[hole][Y], POSTSCALE*BMP[hole][Y],
                POSTSCALE*AMP[hole][Z], POSTSCALE*BMP[hole][Z] );                                
    else if ( size == LONG )
      gold.set( POSTSCALE*ALP[hole][X], POSTSCALE*BLP[hole][X], 
                POSTSCALE*ALP[hole][Y], POSTSCALE*BLP[hole][Y],
                POSTSCALE*ALP[hole][Z], POSTSCALE*BLP[hole][Z] );
  }

  // returns the golden number
  return gold;

} // end setGold

// createStrutGeom - messy looking so I put it at the bottom
//----------------------------------------------------------------------------------------------------- 
// creates geometry info for a strut

  private static GeometryInfo createStrutGeom( int type, int size )
  {
    // geometry info holder
    GeometryInfo gi = new GeometryInfo( GeometryInfo.TRIANGLE_STRIP_ARRAY );

    // generate geometry data for correct strut
    if ( type == RECT )
    {
      float BlueLength = 1;

      if(size == SHORT)
        BlueLength = 1;
      if(size == MED)
        BlueLength = TAUF;
      if(size == LONG)
        BlueLength = TAUF*TAUF;

      // generate Blue Strut Coordinates
      Point3f[] blueC = {new Point3f( SIDE*0.5f, 0.0f, SIDE*TAUF*0.5f),
                         new Point3f( SIDE*0.5f, 0.0f,-SIDE*TAUF*0.5f),
                         new Point3f(-SIDE*0.5f, 0.0f,-SIDE*TAUF*0.5f),
                         new Point3f(-SIDE*0.5f, 0.0f, SIDE*TAUF*0.5f),

                         new Point3f( SIDE*0.5f, BlueLength*1.0f,  SIDE*TAUF*0.5f),
                         new Point3f( SIDE*0.5f, BlueLength*1.0f, -SIDE*TAUF*0.5f),
                         new Point3f(-SIDE*0.5f, BlueLength*1.0f, -SIDE*TAUF*0.5f),
                         new Point3f(-SIDE*0.5f, BlueLength*1.0f,  SIDE*TAUF*0.5f)};

      // set geometryInfo
      gi.setCoordinateIndices( BLUECI );
      gi.setStripCounts( BLUESC );
      gi.setCoordinates( blueC );
    }
    
    if ( type == TRI )
    {
      float YellowLength = SHORTYELLOWLENGTH;

      if(size == SHORT)
        YellowLength = SHORTYELLOWLENGTH;
      if(size == MED)
        YellowLength = TAUF*SHORTYELLOWLENGTH;
      if(size == LONG)
        YellowLength = TAUF*TAUF*SHORTYELLOWLENGTH;


      // Yellow Strut Coordinates
      Point3f[] yellowC = {    new Point3f(              0.0f, 0.0f,  2.0f/3*SIDE2*SIN60F),
                               new Point3f( 1.0f*SIDE2*COS60F, 0.0f, -1.0f/3*SIDE2*SIN60F),
                               new Point3f(-1.0f*SIDE2*COS60F, 0.0f, -1.0f/3*SIDE2*SIN60F),
  
                               new Point3f(              0.0f, YellowLength/2-YELLOWRADIUS,  2.0f/3*SIDE2*SIN60F),
                               new Point3f( 1.0f*SIDE2*COS60F, YellowLength/2-YELLOWRADIUS, -1.0f/3*SIDE2*SIN60F),
                               new Point3f(-1.0f*SIDE2*COS60F, YellowLength/2-YELLOWRADIUS, -1.0f/3*SIDE2*SIN60F),

                               new Point3f(              0.0f, YellowLength/2+YELLOWRADIUS,-2.0f/3*SIDE2*SIN60F),
                               new Point3f(-1.0f*SIDE2*COS60F, YellowLength/2+YELLOWRADIUS, 1.0f/3*SIDE2*SIN60F),
                               new Point3f( 1.0f*SIDE2*COS60F, YellowLength/2+YELLOWRADIUS, 1.0f/3*SIDE2*SIN60F),

                               new Point3f(             0.0f,  1.0f*YellowLength,-2.0f/3*SIDE2*SIN60F),
                               new Point3f(-1.0f*SIDE2*COS60F, 1.0f*YellowLength, 1.0f/3*SIDE2*SIN60F),
                               new Point3f( 1.0f*SIDE2*COS60F, 1.0f*YellowLength, 1.0f/3*SIDE2*SIN60F)};

      // set geometryInfo
      gi.setCoordinateIndices( YELLOWCI );
      gi.setStripCounts( YELLOWSC );
      gi.setCoordinates( yellowC );
    }

    if ( type == PENT )
    {
      float RedLength = SHORTREDLENGTH;

      if(size == SHORT)
        RedLength = SHORTREDLENGTH;
      if(size == MED)
        RedLength = TAUF*SHORTREDLENGTH;
      if(size == LONG)
        RedLength = TAUF*TAUF*SHORTREDLENGTH;


      // Red Strut Coordinates
      Point3f[] redC = { new Point3f(        0.0f, 0.0f, -.038351567f),
                         new Point3f( -.03647451f, 0.0f, -.01158129f),
                         new Point3f( -.02254249f, 0.0f,  .03102707f),
                         new Point3f(  .02254249f, 0.0f,  .03102707f),
                         new Point3f(  .03647451f, 0.0f, -.01158129f),

                         new Point3f(        0.0f, RedLength/2-REDRADIUS, -.038351567f),
                         new Point3f( -.03647451f, RedLength/2-REDRADIUS, -.01158129f),
                         new Point3f( -.02254249f, RedLength/2-REDRADIUS,  .03102707f),
                         new Point3f(  .02254249f, RedLength/2-REDRADIUS,  .03102707f),
                         new Point3f(  .03647451f, RedLength/2-REDRADIUS, -.01158129f),

                         new Point3f(        0.0f, RedLength/2+REDRADIUS, .038351567f), //10
                         new Point3f(  .03647451f, RedLength/2+REDRADIUS, .01158129f),
                         new Point3f(  .02254249f, RedLength/2+REDRADIUS,-.03102707f),
                         new Point3f( -.02254249f, RedLength/2+REDRADIUS,-.03102707f),
                         new Point3f( -.03647451f, RedLength/2+REDRADIUS, .01158129f),

                         new Point3f(        0.0f, RedLength, .038351567f),
                         new Point3f(  .03647451f, RedLength, .01158129f),
                         new Point3f(  .02254249f, RedLength,-.03102707f),
                         new Point3f( -.02254249f, RedLength,-.03102707f),
                         new Point3f( -.03647451f, RedLength, .01158129f) };

      // set geometryInfo
      gi.setCoordinateIndices( REDCI );
      gi.setStripCounts( REDSC );
      gi.setCoordinates( redC );
    }

    return gi;
  } // end createStrut

} // endfile


// solidstyle
// ...For great justice!!




                     