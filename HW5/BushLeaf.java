//Evan Nibbe, taking from DefPoly.java in order to satisfy the constraints of Homework 3 for CS 4361 Computer Graphics
// DefPoly.java: Drawing a polygon.
// Uses: CvDefPoly (discussed below).

// Copied from Section 1.5 of
//    Ammeraal, L. and K. Zhang (2007). Computer Graphics for Java Programmers, 2nd Edition,
//       Chichester: John Wiley.

import java.awt.*;
import java.awt.event.*;

public class BushLeaf extends Frame {
   public static void main(String[] args) {new BushLeaf();}

   BushLeaf() {
      super("Define A and B vertices by clicking");
      addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent e) {System.exit(0);}
      });
      setSize(1280, 720);
      add("Center", new CvDefBushLeaf());
      setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
      setVisible(true);
   }
}
