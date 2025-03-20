package meteordevelopment.meteorclient.systems.modules.render;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.screens.EditSystemScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.gui.widgets.pressable.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_418;
import net.minecraft.class_5250;
import org.joml.Vector3d;

public class WaypointsModule extends Module {
   private static final Color GRAY = new Color(200, 200, 200);
   private static final Color TEXT = new Color(255, 255, 255);
   private final SettingGroup sgGeneral;
   private final SettingGroup sgDeathPosition;
   public final Setting<Integer> textRenderDistance;
   private final Setting<Integer> waypointFadeDistance;
   private final Setting<Integer> maxDeathPositions;
   private final Setting<Boolean> dpChat;
   private final SimpleDateFormat dateFormat;

   public WaypointsModule() {
      super(Categories.Render, "waypoints", "Allows you to create waypoints.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgDeathPosition = this.settings.createGroup("Death Position");
      this.textRenderDistance = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("text-render-distance")).description("Maximum distance from the center of the screen at which text will be rendered.")).defaultValue(100)).min(0).sliderMax(200).build());
      this.waypointFadeDistance = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("waypoint-fade-distance")).description("The distance to a waypoint at which it begins to start fading.")).defaultValue(20)).sliderRange(0, 100).min(0).build());
      this.maxDeathPositions = this.sgDeathPosition.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-death-positions")).description("The amount of death positions to save, 0 to disable")).defaultValue(0)).min(0).sliderMax(20).onChanged(this::cleanDeathWPs)).build());
      this.dpChat = this.sgDeathPosition.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("chat")).description("Send a chat message with your position once you die")).defaultValue(false)).build());
      this.dateFormat = new SimpleDateFormat("HH:mm:ss");
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      TextRenderer text = TextRenderer.get();
      Vector3d center = new Vector3d((double)this.mc.method_22683().method_4489() / 2.0D, (double)this.mc.method_22683().method_4506() / 2.0D, 0.0D);
      int textRenderDist = (Integer)this.textRenderDistance.get();
      Iterator var5 = Waypoints.get().iterator();

      while(true) {
         Waypoint waypoint;
         Vector3d pos;
         double dist;
         double distToCenter;
         double a;
         do {
            do {
               do {
                  do {
                     do {
                        if (!var5.hasNext()) {
                           return;
                        }

                        waypoint = (Waypoint)var5.next();
                     } while(!(Boolean)waypoint.visible.get());
                  } while(!Waypoints.checkDimension(waypoint));

                  class_2338 blockPos = waypoint.getPos();
                  pos = new Vector3d((double)blockPos.method_10263() + 0.5D, (double)blockPos.method_10264(), (double)blockPos.method_10260() + 0.5D);
                  dist = PlayerUtils.distanceToCamera(pos.x, pos.y, pos.z);
               } while(dist > (double)(Integer)waypoint.maxVisible.get());
            } while(!NametagUtils.to2D(pos, (Double)waypoint.scale.get() - 0.2D));

            distToCenter = pos.distance(center);
            a = 1.0D;
            if (!(dist < (double)(Integer)this.waypointFadeDistance.get())) {
               break;
            }

            a = (dist - (double)(Integer)this.waypointFadeDistance.get() / 2.0D) / ((double)(Integer)this.waypointFadeDistance.get() / 2.0D);
         } while(a < 0.01D);

         NametagUtils.begin(pos);
         waypoint.renderIcon(-16.0D, -16.0D, a, 32.0D);
         if (distToCenter <= (double)textRenderDist) {
            int preTextA = TEXT.a;
            Color var10000 = TEXT;
            var10000.a *= (int)a;
            text.begin();
            text.render((String)waypoint.name.get(), -text.getWidth((String)waypoint.name.get()) / 2.0D, -16.0D - text.getHeight(), TEXT, true);
            String distText = String.format("%d blocks", (int)Math.round(dist));
            text.render(distText, -text.getWidth(distText) / 2.0D, 16.0D, TEXT, true);
            text.end();
            TEXT.a = preTextA;
         }

         NametagUtils.end();
      }
   }

   @EventHandler
   private void onOpenScreen(OpenScreenEvent event) {
      if (event.screen instanceof class_418) {
         if (!event.isCancelled()) {
            this.addDeath(this.mc.field_1724.method_19538());
         }

      }
   }

   public void addDeath(class_243 deathPos) {
      String time = this.dateFormat.format(new Date());
      if ((Boolean)this.dpChat.get()) {
         class_5250 text = class_2561.method_43470("Died at ");
         text.method_10852(ChatUtils.formatCoords(deathPos));
         text.method_27693(String.format(" on %s.", time));
         this.info(text);
      }

      if ((Integer)this.maxDeathPositions.get() > 0) {
         Waypoint waypoint = (new Waypoint.Builder()).name("Death " + time).icon("skull").pos(class_2338.method_49638(deathPos).method_10086(2)).dimension(PlayerUtils.getDimension()).build();
         Waypoints.get().add(waypoint);
      }

      this.cleanDeathWPs((Integer)this.maxDeathPositions.get());
   }

   private void cleanDeathWPs(int max) {
      int oldWpC = 0;
      Iterator it = Waypoints.get().iterator();

      while(it.hasNext()) {
         Waypoint wp = (Waypoint)it.next();
         if (((String)wp.name.get()).startsWith("Death ") && ((String)wp.icon.get()).equals("skull")) {
            ++oldWpC;
            if (oldWpC > max) {
               it.remove();
            }
         }
      }

   }

   public WWidget getWidget(GuiTheme theme) {
      if (!Utils.canUpdate()) {
         return theme.label("You need to be in a world.");
      } else {
         WTable table = theme.table();
         this.initTable(theme, table);
         return table;
      }
   }

   private void initTable(GuiTheme theme, WTable table) {
      table.clear();
      Iterator var3 = Waypoints.get().iterator();

      while(var3.hasNext()) {
         Waypoint waypoint = (Waypoint)var3.next();
         boolean validDim = Waypoints.checkDimension(waypoint);
         table.add(new WaypointsModule.WIcon(waypoint));
         WLabel name = (WLabel)table.add(theme.label((String)waypoint.name.get())).expandCellX().widget();
         if (!validDim) {
            name.color = GRAY;
         }

         WCheckbox visible = (WCheckbox)table.add(theme.checkbox((Boolean)waypoint.visible.get())).widget();
         visible.action = () -> {
            waypoint.visible.set(visible.checked);
            Waypoints.get().save();
         };
         WButton edit = (WButton)table.add(theme.button(GuiRenderer.EDIT)).widget();
         edit.action = () -> {
            this.mc.method_1507(new WaypointsModule.EditWaypointScreen(theme, waypoint, () -> {
               this.initTable(theme, table);
            }));
         };
         if (validDim) {
            WButton gotoB = (WButton)table.add(theme.button("Goto")).widget();
            gotoB.action = () -> {
               if (PathManagers.get().isPathing()) {
                  PathManagers.get().stop();
               }

               PathManagers.get().moveTo(waypoint.getPos());
            };
         }

         WMinus remove = (WMinus)table.add(theme.minus()).widget();
         remove.action = () -> {
            Waypoints.get().remove(waypoint);
            this.initTable(theme, table);
         };
         table.row();
      }

      table.add(theme.horizontalSeparator()).expandX();
      table.row();
      WButton create = (WButton)table.add(theme.button("Create")).expandX().widget();
      create.action = () -> {
         this.mc.method_1507(new WaypointsModule.EditWaypointScreen(theme, (Waypoint)null, () -> {
            this.initTable(theme, table);
         }));
      };
   }

   private static class WIcon extends WWidget {
      private final Waypoint waypoint;

      public WIcon(Waypoint waypoint) {
         this.waypoint = waypoint;
      }

      protected void onCalculateSize() {
         double s = this.theme.scale(32.0D);
         this.width = s;
         this.height = s;
      }

      protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
         renderer.post(() -> {
            this.waypoint.renderIcon(this.x, this.y, 1.0D, this.width);
         });
      }
   }

   private static class EditWaypointScreen extends EditSystemScreen<Waypoint> {
      public EditWaypointScreen(GuiTheme theme, Waypoint value, Runnable reload) {
         super(theme, value, reload);
      }

      public Waypoint create() {
         return (new Waypoint.Builder()).pos(class_310.method_1551().field_1724.method_24515().method_10086(2)).dimension(PlayerUtils.getDimension()).build();
      }

      public boolean save() {
         if (((String)((Waypoint)this.value).name.get()).isBlank()) {
            return false;
         } else {
            Waypoints.get().add((Waypoint)this.value);
            return true;
         }
      }

      public Settings getSettings() {
         return ((Waypoint)this.value).settings;
      }
   }
}
