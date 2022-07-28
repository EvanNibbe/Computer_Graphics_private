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
	static double[] matmul(double[] A, int rowA, int colA, double[] B, int rowB, int colB) {
			double[] res=new double[rowA*colB];
			for (int i=0; i<rowA; i++) {
				for (int j=0; j<colB; j++) {
					res[i*colB+j]=0;
					for (int k=0; k<colA && k<rowB; k++) {
						res[i*colB+j]+=A[i*colA+k]*B[k*colB+j];
					}
				}
			}
			return res;
		}
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
	class Triple {
		public Point2D c, d, e;
		Triple(Point2D a, Point2D b) {
			double dist=Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y));
			double[] rotB_neg90={0, -1, 0, 1, 0, 0, -b.x*0+b.y*(-1)+b.x, -b.x*(-1)-b.y*0+b.y, 1};
			double[] hA={a.x, a.y, 1};
			double[] C=matmul(hA, 1, 3, rotB_neg90, 3, 3);
			double[] rotA_90={0, 1, 0, -1, 0, 0, -a.x*0+a.y*1+a.x, -a.x*1-a.y*0+a.y, 1};
			double[] hB={b.x, b.y, 1};
			double[] D=matmul(hB, 1, 3, rotA_90, 3, 3);
			//E is then created by drawing a dist*sqrt(2)/2 line along the top of DE starting at D,
			//then rotate that point 45 degrees up with respect to D
			double[] E={D[0], D[1], 1};
			double[] CD={C[0]-D[0], C[1]-D[1]};
			double cd_dist=Math.sqrt((CD[0]*CD[0])+(CD[1]*CD[1]));
			CD[0]/=cd_dist;
			CD[1]/=cd_dist;
			double[] move={1, 0, 0, 0, 1, 0, dist*CD[0]/Math.sqrt(2), dist*CD[1]/Math.sqrt(2), 1};
			double[] rot={Math.cos(Math.PI/4), Math.sin(Math.PI/4), 0, -Math.sin(Math.PI/4), Math.cos(Math.PI/4), 0, -D[0]*Math.cos(Math.PI/4)+D[1]*Math.sin(Math.PI/4)+D[0], -D[0]*Math.sin(Math.PI/4)-D[1]*Math.cos(Math.PI/4)+D[1], 1};
			E=matmul(E, 1, 3, move, 3, 3);
			E=matmul(E, 1, 3, rot, 3, 3);
			c=new Point2D((float)(C[0]), (float)(C[1]));
			d=new Point2D((float)(D[0]), (float)(D[1]));
			e=new Point2D((float)(E[0]), (float)(E[1]));
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
            if (v.size() < 2) {
               v.addElement(new Point2D(xA, yA));
            } 
            
            // Added December 2016:
            if(evt.getModifiers()==InputEvent.BUTTON3_MASK) {
               ready = true;
            }
		if (v.size() < 3) {
			repaint();
		}
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
      if (n <2)
         return;
      Point2D a = (Point2D) (v.elementAt(0));
	Point2D b=(Point2D) (v.elementAt(1));
      g.drawLine(iX(a.x), iY(a.y), iX(b.x), iY(b.y));
      // Show tiny rectangle around first vertex:
      //g.drawRect(iX(a.x) - 2, iY(a.y) - 2, 4, 4);
      //for (int i = 1; i <= n && i<3; i++) {
      //   if (i == n && !ready)
      //      break;
      //   b = (Point2D) (v.elementAt(i % n));
      //   g.drawLine(iX(a.x), iY(a.y), iX(b.x), iY(b.y));
         //g.drawRect(iX(b.x) - 2, iY(b.y) - 2, 4, 4); // Tiny rectangle; added
         //a = b;
         //g.drawString(""+(i%n), iX(b.x), iY(b.y));// to test.......
      //}
	float s0=(float)(Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y)));
	if (s0>.001) { 
		//The plan is to use pseudo-recursion, where a function
		//takes in the points A and B, then outputs the corresponding C, D, E for the 
		//isoceles triange on top of the square ABCD
		//CE and ED are then pushed to the end of a queue
		//once the drawing is finished, the next pair of points are pulled out of the queue 
		//and used the same way as A and B, provided that the two points are at least .05 away from each other.
		//the process ends when there are no more points in the queue (if the points go below .05 from each other, and thus skipped), or when the queue reaches 10000 points, whichever is earlier.
		Vector<Point2D> points = new Vector<Point2D>();
		Triple cde=new Triple(a, b);
		points.addElement(cde.e); 
		points.addElement(cde.c);
		points.addElement(cde.d);
		points.addElement(cde.e);
		int count=0;
		while (count<points.size() && points.size()<100000) {
			g.drawLine(iX(b.x), iY(b.y), iX(cde.c.x), iY(cde.c.y));
			g.drawLine(iX(cde.c.x), iY(cde.c.y), iX(cde.d.x), iY(cde.d.y));
			g.drawLine(iX(a.x), iY(a.y), iX(cde.d.x), iY(cde.d.y));
			g.drawLine(iX(cde.c.x), iY(cde.c.y), iX(cde.e.x), iY(cde.e.y));
			g.drawLine(iX(cde.e.x), iY(cde.e.y), iX(cde.d.x), iY(cde.d.y));
			a=points.elementAt(count);
			b=points.elementAt(count+1);
			cde=new Triple(a, b);
			s0=(float)(Math.sqrt((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y)));
			if (s0>.001) {
				points.addElement(cde.e); 
				points.addElement(cde.c);
				points.addElement(cde.d);
				points.addElement(cde.e);
			}
			count+=2;
		}


		//The following stuff is leftover from HW2
		//g.drawLine(iX(a.x), iY(a.y), iX(b.x), iY(b.y));
		//if (n>3) {
		//	Point2D P=(Point2D) (v.elementAt(3));
		//	g.drawLine(iX(P.x), iY(P.y)-5, iX(P.x), iY(P.y)+7); //vertical part of cross
		//	g.drawLine(iX(P.x)-3, iY(P.y), iX(P.x)+3, iY(P.y)); //horizontal part of cross
		//	Point2D c=(Point2D) (v.elementAt(1));
		//	float abc=area2(a, b, c);
		//	float apc=area2(a, P, c);
		//	float abp=area2(a, b, P);
		//	float bcp=area2(b, c, P);
		//	if (sign(abc)==sign(apc) && sign(abp)==sign(bcp) && sign(apc)==sign(abp) && sign(abc)!=0) {
		//		g.drawString("P lies inside ABC", iX(P.x), iY(P.y));
		//	} else if (sign(apc)*sign(abp)*sign(bcp)==0) {
		//		g.drawString("P lies on an edge of ABC", iX(P.x), iY(P.y));
                  //      } else {
		//		g.drawString("P lies outside ABC", iX(P.x), iY(P.y));
                  //      }
		//}
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
