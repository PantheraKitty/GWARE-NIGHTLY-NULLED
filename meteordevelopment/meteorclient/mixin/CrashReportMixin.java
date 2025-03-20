package meteordevelopment.meteorclient.mixin;

import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.class_128;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_128.class})
public abstract class CrashReportMixin {
   @Inject(
      method = {"addDetails"},
      at = {@At("TAIL")}
   )
   private void onAddDetails(StringBuilder sb, CallbackInfo info) {
      sb.append("\n\n-- Meteor Client --\n\n");
      sb.append("Version: ").append(MeteorClient.VERSION).append("\n");
      if (!MeteorClient.DEV_BUILD.isEmpty()) {
         sb.append("Dev Build: ").append(MeteorClient.DEV_BUILD).append("\n");
      }

      boolean hudActive;
      Iterator var4;
      if (Modules.get() != null) {
         hudActive = false;
         var4 = Modules.loopCategories().iterator();

         while(var4.hasNext()) {
            Category category = (Category)var4.next();
            List<Module> modules = Modules.get().getGroup(category);
            boolean categoryActive = false;
            Iterator var8 = modules.iterator();

            while(var8.hasNext()) {
               Module module = (Module)var8.next();
               if (module != null && module.isActive()) {
                  if (!hudActive) {
                     hudActive = true;
                     sb.append("\n[[ Active Modules ]]\n");
                  }

                  if (!categoryActive) {
                     categoryActive = true;
                     sb.append("\n[").append(category).append("]:\n");
                  }

                  sb.append(module.name).append("\n");
               }
            }
         }
      }

      if (Hud.get() != null && Hud.get().active) {
         hudActive = false;
         var4 = Hud.get().iterator();

         while(var4.hasNext()) {
            HudElement element = (HudElement)var4.next();
            if (element != null && element.isActive()) {
               if (!hudActive) {
                  hudActive = true;
                  sb.append("\n[[ Active Hud Elements ]]\n");
               }

               if (element instanceof TextHud) {
                  TextHud textHud = (TextHud)element;
                  sb.append("Text\n{").append((String)textHud.text.get()).append("}\n");
                  if (textHud.shown.get() != TextHud.Shown.Always) {
                     sb.append("(").append(textHud.shown.get()).append((String)textHud.condition.get()).append(")\n");
                  }
               } else {
                  sb.append(element.info.name).append("\n");
               }
            }
         }
      }

   }
}
