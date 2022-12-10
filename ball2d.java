// ball2D.java by Mike Wilson
//////////////////////////////////////////////////////////////////////
// ball2d Overview:
// ball2D creates 2 dimensional views of of the zomeball.  It takes a 
// boolean variable "frontOrBack" that controls whether the zomeball 
// acts as the front or the back.  If frontOrBack is true, ball2D is 
// the front of a zomeball. If it is false, ball2d is the back.
//////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.awt.event.*;
import java.awt.Polygon.*;
import java.applet.*;
import java.net.URL;
import java.net.MalformedURLException;

// Java extension packages
import javax.swing.*;

public class ball2d extends JPanel implements MouseListener, MouseMotionListener
{

  //-----------------------------------------------------------------------------------------------------
  // CONSTANTS
  //-----------------------------------------------------------------------------------------------------  

  private static final int SHORT = 0;
  private static final int MED   = 1;
  private static final int LONG  = 2;  

  private static final int REC  = 0;
  private static final int TRI  = 1;
  private static final int PENT = 2;   

  // lookup table: determines the location of the strut on the real zomeball.
  // It provides a relationship between the polygons on ball2d to the real 3D
  // zomeball.
  private static final int LOCATION[] = { 0,  0,  2,  1,  3,  1,  4,  3,  1,  0,  6,  3,
                                          7,  4,  8,  5,  9,  2, 10,  6, 11,  7, 12,  2,
                                          5,  8, 13,  9,  5, 14,  6, 10, 15, 11,  7, 16, 4,

                                         29, 18, 28, 11, 27, 19, 26,  9, 25,  8, 24, 12, 
                                         23, 17, 22, 16, 21, 10, 20, 15, 19, 14, 18, 13, 
                                         17,  9, 13,  8,  4, 16,  7, 11, 15, 10,  6, 14, 5 };

  // look up table: determines the type of strut(blue, yellow or red).
  // Like the LOCATION variable, it provides a relationship between 
  // the polygons on ball2d to the real 3D zomeball.

  private static final int SHAPE[] =  {  REC,  TRI, REC, PENT,  REC,  TRI, 
                                         REC, PENT, REC, PENT,  REC,  TRI,  
                                         REC,  TRI, REC,  TRI,  REC, PENT,  
                                         REC,  TRI, REC,  TRI,  REC,  TRI, 
                                         REC,  TRI, REC,  TRI, PENT,  REC,
                                        PENT,  TRI, REC,  TRI, PENT,  REC, PENT };


  private static final Color COLORS[] = { new Color(  0,   0, 255),  // Blue
                                          new Color(255, 200,   0),  // Yellow
                                          new Color(255,   0,   0)}; // Red

  // x cordinates of the ball2d polygon vertices
  private static final int x[] = { 52, 92, 92, 52, 72, 85,107,114,107, 85,
                                   72, 59, 37, 30, 37, 59, 65, 79,106,115,
                                  126,126,115,106, 79, 65, 38, 29, 18, 18,
                                   29, 39, 33, 60, 59, 65, 79, 85, 84,111,
                                  122,142,140,124,120,144,144,124,140,142,
                                  122,120,111, 84, 85, 79, 65, 60, 33, 59,
                                   22,  2,  4, 20, 24,  0,  0,  4,  2, 22,
                                   24, 20,  0,144,144,  0,  0,  0,144,144 };

  // y cordinates of the ball2d polygon vertices
  private static final int y[] = { 65, 65, 88, 88, 42, 37, 59, 77, 94,116, 
                                  111,116, 94, 76, 59, 37, 23, 23, 30, 42,
                                   59, 94,111,123,130,130,123,111, 94, 59,
                                   42, 30,  9,  2, 21,  0,  0, 21,  2,  9,
                                   20, 49, 46, 46, 31, 59, 94,107,107,104,
                                  133,122,144,151,132,153,153,151,144,132,
                                  133,104,107,107,122, 94, 59, 46, 49, 20,
                                   31, 46,  0,  0,153,153, 59, 94, 59, 94 };

  //-----------------------------------------------------------------------------------------------------
  // GLOBALS
  //-----------------------------------------------------------------------------------------------------  

  private int polygonNumber = -1;
  private int lastPolygon   = -1;

  private boolean frontView = true;

  private Polygon p[] = new Polygon[37];

  //-----------------------------------------------------------------------------------------------------
  // MAIN
  //-----------------------------------------------------------------------------------------------------  

  public ball2d(boolean frontOrBack)
  {
    // if frontView  true, then front ball. Else back ball.
    frontView = frontOrBack;

    addMouseListener(this);
    addMouseMotionListener(this);

//    setToolTipText("Click to add a stick.");

    initializeData();  // initialize polygon objects

  } // end ball2d

  public Dimension getPreferredSize()
  {
    //(145,154) needed for 2D zome ball
    return new Dimension(145,154);
  }

  //-----------------------------------------------------------------------------------------------------
  // MOUSE LISTENER
  //-----------------------------------------------------------------------------------------------------  

  // if mouse has moved in ball2d panel then...
  public void mouseMoved(MouseEvent event)
  {
    // find the polygon it moved in.
    setPolygonNumber(event.getX(), event.getY());
    repaint();

    if(polygonNumber >= 0)
    {
       // ...if the mouse move to a different polygon, 
       // then remove and add the next phantom strut.
       if(polygonNumber != lastPolygon)
       {
         zome3d.remove( zome3d.PHANTOM );  // ...remove phantom strut.

         int offSet = 0;

         if(frontView)
            offSet = 0;
         else
            offSet = 37;

         zome3d.addStrutBall(SHAPE[polygonNumber], zomeUI.getStrutSize(),
                             LOCATION[polygonNumber + offSet], zome3d.GHOST); // ...add phantom strut.
       } // end if
    }// end if
  }// end mouseMoved

  // If mouse clicked in a ball2d panel.
  public void mouseClicked(MouseEvent event)
  {
    // Find the polygon it clicked in.
    setPolygonNumber(event.getX(), event.getY());

    if(polygonNumber >= 0 && polygonNumber <= 36 )
    {
       int offSet = 0;

       if(frontView)
          offSet = 0;
       else
          offSet = 37;

       // Play sound for adding a strut or having an error because there already is a strut,
       // in the selected location.
       if(!zome3d.isStrut(SHAPE[polygonNumber], LOCATION[polygonNumber+offSet]))
          {
          runZome3d.addStrutSound = runZome3d.soundList.getClip(runZome3d.addStrutString);
          runZome3d.addStrutSound.play();
          }
       else
          {
          runZome3d.errorSound = runZome3d.soundList.getClip(runZome3d.errorString);
          runZome3d.errorSound.play();
          }
       zome3d.addStrutBall(SHAPE[polygonNumber], zomeUI.getStrutSize(),
                           LOCATION[polygonNumber + offSet], zome3d.REAL);

      } // endif

    repaint();

  }// end mouseClicked

  public void mouseExited(MouseEvent event)
  {
    polygonNumber = -1;
    zome3d.remove( zome3d.PHANTOM );
    repaint();
  }

  public void mousePressed (MouseEvent event){}
  public void mouseReleased(MouseEvent event){}
  public void mouseEntered (MouseEvent event){}
  public void mouseDragged (MouseEvent event){}

  //-----------------------------------------------------------------------------------------------------
  // PAINT
  //-----------------------------------------------------------------------------------------------------  

  public void paint(Graphics g)
  {
    super.paint(g);
    g.setColor(Color.black);

    int offSet = 0;

    if(frontView)
      offSet = 0;
    else
      offSet = 37;

    for ( int i = 0; i < 37; i++ )
    {
      // Highlight a polygon ...
      if ( polygonNumber == i || zome3d.isStrut( SHAPE[i], LOCATION[i+offSet] ) )
      {
        g.setColor(COLORS[SHAPE[i]]);
        g.fillPolygon(p[i]);
        g.setColor(Color.black);
        g.drawPolygon(p[i]);

      }//endif

      // or unhighlight a polygon.
      else if ( i < 25 )
      {
        g.setColor(Color.white);
        g.fillPolygon(p[i]);
        g.setColor(Color.black);
        g.drawPolygon(p[i]);

      }//endif
    }//endfor
  }//end paint

  //-----------------------------------------------------------------------------------------------------
  // DETRCTION FUNCTIONS
  //-----------------------------------------------------------------------------------------------------  


  // area, left, inside3, inside4, and inside5 are all part of and algorithm that
  // determines if a point is inside of a polygon.  Point (qx, qy) is the point in
  // question.  It works using a matrix operation to determine if the point in question
  // is on the left side of a line in the polygon.  If it is true for all sides of the polygon.
  // then the point is in the polygon.  inside3 checks triangles, inside4 checks rectangles,
  // and inside5 checks petagons.
  public int area(int mx, int my, int nx, int ny, int qx, int qy)
    {
    return((nx-mx)*(qy-my) - (qx-mx)*(ny-my));
    }

  public boolean left(int mx, int my, int nx, int ny, int qx, int qy)
    {
    return(area(mx, my, nx, ny, qx, qy)>0);
    }

  public boolean inside3(int ax, int ay, int bx, int by, int cx, int cy, int qx, int qy)
    {
    return(left(ax,ay,bx,by,qx,qy) && left(bx,by,cx,cy,qx,qy) && left(cx,cy,ax,ay,qx,qy));
    }

  public boolean inside4(int ax,int ay,int bx,int by,int cx,int cy,
                         int dx,int dy,int qx,int qy)
    {
    return(left(ax,ay,bx,by,qx,qy) && left(bx,by,cx,cy,qx,qy) &&
           left(cx,cy,dx,dy,qx,qy) && left(dx,dy,ax,ay,qx,qy));
    }

  public boolean inside5(int ax,int ay,int bx,int by,int cx,int cy,
                         int dx,int dy,int ex,int ey,int qx,int qy)
    {
    return(left(ax,ay,bx,by,qx,qy) && left(bx,by,cx,cy,qx,qy) &&
           left(cx,cy,dx,dy,qx,qy) && left(dx,dy,ex,ey,qx,qy)&& 
           left(ex,ey,ax,ay,qx,qy));
    }

  // setPolygonNumber determines which polygon the mouse is in.
  // It sets an idex for the array of polygons.
  public void setPolygonNumber(int mouseX, int mouseY)
  {
   if(inside4(x[72],y[72],x[73],y[73],x[74],y[74],x[75],y[75],mouseX,mouseY))
   {
     lastPolygon = polygonNumber;
     if(inside4(x[0],y[0],x[1],y[1],x[2],y[2],x[3],y[3],mouseX,mouseY))
       polygonNumber = 0;
     if(inside3(x[0],y[0],x[4],y[4],x[1],y[1],mouseX,mouseY))
       polygonNumber = 1;
     if(inside4(x[4],y[4],x[5],y[5],x[6],y[6],x[1],y[1],mouseX,mouseY))
       polygonNumber = 2;
     if(inside5(x[6],y[6],x[7],y[7],x[8],y[8],x[2],y[2],x[1],y[1],mouseX,mouseY))
       polygonNumber = 3;
     if(inside4(x[8],y[8],x[9],y[9],x[10],y[10],x[2],y[2],mouseX,mouseY))
       polygonNumber = 4;
     if(inside3(x[3],y[3],x[2],y[2],x[10],y[10],mouseX,mouseY))
       polygonNumber = 5;
     if(inside4(x[10],y[10],x[11],y[11],x[12],y[12],x[3],y[3],mouseX,mouseY))
       polygonNumber = 6;
     if(inside5(x[12],y[12],x[13],y[13],x[14],y[14],x[0],y[0],x[3],y[3],mouseX,mouseY))
       polygonNumber = 7;
     if(inside4(x[14],y[14],x[15],y[15],x[4],y[4],x[0],y[0],mouseX,mouseY))
       polygonNumber = 8;
     if(inside5(x[15],y[15],x[16],y[16],x[17],y[17],x[5],y[5],x[4],y[4],mouseX,mouseY))
       polygonNumber = 9;
     if(inside4(x[17],y[17],x[18],y[18],x[19],y[19],x[5],y[5],mouseX,mouseY))
       polygonNumber = 10;
     if(inside3(x[5],y[5],x[19],y[19],x[6],y[6],mouseX,mouseY))
       polygonNumber = 11;
     if(inside4(x[19],y[19],x[20],y[20],x[7],y[7],x[6],y[6],mouseX,mouseY))
       polygonNumber = 12;
     if(inside3(x[7],y[7],x[20],y[20],x[21],y[21],mouseX,mouseY))
       polygonNumber = 13;
     if(inside4(x[21],y[21],x[22],y[22],x[8],y[8],x[7],y[7],mouseX,mouseY))
       polygonNumber = 14;
     if(inside3(x[22],y[22],x[9],y[9],x[8],y[8],mouseX,mouseY))
       polygonNumber = 15;
     if(inside4(x[22],y[22],x[23],y[23],x[24],y[24],x[9],y[9],mouseX,mouseY))
       polygonNumber = 16;
     if(inside5(x[24],y[24],x[25],y[25],x[11],y[11],x[10],y[10],x[9],y[9],mouseX,mouseY))
       polygonNumber = 17;
     if(inside4(x[25],y[25],x[26],y[26],x[27],y[27],x[11],y[11],mouseX,mouseY))
       polygonNumber = 18;
     if(inside3(x[27],y[27],x[12],y[12],x[11],y[11],mouseX,mouseY))
       polygonNumber = 19;
     if(inside4(x[27],y[27],x[28],y[28],x[13],y[13],x[12],y[12],mouseX,mouseY))
       polygonNumber = 20;
     if(inside3(x[28],y[28],x[29],y[29],x[13],y[13],mouseX,mouseY))
       polygonNumber = 21;
     if(inside4(x[29],y[29],x[30],y[30],x[14],y[14],x[13],y[13],mouseX,mouseY))
       polygonNumber = 22;
     if(inside3(x[30],y[30],x[15],y[15],x[14],y[14],mouseX,mouseY))
       polygonNumber = 23;
     if(inside4(x[30],y[30],x[31],y[31],x[16],y[16],x[15],y[15],mouseX,mouseY))
       polygonNumber = 24;
     if(inside4(x[72],y[72],x[35],y[35],x[16],y[16],x[31],y[31],mouseX,mouseY))
       polygonNumber = 25;
     if(inside4(x[35],y[35],x[36],y[36],x[17],y[17],x[16],y[16],mouseX,mouseY))
       polygonNumber = 26;
     if(inside4(x[36],y[36],x[73],y[73],x[18],y[18],x[17],y[17],mouseX,mouseY))
       polygonNumber = 27;
     if(inside5(x[73],y[73],x[78],y[78],x[20],y[20],x[19],y[19],x[18],y[18],mouseX,mouseY))
       polygonNumber = 28;
     if(inside4(x[20],y[20],x[78],y[78],x[79],y[79],x[21],y[21],mouseX,mouseY))
       polygonNumber = 29;
     if(inside5(x[21],y[21],x[79],y[79],x[74],y[74],x[23],y[23],x[22],y[22],mouseX,mouseY))
       polygonNumber = 30;
     if(inside4(x[24],y[24],x[23],y[23],x[74],y[74],x[55],y[55],mouseX,mouseY))
       polygonNumber = 31;
     if(inside4(x[25],y[25],x[24],y[24],x[55],y[55],x[56],y[56],mouseX,mouseY))
       polygonNumber = 32;
     if(inside4(x[26],y[26],x[25],y[25],x[56],y[56],x[75],y[75],mouseX,mouseY))
       polygonNumber = 33;
     if(inside5(x[77],y[77],x[28],y[28],x[27],y[27],x[26],y[26],x[75],y[75],mouseX,mouseY))
       polygonNumber = 34;
     if(inside4(x[76],y[76],x[29],y[29],x[28],y[28],x[77],y[77],mouseX,mouseY))
       polygonNumber = 35;
     if(inside5(x[72],y[72],x[31],y[31],x[30],y[30],x[29],y[29],x[76],y[76],mouseX,mouseY))
       polygonNumber = 36;

   }// end if

   else
     polygonNumber = -1;

}// end setPolygonNumber

  //-----------------------------------------------------------------------------------------------------
  // INITIALIZE DATA
  //-----------------------------------------------------------------------------------------------------  

public void initializeData()
{
    p[0] = new Polygon();
    p[0].addPoint(x[0],y[0]);
    p[0].addPoint(x[1],y[1]);
    p[0].addPoint(x[2],y[2]);
    p[0].addPoint(x[3],y[3]);

    p[1] = new Polygon();
    p[1].addPoint(x[0],y[0]);
    p[1].addPoint(x[4],y[4]);
    p[1].addPoint(x[1],y[1]);

    p[2] = new Polygon();
    p[2].addPoint(x[4],y[4]);
    p[2].addPoint(x[5],y[5]);
    p[2].addPoint(x[6],y[6]);
    p[2].addPoint(x[1],y[1]);

    p[3] = new Polygon();
    p[3].addPoint(x[6],y[6]);
    p[3].addPoint(x[7],y[7]);
    p[3].addPoint(x[8],y[8]);
    p[3].addPoint(x[2],y[2]);
    p[3].addPoint(x[1],y[1]);

    p[4] = new Polygon();
    p[4].addPoint(x[2],y[2]);
    p[4].addPoint(x[8],y[8]);
    p[4].addPoint(x[9],y[9]);
    p[4].addPoint(x[10],y[10]);

    p[5] = new Polygon();
    p[5].addPoint(x[3],y[3]);
    p[5].addPoint(x[2],y[2]);
    p[5].addPoint(x[10],y[10]);

    p[6] = new Polygon();
    p[6].addPoint(x[3],y[3]);
    p[6].addPoint(x[10],y[10]);
    p[6].addPoint(x[11],y[11]);
    p[6].addPoint(x[12],y[12]);

    p[7] = new Polygon();
    p[7].addPoint(x[12],y[12]);
    p[7].addPoint(x[13],y[13]);
    p[7].addPoint(x[14],y[14]);
    p[7].addPoint(x[0],y[0]);
    p[7].addPoint(x[3],y[3]);

    p[8] = new Polygon();
    p[8].addPoint(x[14],y[14]);
    p[8].addPoint(x[15],y[15]);
    p[8].addPoint(x[4],y[4]);
    p[8].addPoint(x[0],y[0]);

    p[9] = new Polygon();
    p[9].addPoint(x[15],y[15]);
    p[9].addPoint(x[16],y[16]);
    p[9].addPoint(x[17],y[17]);
    p[9].addPoint(x[5],y[5]);
    p[9].addPoint(x[4],y[4]);

    p[10] = new Polygon();
    p[10].addPoint(x[17],y[17]);
    p[10].addPoint(x[18],y[18]);
    p[10].addPoint(x[19],y[19]);
    p[10].addPoint(x[5],y[5]);

    p[11] = new Polygon();
    p[11].addPoint(x[5],y[5]);
    p[11].addPoint(x[19],y[19]);
    p[11].addPoint(x[6],y[6]);

    p[12] = new Polygon();
    p[12].addPoint(x[19],y[19]);
    p[12].addPoint(x[20],y[20]);
    p[12].addPoint(x[7],y[7]);
    p[12].addPoint(x[6],y[6]);

    p[13] = new Polygon();
    p[13].addPoint(x[7],y[7]);
    p[13].addPoint(x[20],y[20]);
    p[13].addPoint(x[21],y[21]);

    p[14] = new Polygon();
    p[14].addPoint(x[7],y[7]);
    p[14].addPoint(x[21],y[21]);
    p[14].addPoint(x[22],y[22]);
    p[14].addPoint(x[8],y[8]);

    p[15] = new Polygon();
    p[15].addPoint(x[8],y[8]);
    p[15].addPoint(x[22],y[22]);
    p[15].addPoint(x[9],y[9]);

    p[16] = new Polygon();
    p[16].addPoint(x[9],y[9]);
    p[16].addPoint(x[22],y[22]);
    p[16].addPoint(x[23],y[23]);
    p[16].addPoint(x[24],y[24]);

    p[17] = new Polygon();
    p[17].addPoint(x[24],y[24]);
    p[17].addPoint(x[25],y[25]);
    p[17].addPoint(x[11],y[11]);
    p[17].addPoint(x[10],y[10]);
    p[17].addPoint(x[9],y[9]);

    p[18] = new Polygon();
    p[18].addPoint(x[11],y[11]);
    p[18].addPoint(x[25],y[25]);
    p[18].addPoint(x[26],y[26]);
    p[18].addPoint(x[27],y[27]);

    p[19] = new Polygon();
    p[19].addPoint(x[27],y[27]);
    p[19].addPoint(x[11],y[11]);
    p[19].addPoint(x[12],y[12]);

    p[20] = new Polygon();
    p[20].addPoint(x[27],y[27]);
    p[20].addPoint(x[28],y[28]);
    p[20].addPoint(x[13],y[13]);
    p[20].addPoint(x[12],y[12]);

    p[21] = new Polygon();
    p[21].addPoint(x[28],y[28]);
    p[21].addPoint(x[29],y[29]);
    p[21].addPoint(x[13],y[13]);

    p[22] = new Polygon();
    p[22].addPoint(x[29],y[29]);
    p[22].addPoint(x[30],y[30]);
    p[22].addPoint(x[14],y[14]);
    p[22].addPoint(x[13],y[13]);

    p[23] = new Polygon();
    p[23].addPoint(x[30],y[30]);
    p[23].addPoint(x[15],y[15]);
    p[23].addPoint(x[14],y[14]);

    p[24] = new Polygon();
    p[24].addPoint(x[30],y[30]);
    p[24].addPoint(x[31],y[31]);
    p[24].addPoint(x[16],y[16]);
    p[24].addPoint(x[15],y[15]);

    p[25] = new Polygon();
    p[25].addPoint(x[32],y[32]);
    p[25].addPoint(x[33],y[33]);
    p[25].addPoint(x[16],y[16]);
    p[25].addPoint(x[31],y[31]);

    p[26] = new Polygon();
    p[26].addPoint(x[35],y[35]);
    p[26].addPoint(x[36],y[36]);
    p[26].addPoint(x[17],y[17]);
    p[26].addPoint(x[16],y[16]);

    p[27] = new Polygon();
    p[27].addPoint(x[17],y[17]);
    p[27].addPoint(x[38],y[38]);
    p[27].addPoint(x[39],y[39]);
    p[27].addPoint(x[18],y[18]);

    p[28] = new Polygon();
    p[28].addPoint(x[40],y[40]);
    p[28].addPoint(x[41],y[41]);
    p[28].addPoint(x[20],y[20]);
    p[28].addPoint(x[19],y[19]);
    p[28].addPoint(x[18],y[18]);

    p[29] = new Polygon();
    p[29].addPoint(x[45],y[45]);
    p[29].addPoint(x[46],y[46]);
    p[29].addPoint(x[21],y[21]);
    p[29].addPoint(x[20],y[20]);

    p[30] = new Polygon();
    p[30].addPoint(x[49],y[49]);
    p[30].addPoint(x[50],y[50]);
    p[30].addPoint(x[23],y[23]);
    p[30].addPoint(x[22],y[22]);
    p[30].addPoint(x[21],y[21]);

    p[31] = new Polygon();
    p[31].addPoint(x[52],y[52]);
    p[31].addPoint(x[53],y[53]);
    p[31].addPoint(x[24],y[24]);
    p[31].addPoint(x[23],y[23]);

    p[32] = new Polygon();
    p[32].addPoint(x[55],y[55]);
    p[32].addPoint(x[56],y[56]);
    p[32].addPoint(x[25],y[25]);
    p[32].addPoint(x[24],y[24]);

    p[33] = new Polygon();
    p[33].addPoint(x[57],y[57]);
    p[33].addPoint(x[58],y[58]);
    p[33].addPoint(x[26],y[26]);
    p[33].addPoint(x[25],y[25]);

    p[34] = new Polygon();
    p[34].addPoint(x[60],y[60]);
    p[34].addPoint(x[61],y[61]);
    p[34].addPoint(x[28],y[28]);
    p[34].addPoint(x[27],y[27]);
    p[34].addPoint(x[26],y[26]);

    p[35] = new Polygon();
    p[35].addPoint(x[65],y[65]);
    p[35].addPoint(x[66],y[66]);
    p[35].addPoint(x[29],y[29]);
    p[35].addPoint(x[28],y[28]);

    p[36] = new Polygon();
    p[36].addPoint(x[68],y[68]);
    p[36].addPoint(x[69],y[69]);
    p[36].addPoint(x[31],y[31]);
    p[36].addPoint(x[30],y[30]);
    p[36].addPoint(x[29],y[29]);

  } // end initialization

} // end file
