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
	final int dev_top=2;
	final int dev_right_rect=80;
	final int dev_bottom_rect=50;
   int centerX, centerY;
   int generations=2;
   static final Color GREY=new Color(223, 223, 223);
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
	static float sqrt(float a) {
		return (float)Math.sqrt(a);
	}
	static float abs(float a) {
		return (float)Math.abs(a);
	}
	static float pow(float a, float p) {
		return (float)Math.pow(a, p);
	}
	static Point2D get_C(Point2D A, Point2D B) {
		double[] c=new double[3];
		c[0]=1;
		c[1]=Math.sqrt(3);
		c[2]=1;
		float d=(float)(Math.sqrt((A.x-B.x)*(A.x-B.x)+(A.y-B.y)*(A.y-B.y)))/2.0f; //Using the fact that an equilateral triangle is formed
		double theta=Math.atan2(B.y-A.y, B.x-A.x);
		double[] rot={Math.cos(theta), Math.sin(theta), 0, -Math.sin(theta), Math.cos(theta), 0, 0, 0, 1};
		double[] scale={d, 0, 0, 0, d, 0, 0, 0, 1};
		double[] trans={1, 0, 0, 0, 1, 0, A.x, A.y, 1};
		c=matmul(c, 1, 3, matmul(rot, 3, 3, matmul(scale, 3, 3, trans, 3, 3), 3, 3), 3, 3);
		//from
		return new Point2D((float)c[0], (float)c[1]);
		//along the line
		//float f=(float x, A, B, mid_p)->-(B.x-A.x)/(B.y-A.y)*(x-mid_p.x)+mid_p.y; //lambda of the line's function.
		
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
		//takes a line and makes .d the new point 1/3rd of the way in
		//.e the new point 2/3rds of the way in
		//.c the new point C created outside.
	Triple cut_line(Point2D A, Point2D B) {
		Triple res=new Triple();
		res.d.x=(A.x*2.0f+B.x)/3.0f;
		res.d.y=(A.y*2.0f+B.y)/3.0f;
		res.e.x=(B.x*2.0f+A.x)/3.0f;
		res.e.y=(B.y*2.0f+A.y)/3.0f;
		res.c=get_C(res.d, res.e);
		return res;
	}
	void draw_triangle(Graphics2D g, Point2D A, Point2D B) {
		Point2D C=get_C(A, B);
		g.setStroke(new BasicStroke(2)); //more thickness means getting rid of more black pixels, and thus a clearer picture
		g.setColor(GREY);
		//the interpolation avoids getting rid of the corner pixels
		g.drawLine(iX((A.x*99f+B.x)/100.0f), iY((A.y*99f+B.y)/100.0f), iX((B.x*99f+A.x)/100.0f), iY((B.y*99+A.y)/100.0f));
		g.setStroke(new BasicStroke(1)); //reset thickness
		g.setColor(Color.BLACK);
		g.drawLine(iX(A.x), iY(A.y), iX(C.x), iY(C.y));
		g.drawLine(iX(B.x), iY(B.y), iX(C.x), iY(C.y));
	}
	class Triple {
		public Point2D c, d, e;
		Triple() {
			c=new Point2D(0f, 0f);
			d=new Point2D(0f, 0f);
			e=new Point2D(0f, 0f);
		}
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
            if (v.size() < 10) {
		if (v.size() < 3) {
	               v.addElement(new Point2D(xA, yA));
		} else {
			v.set(v.size()-1, new Point2D(xA, yA));
			generations+=1;
		}
		//Point2D a=(Point2D) (v.elementAt(v.size()-1));
		//if (iX(a.x)<dev_right_rect) {
		//	if (iY(a.y)<(dev_top+dev_bottom_rect)/2) {
		//		v.clear(); //Pythagoras is chosen
		//	} else if (iY(a.y)<dev_bottom_rect+dev_top) {
		//		System.exit(0);
		//	}
		//}
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

   public void paint(Graphics g1) {
	   
	   Graphics2D g=(Graphics2D) g1; //allows for setting the brush size with .setStroke
      initgr();
	//g.setColor(new Color(223, 223, 223)); //this is done in leiu of commenting out the base methods.
      int left = iX(-rWidth / 2), right = iX(rWidth / 2), 
          bottom = iY(-rHeight / 2), top = iY(rHeight / 2);
      //g.drawRect(left, top, right - left, bottom - top);
      int n = v.size();
	//For HW3, I need to draw a rectangle in the corner big enough to have "Pythagoras"
	//and "Quit" (separate line below Pythagoras)
	//and have a line between them. If a point exists and is within the device coordinates of one of those partitions,
	//then "Pythagoras" will clear the points (allowing the user to choose two new points)
	//and "Quit" will run System.exit(0);
	//g.drawRect(dev_top, dev_top, dev_right_rect, dev_bottom_rect); //to surround "Pythagoras and "Quit"
	//g.drawLine(dev_top, (dev_top+dev_bottom_rect)/2, dev_right_rect+1, (dev_top+dev_bottom_rect)/2);
	//g.drawString("Pythagoras", dev_top+8, dev_top+12);
	//g.drawString("Quit", dev_top+8, (dev_top+dev_bottom_rect)/2+12);
      if (n <2)
         return;
      Point2D a = (Point2D) (v.elementAt(0));
	Point2D b=(Point2D) (v.elementAt(1));
      g.drawLine(iX(a.x), iY(a.y), iX(b.x), iY(b.y));
      Point2D C=get_C(a, b);
     
      g.drawLine(iX(a.x), iY(a.y), iX(C.x), iY(C.y));
      g.drawLine(iX(b.x), iY(b.y), iX(C.x), iY(C.y));
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
		Vector<Point2D> prev_gen = new Vector<Point2D>();
		prev_gen.addElement(b);
		prev_gen.addElement(a);
		prev_gen.addElement(C);
		prev_gen.addElement(b);
		Vector<Point2D> next_gen = new Vector<Point2D>();
		int count=0;
		while (prev_gen.size()<10000 && count<generations) {
			count+=1;
			int index=0;
			while (index<prev_gen.size()-1) {
				a=prev_gen.elementAt(index);
				b=prev_gen.elementAt(index+1);
				if ((a.x-b.x)*(a.x-b.x)+(a.y-b.y)*(a.y-b.y)>.0001) {
					Triple mid_p=cut_line(a, b);
					next_gen.addElement(a);
					next_gen.addElement(mid_p.d);
					next_gen.addElement(mid_p.c);
					next_gen.addElement(mid_p.e);
					next_gen.addElement(b);
					draw_triangle(g, mid_p.d, mid_p.e);
				}
				index+=1;
			}
			prev_gen.removeAllElements();
			prev_gen=next_gen;
			next_gen=new Vector<Point2D>();
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
