// ZomeUI.java by Mike Wilson
///////////////////////////////////
// Overview:
// ZomeUI calls two instances of ball2d and displays the radio buttons
// for the strut size.

import java.awt.*;
import java.awt.event.*;
import java.awt.Polygon.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;

// Java extension packages
import javax.swing.*;

public class zomeUI extends JPanel
{

  //-----------------------------------------------------------------------------------------------------
  // CONSTANTS
  //-----------------------------------------------------------------------------------------------------  

  private static final int SHORT = 0;
  private static final int MED   = 1;
  private static final int LONG  = 2;  

  //-----------------------------------------------------------------------------------------------------
  // GLOBALS
  //-----------------------------------------------------------------------------------------------------  

  private JRadioButton smallStick, mediumStick, largeStick;
  private ButtonGroup sizeRadioGroup;

  private static int strutSize = 2;

  private JPanel buttonPanel;
  private JPanel titleFront;
  private JPanel titleBack;
  private JPanel ballPanel;

  // set up ball2d panels
  public ball2d frontBall = new ball2d(true);
  public ball2d backBall = new ball2d(false);

  //-----------------------------------------------------------------------------------------------------
  // MAIN
  //-----------------------------------------------------------------------------------------------------  

  public zomeUI()
  {
    // set size Radio buttons and add to Radio group
    smallStick  = new JRadioButton("Small Stick", false);
    mediumStick = new JRadioButton("Medium Stick", false);
    largeStick  = new JRadioButton("Large Stick", true);
    sizeRadioGroup = new ButtonGroup();
    sizeRadioGroup.add(smallStick);
    sizeRadioGroup.add(mediumStick);
    sizeRadioGroup.add(largeStick);

    // set up event handler for Radio Buttons
    RadioButtonHandler radioHandler = new RadioButtonHandler();
    smallStick.addActionListener(radioHandler);
    smallStick.setHorizontalAlignment(AbstractButton.CENTER);
    mediumStick.addActionListener(radioHandler);
    mediumStick.setHorizontalAlignment(AbstractButton.CENTER);
    largeStick.addActionListener(radioHandler);
    largeStick.setHorizontalAlignment(AbstractButton.CENTER);

    // add Boxes to buttonPanel
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new GridLayout(3,1));
    buttonPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
    buttonPanel.add(smallStick);
    buttonPanel.add(mediumStick);
    buttonPanel.add(largeStick);
    buttonPanel.setToolTipText("Change the stick size.");

    // set up title panels
    // front ball
    titleFront = new JPanel();
    titleFront.setBorder(new TitledBorder("Front"));
    titleFront.add(frontBall);
    // back ball
    titleBack = new JPanel();
    titleBack.setBorder(new TitledBorder("Back"));
    titleBack.add(backBall);

   // set up Ball Panel
   ballPanel = new JPanel();
   ballPanel.setLayout(new BorderLayout());
   ballPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
   ballPanel.setLayout(new GridLayout(2,1));
   ballPanel.add(titleFront);
   ballPanel.add(titleBack);

   setLayout(new BorderLayout());
   add(ballPanel, BorderLayout.NORTH);
   add(buttonPanel, BorderLayout.CENTER);

  }

  public static int getStrutSize()
   {
   if(strutSize == SHORT)
      return SHORT;
   if(strutSize == MED)
      return MED;
   if(strutSize == LONG)
      return LONG;
   return SHORT;
   }

  //-----------------------------------------------------------------------------------------------------
  // ACTION LISTENER
  //-----------------------------------------------------------------------------------------------------  

  private class RadioButtonHandler implements ActionListener
  {
    public void actionPerformed(ActionEvent event)
    {
      if(event.getSource() == smallStick)
      {
        strutSize = SHORT;
      }
      if(event.getSource() == mediumStick)
      {
        strutSize = MED;
      }
      if(event.getSource() == largeStick)
      {
        strutSize = LONG;
      }
    }
  }

} // end of zomeUI class
