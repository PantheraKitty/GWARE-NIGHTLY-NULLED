package meteordevelopment.meteorclient.systems.modules.render.marker;

import java.util.ArrayList;
import java.util.Iterator;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.input.WDropdown;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;

public class Marker extends Module {
   private final MarkerFactory factory = new MarkerFactory();
   private final ArrayList<BaseMarker> markers = new ArrayList();

   public Marker() {
      super(Categories.Render, "marker", "Renders shapes. Useful for large scale projects");
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      Iterator var2 = this.markers.iterator();

      while(var2.hasNext()) {
         BaseMarker marker = (BaseMarker)var2.next();
         if (marker.isVisible()) {
            marker.tick();
         }
      }

   }

   @EventHandler
   private void onRender(Render3DEvent event) {
      Iterator var2 = this.markers.iterator();

      while(var2.hasNext()) {
         BaseMarker marker = (BaseMarker)var2.next();
         if (marker.isVisible()) {
            marker.render(event);
         }
      }

   }

   public class_2487 toTag() {
      class_2487 tag = super.toTag();
      class_2499 list = new class_2499();
      Iterator var3 = this.markers.iterator();

      while(var3.hasNext()) {
         BaseMarker marker = (BaseMarker)var3.next();
         class_2487 mTag = new class_2487();
         mTag.method_10582("type", marker.getTypeName());
         mTag.method_10566("marker", marker.toTag());
         list.add(mTag);
      }

      tag.method_10566("markers", list);
      return tag;
   }

   public Module fromTag(class_2487 tag) {
      super.fromTag(tag);
      this.markers.clear();
      class_2499 list = tag.method_10554("markers", 10);
      Iterator var3 = list.iterator();

      while(var3.hasNext()) {
         class_2520 tagII = (class_2520)var3.next();
         class_2487 tagI = (class_2487)tagII;
         String type = tagI.method_10558("type");
         BaseMarker marker = this.factory.createMarker(type);
         if (marker != null) {
            class_2487 markerTag = (class_2487)tagI.method_10580("marker");
            if (markerTag != null) {
               marker.fromTag(markerTag);
            }

            this.markers.add(marker);
         }
      }

      return this;
   }

   public WWidget getWidget(GuiTheme theme) {
      WVerticalList list = theme.verticalList();
      this.fillList(theme, list);
      return list;
   }

   protected void fillList(GuiTheme theme, WVerticalList list) {
      BaseMarker marker;
      WMinus remove;
      for(Iterator var3 = this.markers.iterator(); var3.hasNext(); remove.action = () -> {
         this.markers.remove(marker);
         marker.settings.unregisterColorSettings();
         list.clear();
         this.fillList(theme, list);
      }) {
         marker = (BaseMarker)var3.next();
         WHorizontalList hList = (WHorizontalList)list.add(theme.horizontalList()).expandX().widget();
         WLabel label = (WLabel)hList.add(theme.label((String)marker.name.get())).widget();
         label.tooltip = (String)marker.description.get();
         ((WLabel)hList.add(theme.label(" - " + marker.getDimension().toString())).expandX().widget()).color = theme.textSecondaryColor();
         WCheckbox checkbox = (WCheckbox)hList.add(theme.checkbox(marker.isActive())).widget();
         checkbox.action = () -> {
            if (marker.isActive() != checkbox.checked) {
               marker.toggle();
            }

         };
         WButton edit = (WButton)hList.add(theme.button(GuiRenderer.EDIT)).widget();
         edit.action = () -> {
            this.mc.method_1507(marker.getScreen(theme));
         };
         remove = (WMinus)hList.add(theme.minus()).widget();
      }

      WHorizontalList bottom = (WHorizontalList)list.add(theme.horizontalList()).expandX().widget();
      WDropdown<String> newMarker = (WDropdown)bottom.add(theme.dropdown(this.factory.getNames(), this.factory.getNames()[0])).widget();
      WButton add = (WButton)bottom.add(theme.button("Add")).expandX().widget();
      add.action = () -> {
         String name = (String)newMarker.get();
         this.markers.add(this.factory.createMarker(name));
         list.clear();
         this.fillList(theme, list);
      };
   }
}
