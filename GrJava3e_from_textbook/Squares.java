//Evan Nibbe
//Feb 5, 2022
//Squares.java is based on the DefPoly example class. This calls a revised form of CvDefPoly which draws the concentric
//squares instead of drawing a polygon from clicking within the area.
// DefPoly.java: Drawing a polygon.
// Uses: CvDefPoly (discussed below).

// Copied from Section 1.5 of
//    Ammeraal, L. and K. Zhang (2007). Computer Graphics for Java Programmers, 2nd Edition,
//       Chichester: John Wiley.

import java.awt.*;
import java.awt.event.*;

public class Squares extends Frame {
   public static void main(String[] args) {new Squares();}

   Squares() {
      super("Define polygon vertices by clicking");
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {System.exit(0);}
      });
      setSize(500, 500);
      add("Center", new CvDefPoly());
      setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      setVisible(true);
   }
}
