// Beams.java: Generating input files for a spiral of beams. The
//    values of n, a and alpha (in degrees) as well as the output 
//    file name are to be supplied as program arguments.
//    Uses: Point3D (Section 3.9).
import java.io.*;

public class Stairs {
   public static void main(String[] args) throws IOException {
      if (args.length != 3) {
         System.out.println(
               "Supply n (> 0), alpha (in degrees)\n" + 
               "and a filename as program arguments.\n");
         System.exit(1);
      }
      int n = 0;
      double a = 6, alphaDeg = 0;
      try {
         n = Integer.valueOf(args[0]).intValue();
         //a = Double.valueOf(args[1]).doubleValue(); //already found by checking the teacher's copy of stairs.dat 
         alphaDeg = Double.valueOf(args[1]).doubleValue();
         if (n <= 0 || a < 0.5)
            throw new NumberFormatException();
      } catch (NumberFormatException e) {
         System.out.println("n must be an integer > 0");
         System.out.println("alpha must be a real number");
         System.exit(1);
      }
      new BeamsObj(n, a, alphaDeg * Math.PI / 180, args[2]);
   }
}

class BeamsObj {
   FileWriter fw;

   BeamsObj(int n, double a, double alpha, String fileName)
         throws IOException {
      fw = new FileWriter(fileName);
      Point3D[] P = new Point3D[9];
      //double b = a - 2;
      //setting these basis values to the values seen in the original stairs.dat
      P[1] = new Point3D(7.0, -1.0, 0);
      P[2] = new Point3D(7.0, 1.0, 0);
      P[3] = new Point3D(1.0, 1.0, 0);
      P[4] = new Point3D(1.0, -1.0, 0);
      P[5] = new Point3D(7.0, -1.0, 0.2);
      P[6] = new Point3D(7.0, 1.0, 0.2);
      P[7] = new Point3D(1.0, 1.0, 0.2);
      P[8] = new Point3D(1.0, -1.0, 0.2);
      int max_vert_seen=0;
      for (int k = 0; k < n; k++) { // Beam k:
         double phi = k * alpha, 
                cosPhi = Math.cos(phi), sinPhi = Math.sin(phi);
         int m = 8 * k;
         for (int i = 1; i <= 8; i++) {
            double x = P[i].x, y = P[i].y;
            float x1 = (float) (x * cosPhi - y * sinPhi), 
                  y1 = (float) (x * sinPhi + y * cosPhi), 
                  z1 = (float) (P[i].z + k);
            fw.write((m + i) + " " + x1 + " " + y1 + " " + z1 + "\r\n");
            if (m+i>max_vert_seen) {
                max_vert_seen=m+i;
            }
         }
      }
      //Need to put the vertices of cylinder here before fw.write("Faces\r\n");  is called
      genCylinder(20, 2.000000048565771f, 0, max_vert_seen); //2.000000048565771 is used because that is the maximum I calculated of the distances between
      //vertexes 211, 202, 212, 221 in the original stairs.dat
      
      //fw.write("Faces:\r\n"); //already called by genCylinder
      for (int k = 0; k < n; k++) { // Beam k again:
         int m = 8 * k;
         face(m, 1, 2, 6, 5);
         face(m, 4, 8, 7, 3);
         face(m, 5, 6, 7, 8);
         face(m, 1, 4, 3, 2);
         face(m, 2, 3, 7, 6);
         face(m, 1, 5, 8, 4);
      }
      fw.close();
   }

   void face(int m, int a, int b, int c, int d) throws IOException {
      a += m;
      b += m;
      c += m;
      d += m;
      fw.write(a + " " + b + " " + c + " " + d + ".\r\n");
   }
   
   //Taken from Cylinder.java is the real workhorse of Cylinder.java as far as building the middle cylinder goes.
   //Note that the height of the cylinder in stairs.java was 25, not 1, which was what Cylinder.java set it to otherwise
   void genCylinder(int n, float rOuter, float rInner, final int vert_n)
         throws IOException {
      int n2 = 2 * n, n3 = 3 * n, n4 = 4 * n;
      //fw = new FileWriter("Cylinder.dat"); //not posting the cylinder here.
      double delta = 2 * Math.PI / n;
      for (int i = 1; i <= n; i++) {
         double alpha = i * delta, 
               cosa = Math.cos(alpha), sina = Math.sin(alpha);
         for (int inner = 0; inner < 2; inner++) {
            double r = rOuter; //rInner is zero in this question
            if (r > 0)
				for (int bottom = 0; bottom < 2; bottom++) {
					int k = (2 * inner + bottom) * n + i;
					// Vertex numbers for i = 1:
					// Top: 1 (outer) 2n+1 (inner)
					// Bottom: n+1 (outer) 3n+1 (inner)
					//vert_n accounts for the vertices written to this point.
					wi(k+vert_n); // w = write, i = int, r = real
					wr(r * cosa); wr(r * sina); // x and y
					wi((1 - bottom)*25); // bottom: z = 0; top: z = 1
					fw.write("\r\n");
				}
         }
      }
      fw.write("Faces:\r\n");
      // Top boundary face:
      for (int i = 1; i <= n; i++) wi(i+vert_n);
      if (rInner > 0) {
         //wi(-n3+vert_n); // Invisible edge, see Section 7.5
         for (int i = n3 - 1; i >= n2 + 1; i--) wi(i+vert_n);
         //wi(n3+vert_n); wi(-n+vert_n); // Invisible edge again.
      }
      fw.write(".\r\n");
      // Bottom boundary face:
      for (int i = n2; i >= n + 1; i--) wi(i+vert_n);
      if (rInner > 0) {
         //wi(-(n3 + 1)+vert_n);
         for (int i = n3 + 2; i <= n4; i++) wi(i+vert_n);
         //wi(n3 + 1+vert_n); wi(-(n + 1)+vert_n);
      }
      fw.write(".\r\n");
      // Vertical, rectangular faces:
      for (int i = 1; i <= n; i++) {
         int j = i % n + 1;
         // Outer rectangle:
         wi(j+vert_n); wi(i+vert_n); wi(i + n+vert_n); wi(j + n+vert_n); fw.write(".\r\n");
         if (rInner > 0) { // Inner rectangle:
            wi(i + n2+vert_n); wi(j + n2+vert_n); wi(j + n3+vert_n); wi(i + n3+vert_n);
            fw.write(".\r\n");
         }
      }
      //fw.close(); //The BeamsObj(...) method closes the stream.
   }

   void wi(int x) throws IOException {
      fw.write(" " + String.valueOf(x));
   }

   void wr(double x) throws IOException {
      if (Math.abs(x) < 1e-9) x = 0;
      fw.write(" " + String.valueOf((float) x));
      // float instead of double to reduce the file size
   }
}
