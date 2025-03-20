package meteordevelopment.meteorclient.gui.utils;

import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2487;

public class WindowConfig implements ISerializable<WindowConfig> {
   public boolean expanded = true;
   public double x = -1.0D;
   public double y = -1.0D;

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10556("expanded", this.expanded);
      tag.method_10549("x", this.x);
      tag.method_10549("y", this.y);
      return tag;
   }

   public WindowConfig fromTag(class_2487 tag) {
      this.expanded = tag.method_10577("expanded");
      this.x = tag.method_10574("x");
      this.y = tag.method_10574("y");
      return this;
   }
}
