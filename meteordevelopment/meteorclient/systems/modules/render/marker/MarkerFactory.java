package meteordevelopment.meteorclient.systems.modules.render.marker;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class MarkerFactory {
   private final Map<String, MarkerFactory.Factory> factories = new HashMap();
   private final String[] names;

   public MarkerFactory() {
      this.factories.put("Cuboid", CuboidMarker::new);
      this.factories.put("Sphere-2D", Sphere2dMarker::new);
      this.names = new String[this.factories.size()];
      int i = 0;

      String key;
      for(Iterator var2 = this.factories.keySet().iterator(); var2.hasNext(); this.names[i++] = key) {
         key = (String)var2.next();
      }

   }

   public String[] getNames() {
      return this.names;
   }

   public BaseMarker createMarker(String name) {
      if (this.factories.containsKey(name)) {
         BaseMarker marker = ((MarkerFactory.Factory)this.factories.get(name)).create();
         marker.settings.registerColorSettings(Modules.get().get(Marker.class));
         return marker;
      } else {
         return null;
      }
   }

   private interface Factory {
      BaseMarker create();
   }
}
