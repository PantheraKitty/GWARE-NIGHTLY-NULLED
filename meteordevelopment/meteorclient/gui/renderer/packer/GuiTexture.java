package meteordevelopment.meteorclient.gui.renderer.packer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GuiTexture {
   private final List<TextureRegion> regions = new ArrayList(2);

   void add(TextureRegion region) {
      this.regions.add(region);
   }

   public TextureRegion get(double width, double height) {
      double targetDiagonal = Math.sqrt(width * width + height * height);
      double closestDifference = Double.MAX_VALUE;
      TextureRegion closestRegion = null;
      Iterator var10 = this.regions.iterator();

      while(var10.hasNext()) {
         TextureRegion region = (TextureRegion)var10.next();
         double difference = Math.abs(targetDiagonal - region.diagonal);
         if (difference < closestDifference) {
            closestDifference = difference;
            closestRegion = region;
         }
      }

      return closestRegion;
   }
}
