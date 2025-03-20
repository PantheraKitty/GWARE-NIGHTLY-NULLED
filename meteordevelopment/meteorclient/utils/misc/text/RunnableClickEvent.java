package meteordevelopment.meteorclient.utils.misc.text;

import net.minecraft.class_2558.class_2559;

public class RunnableClickEvent extends MeteorClickEvent {
   public final Runnable runnable;

   public RunnableClickEvent(Runnable runnable) {
      super((class_2559)null, (String)null);
      this.runnable = runnable;
   }
}
