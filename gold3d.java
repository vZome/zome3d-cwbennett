// gold3d.java by C.W. Bennett
// 05/24/02
//
// Holds and manipulates "Golden" numbers of the zome system
// a(tau) + b where tau is the golden ratio
// 
//

// basic libraries
import javax.swing.*;
import javax.vecmath.*;

public class gold3d
{

  // GLOBALS
  //------------------------------------------------------------------

  // Golden Ratio Constant
  private static final double TAU = 1.618034;

  // a and b values for each direction
  // a is the TAU multiplier, b is the integer
  // an x, y and z point is needed for 3d space
  int ax, ay, az, bx, by, bz;

  // CONSTRUCTORS
  //------------------------------------------------------------------

  // default constructor
  // initialize all values to zero
  public gold3d()
  {
    ax = 0;
    ay = 0;
    az = 0;
    
    bx = 0;
    by = 0;
    bz = 0;
  }

  // Overloaded constructor
  // sets that values when constructed
  public gold3d( int x1, int x2, int y1, int y2, int z1, int z2 )
  {
    ax = x1;
    bx = x2;

    ay = y1;
    by = y2;
 
    az = z1;
    bz = z2;
  }

  // SET - GET functions
  //------------------------------------------------------------------

  // function to set the values directly
  public void set( int x1, int x2, int y1, int y2, int z1, int z2 )
  {
    ax = x1;
    bx = x2;

    ay = y1;
    by = y2;
 
    az = z1;
    bz = z2;
  }

  // get the real x value
  public double getX()
  {
    return (ax * TAU) + bx;
  }

  // get the real y value
  public double getY()
  {
    return (ay * TAU) + by;
  }

  // get the real z value
  public double getZ()
  {
    return (az * TAU) + bz;
  }

  // returns a the real values in Vector3d() form
  public Vector3d vector3d()
  {
     double x = (ax * TAU) + bx;
     double y = (ay * TAU) + by;
     double z = (az * TAU) + bz;

     Vector3d tempvector = new Vector3d();

     tempvector.set( x, y, z );

     return tempvector;
  }

  // COMPARISON FUNCTIONS
  //------------------------------------------------------------------

  // compares two gold3d numbers for equality
  public static boolean isEqual( gold3d p1, gold3d p2 )
  {
    if ( p1.ax == p2.ax && p1.ay == p2.ay && p1.az == p2.az && 
         p1.bx == p2.bx && p1.by == p2.by && p1.bz == p2.bz )
      return true;
    else
      return false;
  }

  // adds a gold3d point to to the current one and
  // returns the value in the current
  // i.e. number1.add( number2 )

  public gold3d add( gold3d p2 )
  {
    ax += p2.ax;
    ay += p2.ay;
    az += p2.az;

    bx += p2.bx;
    by += p2.by;
    bz += p2.bz;

    return this;
  }

}

// For Great Justice

         
    




