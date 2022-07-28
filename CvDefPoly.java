// CvDefPoly.java: To be used in other program files.

// Copied from Section 1.5 of
//    Ammeraal, L. and K. Zhang (2007). Computer Graphics for Java Programmers, 2nd Edition,
//       Chichester: John Wiley.

// A class that enables the user to define
// a polygon by clicking the mouse.
// Uses: Point2D (discussed below).
import java.awt.*;
import java.awt.event.*;
import java.util.*;

class CvDefPoly extends Canvas {
   Vector<Point2D> v = new Vector<Point2D>();
   float x0, y0, rWidth = 10.0F, rHeight = 7.5F, pixelSize;
   boolean ready = true;
   int centerX, centerY;
	
	static float area2(Point2D A, Point2D B, Point2D C) {
		return (A.x-C.x)*(B.y-C.y)-(A.y-C.y)*(B.x-C.x);
	}
	static int sign(float a) {
		if (a>.00001) {
			return 1;
		} else if (a<-.00001) {
			return -1;
		} else {
			return 0;
		}
	}
   CvDefPoly() {
      addMouseListener(new MouseAdapter() {
         public void mousePressed(MouseEvent evt) {
            float xA = fx(evt.getX()), yA = fy(evt.getY());
            if (ready) {
               v.removeAllElements();
               x0 = xA; y0 = yA;
               ready = false;
            }
            float dx = xA - x0, dy = yA - y0;
            //if (v.size() > 0 && 
            //   dx * dx + dy * dy < 20 * pixelSize * pixelSize)
               // Previously 4 instead of 20 .........................
               //ready = true;
            if (v.size() < 4) {
               v.addElement(new Point2D(xA, yA));
            } 
            
            // Added December 2016:
            if(evt.getModifiers()==InputEvent.BUTTON3_MASK) {
               ready = true;
            }
            
            repaint();
         }
      });
   }

   void initgr() {
      Dimension d = getSize();
      int maxX = d.width - 1, maxY = d.height - 1;
      pixelSize = Math.max(rWidth / maxX, rHeight / maxY);
      centerX = maxX / 2; centerY = maxY / 2;
   }

   int iX(float x) {return Math.round(centerX + x / pixelSize);}
   int iY(float y) {return Math.round(centerY - y / pixelSize);}
   float fx(int x) {return (x - centerX) * pixelSize;}
   float fy(int y) {return (centerY - y) * pixelSize;}

   public void paint(Graphics g) {
      initgr();
	//g.setColor(new Color(223, 223, 223)); //this is done in leiu of commenting out the base methods.
      int left = iX(-rWidth / 2), right = iX(rWidth / 2), 
          bottom = iY(-rHeight / 2), top = iY(rHeight / 2);
      //g.drawRect(left, top, right - left, bottom - top);
      int n = v.size();
      if (n == 0)
         return;
      Point2D a = (Point2D) (v.elementAt(0));
      // Show tiny rectangle around first vertex:
      //g.drawRect(iX(a.x) - 2, iY(a.y) - 2, 4, 4);
      for (int i = 1; i <= n && i<3; i++) {
         if (i == n && !ready)
            break;
         Point2D b = (Point2D) (v.elementAt(i % n));
         g.drawLine(iX(a.x), iY(a.y), iX(b.x), iY(b.y));
         //g.drawRect(iX(b.x) - 2, iY(b.y) - 2, 4, 4); // Tiny rectangle; added
         a = b;
         //g.drawString(""+(i%n), iX(b.x), iY(b.y));// to test.......
      }
	if (n>2) { //at least 3, in which case complete the triangle.
		Point2D b=(Point2D) (v.elementAt(2));
		a=(Point2D) (v.elementAt(0));
		g.drawLine(iX(a.x), iY(a.y), iX(b.x), iY(b.y));
		if (n>3) {
			Point2D P=(Point2D) (v.elementAt(3));
			g.drawLine(iX(P.x), iY(P.y)-5, iX(P.x), iY(P.y)+7); //vertical part of cross
			g.drawLine(iX(P.x)-3, iY(P.y), iX(P.x)+3, iY(P.y)); //horizontal part of cross
			Point2D c=(Point2D) (v.elementAt(1));
			float abc=area2(a, b, c);
			float apc=area2(a, P, c);
			float abp=area2(a, b, P);
			float bcp=area2(b, c, P);
			if (sign(abc)==sign(apc) && sign(abp)==sign(bcp) && sign(apc)==sign(abp) && sign(abc)!=0) {
				g.drawString("P lies inside ABC", iX(P.x), iY(P.y));
			} else if (sign(apc)*sign(abp)*sign(bcp)==0) {
				g.drawString("P lies on an edge of ABC", iX(P.x), iY(P.y));
                        } else {
				g.drawString("P lies outside ABC", iX(P.x), iY(P.y));
                        }
		}
	}
	//Class assignment 2
	//g.drawLine(10, 20, 15, 25);
	//g.drawRect(20, 40, 7, 9);
	//g.fillRect(20, 40, 7, 9);
	
	//Homework 1:
	//Draw a set of concentric pairs of squares, each consisting of a square with horizontal and vertical edges 
	//and one rotated through 45 degrees. Except for the outermost square, the vertices of each square are the 
	//midpoints of the edges of its immediately surrounding square, as Fig 1.12 shows. It is required that all lines are
	//exactly straight, and that vertices of smaller squares lie exactly on the edges of larger ones.
	//g.setColor(Color.BLACK);
	//Now what gets drawn will show up on screen
	//There are 13 concentric squares with 1 white pixel in the smallest square with the 
	//smallest square having horizontal top and vertical sides.
	//int x1=-2, y1=2, x2=2, y2=2, x3=2, y3=-2, x4=-2, y4=-2;
	//for (int i=1; i<=13; i++) {
	//	g.drawLine(x1+centerX, y1+centerX, x2+centerX, y2+centerX);
	//	g.drawLine(x2+centerX, y2+centerX, x3+centerX, y3+centerX);
	//	g.drawLine(x3+centerX, y3+centerX, x4+centerX, y4+centerX);
	//	g.drawLine(x4+centerX, y4+centerX, x1+centerX, y1+centerX);
	//	if (i%2==0) { //odd values are next, so set up for the odd square 
	//			//(flat-topped) given the value of the even (45 degree) square
	//		y1=y2;
	//		x2=y2;
	//		y3=y4;
	//		x4=y4;
	//	} else { //given flat-topped square, find the even (45 degree) square
	//		x1*=2;
	//		y1=0;
	//		x2=0;
	//		y2*=2;
	//		x3*=2;
	//		y3=0;
	//		x4=0;
	//		y4*=2;
	//	}
	//}
   }
}
