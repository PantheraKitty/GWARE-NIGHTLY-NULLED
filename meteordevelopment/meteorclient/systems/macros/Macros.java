package meteordevelopment.meteorclient.systems.macros;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2487;
import org.jetbrains.annotations.NotNull;

public class Macros extends System<Macros> implements Iterable<Macro> {
   private List<Macro> macros = new ArrayList();

   public Macros() {
      super("macros");
   }

   public static Macros get() {
      return (Macros)Systems.get(Macros.class);
   }

   public void add(Macro macro) {
      this.macros.add(macro);
      MeteorClient.EVENT_BUS.subscribe((Object)macro);
      this.save();
   }

   public Macro get(String name) {
      Iterator var2 = this.macros.iterator();

      Macro macro;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         macro = (Macro)var2.next();
      } while(!((String)macro.name.get()).equalsIgnoreCase(name));

      return macro;
   }

   public List<Macro> getAll() {
      return this.macros;
   }

   public void remove(Macro macro) {
      if (this.macros.remove(macro)) {
         MeteorClient.EVENT_BUS.unsubscribe((Object)macro);
         this.save();
      }

   }

   @EventHandler(
      priority = 100
   )
   private void onKey(KeyEvent event) {
      if (event.action != KeyAction.Release) {
         Iterator var2 = this.macros.iterator();

         Macro macro;
         do {
            if (!var2.hasNext()) {
               return;
            }

            macro = (Macro)var2.next();
         } while(!macro.onAction(true, event.key, event.modifiers));

      }
   }

   @EventHandler(
      priority = 100
   )
   private void onButton(MouseButtonEvent event) {
      if (event.action != KeyAction.Release) {
         Iterator var2 = this.macros.iterator();

         Macro macro;
         do {
            if (!var2.hasNext()) {
               return;
            }

            macro = (Macro)var2.next();
         } while(!macro.onAction(false, event.button, 0));

      }
   }

   public boolean isEmpty() {
      return this.macros.isEmpty();
   }

   @NotNull
   public Iterator<Macro> iterator() {
      return this.macros.iterator();
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("macros", NbtUtils.listToTag(this.macros));
      return tag;
   }

   public Macros fromTag(class_2487 tag) {
      Iterator var2 = this.macros.iterator();

      Macro macro;
      while(var2.hasNext()) {
         macro = (Macro)var2.next();
         MeteorClient.EVENT_BUS.unsubscribe((Object)macro);
      }

      this.macros = NbtUtils.listFromTag(tag.method_10554("macros", 10), Macro::new);
      var2 = this.macros.iterator();

      while(var2.hasNext()) {
         macro = (Macro)var2.next();
         MeteorClient.EVENT_BUS.subscribe((Object)macro);
      }

      return this;
   }
}
