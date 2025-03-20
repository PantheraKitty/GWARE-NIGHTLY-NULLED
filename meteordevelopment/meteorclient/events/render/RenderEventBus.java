package meteordevelopment.meteorclient.events.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class RenderEventBus {
   private static final List<Consumer<RenderEvent>> listeners = new ArrayList();

   public static void register(Consumer<RenderEvent> listener) {
      listeners.add(listener);
   }

   public static void post(RenderEvent event) {
      Iterator var1 = listeners.iterator();

      while(var1.hasNext()) {
         Consumer<RenderEvent> listener = (Consumer)var1.next();
         listener.accept(event);
      }

   }
}
