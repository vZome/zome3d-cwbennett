//
// runZome3d.java
// by C.W. Bennett
// 6/11/02
// 
// This is a helper Applet to launch zome3d
// Future versions may eliminate this step

// Java Core
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// URL handling
import java.net.URL;
import java.net.MalformedURLException;

public class runZome3d extends Applet implements ActionListener 
{
  public static URL codeBase;                 // the locations of the java code
  public static AppletContext appletContext;  // the location fo the applet

  // Swing Components
  public static JButton button;
  public static JFrame frame;

  // Images - The applet must load all images
  public static Image mines, ball, bs, bm, bl, ys, ym, yl, rs, rm, rl;

  // Sound Variables - The applet must load all sounds
  public static AudioClip undoSound, errorSound, addStrutSound, starBurstSound;
  public static SoundList soundList;
  public static int NUMBEROFSOUNDS = 4;

  // sound string names
  public static String undoString = "undo.wav";
  public static String errorString = "error.wav";
  public static String addStrutString = "pop.wav";
  public static String starBurstString = "starBurst.wav";

// init
//-------------------------------------------------------------------------
// applet initialization function - automatically called when aplet starts

  public void init()
  {    

  // This section loads data
  //------------------------------------------------------    
    // finds the actual applet location
    codeBase = getCodeBase();
    appletContext = getAppletContext();

    //Loads images
    ball = getImage( codeBase, "ball1.gif" );

    bs = getImage( codeBase, "smallBlue.gif" );
    bm = getImage( codeBase, "mediumBlue.gif" );
    bl = getImage( codeBase, "largeBlue.gif" );

    ys = getImage( codeBase, "smallYellow.gif" );
    ym = getImage( codeBase, "mediumYellow.gif" );
    yl = getImage( codeBase, "largeYellow.gif" );

    rs = getImage( codeBase, "smallRed.gif" );
    rm = getImage( codeBase, "mediumRed.gif" );
    rl = getImage( codeBase, "largeRed.gif" );
 
    mines = getImage( codeBase, "triarc.gif");

    // Initializing sound stuff
    soundList = new SoundList( codeBase , NUMBEROFSOUNDS);
    soundList.startLoading(undoString);
    soundList.startLoading(errorString);
    soundList.startLoading(addStrutString);
    soundList.startLoading(starBurstString);

  // This section creates the launcher
  //------------------------------------------------------
     
    // Loads the image and creates a label
    ImageIcon icon = new ImageIcon( ball );
    JLabel label = new JLabel("Zome 3D ver 1.0");
    label.setIcon( icon );
    label.setAlignmentX( Component.CENTER_ALIGNMENT );

    // Creates a launch button
    button = new JButton("Run Zome3D");
    button.addActionListener( this );
    button.setAlignmentX( Component.CENTER_ALIGNMENT );

    // This panel holds the label and button and sets vertical spacing
    JPanel main1 = new JPanel();
    main1.setLayout( new BoxLayout( main1, BoxLayout.Y_AXIS ));
    main1.add (Box.createRigidArea( new Dimension(0,10) )); // blank space
    main1.add ( label );
    main1.add (Box.createRigidArea( new Dimension(0,10) ));
    main1.add( button );
    main1.add (Box.createRigidArea( new Dimension(0,10) ));

    // This panel is to set the horizonal spacing
    JPanel main2 = new JPanel();
    main2.setLayout( new BoxLayout( main2, BoxLayout.X_AXIS ));
    main2.add (Box.createRigidArea( new Dimension(10,0) ));
    main2.add ( main1 );
    main2.add (Box.createRigidArea( new Dimension(10,0) )); 

    // this adds the panel to the applet (which extends JPanel)
    add( main2 );
  }

// destroy
//-------------------------------------------------------------------------
// automatically called when the applet is exited

  public void destroy() 
  {
      frame.setVisible(false);
      frame = null;
  }

// ActionPerformed
//-------------------------------------------------------------------------
// action listener for the button

  public void actionPerformed(ActionEvent event) 
  {
    button.setEnabled(false);

    frame = new JFrame("Zome 3D ver 1.0");
    final zome3d zome = new zome3d(frame);

    Container contentPane = frame.getContentPane();
    contentPane.setLayout(new BorderLayout());
    contentPane.add( zome , BorderLayout.CENTER );

    frame.addWindowListener(new WindowAdapter() 
    {
      public void windowClosing(WindowEvent e)
      {
        zome.clear();
        button.setEnabled(true);
      }
    });

    //Set the initial frame
    frame.setVisible(true);
    frame.setResizable(false);

  }
}





     