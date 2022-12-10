// kitUI.java by Mike Wilson
//////////////////////////////////////////////////////////////////////
// kitUI Overview:
// kitUI is the panel at the bottom of the applet the number of zomeballs
// and struts used in the current model.  Its data is public and is accesed 
// int zome3d.class.
//////////////////////////////////////////////////////////////////////

import java.awt.*;
import java.awt.event.*;
import java.awt.Polygon.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.BevelBorder;
import java.net.URL;
import java.net.MalformedURLException;
import java.applet.*;

// Java extension packages
import javax.swing.*;

public class kitUI extends JPanel implements ActionListener
{

    private JPanel ballPanel;
    private JPanel bluePanel;
    private JPanel yellowPanel;
    private JPanel redPanel;
    private JPanel strutPanel;
    private JPanel ballStrutPanel;
    private JPanel kitPanel;

    // text for displaying counted parts.
    public  JLabel ballLabel;
    public  JLabel blue[] = {new JLabel(), new JLabel(), new JLabel()};
    public JLabel yellow[] = {new JLabel(), new JLabel(), new JLabel()};
    public  JLabel red[] = {new JLabel(), new JLabel(), new JLabel()};
    public  JLabel kitLabel;

    private JButton kitButton;

    public int currentKit = 0;

    // URLs for each kit.
    private static final String[] kitUrl = { "http://store.yahoo.com/zome-tool/pioneerkit.html",
                                             "http://store.yahoo.com/zome-tool/adventurerkit.html",
                                             "http://store.yahoo.com/zome-tool/explorerkit.html",
                                             "http://store.yahoo.com/zome-tool/creatorkit.html",
                                             "http://store.yahoo.com/zome-tool/admatkit.html" };


  //-----------------------------------------------------------------------------------------------------
  // MAIN
  //-----------------------------------------------------------------------------------------------------  

  public kitUI()
  {

  // set up ball panel
  Icon ballIcon = new ImageIcon(runZome3d.ball);
  ballLabel = new JLabel("  1 ");
  ballLabel.setIcon(ballIcon);
  ballLabel.setHorizontalTextPosition(SwingConstants.LEFT);
  ballPanel = new JPanel();
  ballPanel.setLayout(new BorderLayout());
  ballPanel.setBorder(new TitledBorder(""));
  ballPanel.add(ballLabel, BorderLayout.CENTER);

  // set up blue panel
  bluePanel = new JPanel();
  bluePanel.setLayout(new GridLayout(3,1));
  bluePanel.setBorder(new TitledBorder(""));
  Icon blueIcon[] = {new ImageIcon(runZome3d.bs), 
                     new ImageIcon(runZome3d.bm),
                     new ImageIcon(runZome3d.bl)};
  blue[0].setText("  0 ");
  blue[1].setText("  0 ");
  blue[2].setText("  0 ");
  for(int i = 0; i < 3; i++)
     {
     blue[i].setIcon(blueIcon[i]);
     blue[i].setHorizontalTextPosition(SwingConstants.LEFT);
     bluePanel.add(blue[i]);
     }

  // set up yellow panel
  yellowPanel = new JPanel();
  yellowPanel.setLayout(new GridLayout(3,1));
  yellowPanel.setBorder(new TitledBorder(""));
  Icon yellowIcon[] = {new ImageIcon(runZome3d.ys), 
                     new ImageIcon(runZome3d.ym),
                     new ImageIcon(runZome3d.yl)};
  yellow[0].setText("  0 ");
  yellow[1].setText("  0 ");
  yellow[2].setText("  0 ");
  for(int i = 0; i < 3; i++)
     {
     yellow[i].setIcon(yellowIcon[i]);
     yellow[i].setHorizontalTextPosition(SwingConstants.LEFT);
     yellowPanel.add(yellow[i]);
     }

  // set up red panel
  redPanel = new JPanel();
  redPanel.setLayout(new GridLayout(3,1));
  redPanel.setBorder(new TitledBorder(""));
  Icon redIcon[] = {new ImageIcon(runZome3d.rs), 
                    new ImageIcon(runZome3d.rm),
                    new ImageIcon(runZome3d.rl)};
  red[0].setText("  0 ");
  red[1].setText("  0 ");
  red[2].setText("  0 ");
  for(int i = 0; i < 3; i++)
     {
     red[i].setIcon(redIcon[i]);
     red[i].setHorizontalTextPosition(SwingConstants.LEFT);
     redPanel.add(red[i]);
     }

  JPanel strutpanel = new JPanel();
  strutpanel.setLayout( new BoxLayout(strutpanel, BoxLayout.X_AXIS) );
  strutpanel.add( bluePanel );
  strutpanel.add( yellowPanel);
  strutpanel.add( redPanel );

  // set up kit panel
  JPanel kitLabelpanel = new JPanel();
  JLabel kitLabelTop = new JLabel("You can build this ");
  JLabel kitLabelMid = new JLabel("model with the");
  kitLabel = new JLabel("Pioneer Kit!!!");

  kitLabelTop.setHorizontalAlignment(SwingConstants.CENTER);
  kitLabelMid.setHorizontalAlignment(SwingConstants.CENTER);
  kitLabel.setHorizontalAlignment(SwingConstants.CENTER);

  kitLabelpanel.setLayout( new BorderLayout() );
  kitLabelpanel.add( kitLabelTop, BorderLayout.NORTH );
  kitLabelpanel.add( kitLabelMid, BorderLayout.CENTER );
  kitLabelpanel.add( kitLabel, BorderLayout.SOUTH );

  kitButton = new JButton("Buy Now!!");
  kitButton.addActionListener( this );

  kitPanel = new JPanel();
  kitPanel.setLayout(new BoxLayout(kitPanel, BoxLayout.X_AXIS));
  kitPanel.add(Box.createRigidArea( new Dimension(4,0) ));
  kitPanel.add(kitLabelpanel);
  kitPanel.add(Box.createRigidArea( new Dimension(8,0) ));
  kitPanel.add( kitButton ); 
  kitPanel.setBorder(new TitledBorder(""));

  // set up panels in kitUI
  setLayout(new BorderLayout());
  setBorder(new BevelBorder(BevelBorder.RAISED));
  add(ballPanel, BorderLayout.WEST);
  add(strutpanel, BorderLayout.CENTER);
  add(kitPanel, BorderLayout.EAST);

  }

  public Dimension getPreferredSize()
  {return new Dimension(800,77);}

  // for URL button
  public void actionPerformed( ActionEvent ae )
  {
      URL url = null;

      try
      {
        url = new URL( kitUrl[currentKit] );
      }
      catch ( MalformedURLException e )
      {
        System.err.println("Bad Url");
      }

      if ( url != null && runZome3d.appletContext != null )
        runZome3d.appletContext.showDocument( url, "_blank" );
  }  

}

