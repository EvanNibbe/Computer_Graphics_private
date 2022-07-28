// HLinesDashed.java: Perspective drawing with hidden-line elimination.
// Hidden lines are drawn as dashed lines.
import java.awt.*;

public class HLinesDashed extends Frame {
   public static void main(String[] args) {
      new Fr3DHDashed(args.length > 0 ? args[0] : null, 
             new CvHLinesDashed(), "Hidden-lines dashed");
   }
}


