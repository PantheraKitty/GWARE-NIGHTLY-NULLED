package meteordevelopment.meteorclient.utils.render.color;

import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.PostInit;
import meteordevelopment.meteorclient.utils.misc.UnorderedArrayList;
import meteordevelopment.orbit.EventHandler;

public class RainbowColors {
   private static final List<Setting<SettingColor>> colorSettings = new UnorderedArrayList();
   private static final List<Setting<List<SettingColor>>> colorListSettings = new UnorderedArrayList();
   private static final List<SettingColor> colors = new UnorderedArrayList();
   private static final List<Runnable> listeners = new UnorderedArrayList();
   public static final RainbowColor GLOBAL = new RainbowColor();

   private RainbowColors() {
   }

   @PostInit
   public static void init() {
      MeteorClient.EVENT_BUS.subscribe(RainbowColors.class);
   }

   public static void addSetting(Setting<SettingColor> setting) {
      colorSettings.add(setting);
   }

   public static void addSettingList(Setting<List<SettingColor>> setting) {
      colorListSettings.add(setting);
   }

   public static void removeSetting(Setting<SettingColor> setting) {
      colorSettings.remove(setting);
   }

   public static void removeSettingList(Setting<List<SettingColor>> setting) {
      colorListSettings.remove(setting);
   }

   public static void add(SettingColor color) {
      colors.add(color);
   }

   public static void register(Runnable runnable) {
      listeners.add(runnable);
   }

   @EventHandler
   private static void onTick(TickEvent.Post event) {
      GLOBAL.setSpeed((Double)Config.get().rainbowSpeed.get() / 100.0D);
      GLOBAL.getNext();
      Iterator var1 = colorSettings.iterator();

      while(true) {
         Setting setting;
         do {
            if (!var1.hasNext()) {
               var1 = colorListSettings.iterator();

               while(true) {
                  Iterator var3;
                  do {
                     if (!var1.hasNext()) {
                        var1 = colors.iterator();

                        while(var1.hasNext()) {
                           SettingColor color = (SettingColor)var1.next();
                           color.update();
                        }

                        var1 = Waypoints.get().iterator();

                        while(var1.hasNext()) {
                           Waypoint waypoint = (Waypoint)var1.next();
                           ((SettingColor)waypoint.color.get()).update();
                        }

                        if (MeteorClient.mc.field_1755 instanceof WidgetScreen) {
                           var1 = GuiThemes.get().settings.iterator();

                           while(var1.hasNext()) {
                              SettingGroup group = (SettingGroup)var1.next();
                              var3 = group.iterator();

                              while(var3.hasNext()) {
                                 Setting<?> setting = (Setting)var3.next();
                                 if (setting instanceof ColorSetting) {
                                    ((SettingColor)setting.get()).update();
                                 }
                              }
                           }
                        }

                        var1 = listeners.iterator();

                        while(var1.hasNext()) {
                           Runnable listener = (Runnable)var1.next();
                           listener.run();
                        }

                        return;
                     }

                     setting = (Setting)var1.next();
                  } while(setting.module != null && !setting.module.isActive());

                  var3 = ((List)setting.get()).iterator();

                  while(var3.hasNext()) {
                     SettingColor color = (SettingColor)var3.next();
                     color.update();
                  }
               }
            }

            setting = (Setting)var1.next();
         } while(setting.module != null && !setting.module.isActive());

         ((SettingColor)setting.get()).update();
      }
   }
}
