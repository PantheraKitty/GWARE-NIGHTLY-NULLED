package meteordevelopment.meteorclient.systems.modules.render;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.PlayerJoinLeaveEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.fakeplayer.FakePlayerEntity;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.WireframeEntityRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_4587;
import org.joml.Vector3d;

public class LogoutSpots extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgRender;
   private final Setting<Boolean> notifyOnRejoin;
   private final Setting<Boolean> notifyOnRejoinShowCoords;
   private final Setting<Boolean> notifyOnRejoinLimitDistance;
   private final Setting<Double> notifyOnRejoinDistance;
   private final Setting<Boolean> showTime;
   private final Setting<ShapeMode> shapeMode;
   private final Setting<SettingColor> sideColor;
   private final Setting<SettingColor> lineColor;
   private final Setting<SettingColor> nameColor;
   private final Setting<SettingColor> timeColor;
   private final Setting<SettingColor> totemPopsColor;
   private final Setting<SettingColor> textBackgroundColor;
   private final Setting<Double> nametageScale;
   private static final Map<UUID, LogoutSpots.GhostPlayer> loggedPlayers = new ConcurrentHashMap();
   private final Map<UUID, class_1657> playerCache;
   private final Map<UUID, Integer> ticksOnPlayerList;
   private Dimension lastDimension;

   public LogoutSpots() {
      super(Categories.Render, "logout-spots", "Displays a box where another player has logged out at.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgRender = this.settings.createGroup("Render");
      this.notifyOnRejoin = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("notify-on-rejoin")).description("Notifies you when a player rejoins.")).defaultValue(true)).build());
      this.notifyOnRejoinShowCoords = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("notify-on-show-coords")).description("Shows the coords of the player when they rejoin.")).defaultValue(true)).visible(() -> {
         return (Boolean)this.notifyOnRejoin.get();
      })).build());
      this.notifyOnRejoinLimitDistance = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("notify-on-rejoin-limit-distance")).description("Whether or not to limit distances for rejoin coord notifications.")).defaultValue(true)).visible(() -> {
         return (Boolean)this.notifyOnRejoin.get() && (Boolean)this.notifyOnRejoinShowCoords.get();
      })).build());
      this.notifyOnRejoinDistance = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("notify-on-rejoin-distance")).description("The limit to show coords on rejoin.")).defaultValue(5000.0D).min(0.0D).visible(() -> {
         return (Boolean)this.notifyOnRejoin.get() && (Boolean)this.notifyOnRejoinShowCoords.get() && (Boolean)this.notifyOnRejoinLimitDistance.get();
      })).build());
      this.showTime = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-time")).description("Whether or not to show the time since logged out.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.sideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("side-color")).description("The side color.")).defaultValue(new SettingColor(255, 0, 255, 55)).build());
      this.lineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("line-color")).description("The line color.")).defaultValue(new SettingColor(255, 0, 255)).build());
      this.nameColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("name-color")).description("The name color.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.timeColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("time-color")).description("The time color.")).defaultValue(new SettingColor(255, 255, 255)).build());
      this.totemPopsColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("totem-pop-color")).description("The color of the totem pops.")).defaultValue(new SettingColor(225, 120, 20)).build());
      this.textBackgroundColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("text-background-color")).description("The text background color.")).defaultValue(new SettingColor(0, 0, 0, 75)).build());
      this.nametageScale = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("text-scale")).description("The scale for text.")).defaultValue(1.0D).min(0.1D).sliderMax(2.0D).build());
      this.playerCache = new ConcurrentHashMap();
      this.ticksOnPlayerList = new ConcurrentHashMap();
      this.lineColor.onChanged();
   }

   public void onActivate() {
      this.lastDimension = PlayerUtils.getDimension();
   }

   public void onDeactivate() {
      loggedPlayers.clear();
      this.playerCache.clear();
      this.ticksOnPlayerList.clear();
   }

   @EventHandler(
      priority = -200
   )
   private void onTick(TickEvent.Post event) {
      Dimension dimension = PlayerUtils.getDimension();
      if (dimension != this.lastDimension) {
         loggedPlayers.clear();
      }

      this.lastDimension = dimension;
      Iterator var3 = this.mc.field_1687.method_18456().iterator();

      while(var3.hasNext()) {
         class_1657 player = (class_1657)var3.next();
         if (player != null && !player.equals(this.mc.field_1724)) {
            this.playerCache.put(player.method_7334().getId(), player);
         }
      }

      loggedPlayers.entrySet().removeIf((entry) -> {
         if (this.mc.method_1562().method_2871((UUID)entry.getKey()) != null) {
            int n = 0;
            if (this.ticksOnPlayerList.containsKey(entry.getKey())) {
               n = (Integer)this.ticksOnPlayerList.get(entry.getKey());
            }

            this.ticksOnPlayerList.put((UUID)entry.getKey(), n + 1);
            if (n > 1) {
               return true;
            }
         }

         return false;
      });
   }

   @EventHandler
   private void onPlayerJoin(PlayerJoinLeaveEvent.Join event) {
      if (event.getEntry().comp_1106() != null) {
         if (loggedPlayers.containsKey(event.getEntry().comp_1106())) {
            LogoutSpots.GhostPlayer ghost = (LogoutSpots.GhostPlayer)loggedPlayers.remove(event.getEntry().comp_1106());
            if ((Boolean)this.notifyOnRejoin.get()) {
               boolean showCoords = (Boolean)this.notifyOnRejoinShowCoords.get();
               if ((Boolean)this.notifyOnRejoinLimitDistance.get() && (Double)this.notifyOnRejoinDistance.get() < ghost.pos.method_1022(class_243.field_1353)) {
                  showCoords = false;
               }

               if (showCoords) {
                  this.info("(highlight)%s(default) rejoined at %d, %d, %d (highlight)(%.1fm away)(default).", new Object[]{ghost.name, (int)Math.floor(ghost.pos.field_1352), (int)Math.floor(ghost.pos.field_1351), (int)Math.floor(ghost.pos.field_1350), this.mc.field_1724.method_19538().method_1022(ghost.pos)});
               } else {
                  this.info("(highlight)%s(default) rejoined", new Object[]{ghost.name});
               }

               this.mc.field_1687.method_43129(this.mc.field_1724, this.mc.field_1724, class_3417.field_14627, class_3419.field_15256, 3.0F, 1.0F);
            }

         }
      }
   }

   public static Map<UUID, LogoutSpots.GhostPlayer> getLoggedPlayers() {
      return loggedPlayers;
   }

   @EventHandler
   private void onPlayerLeave(PlayerJoinLeaveEvent.Leave event) {
      if (event.getEntry().method_2966() != null) {
         UUID leaveId = event.getEntry().method_2966().getId();
         if (!loggedPlayers.containsKey(leaveId)) {
            if (this.playerCache.containsKey(leaveId)) {
               class_1657 player = (class_1657)this.playerCache.get(leaveId);
               if (player == null) {
                  this.warning("player with id " + leaveId.toString() + " was null for some reason :(, couldn't save logout spot", new Object[0]);
               } else if (!(player instanceof FakePlayerEntity)) {
                  LogoutSpots.GhostPlayer ghost = new LogoutSpots.GhostPlayer(player);
                  loggedPlayers.put(event.getEntry().method_2966().getId(), ghost);
               }
            }
         }
      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      loggedPlayers.values().forEach((player) -> {
         player.render3D(event);
      });
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      loggedPlayers.values().forEach((player) -> {
         player.render2D(event);
      });
   }

   public String getInfoString() {
      return Integer.toString(loggedPlayers.size());
   }

   public class GhostPlayer {
      private final UUID uuid;
      private long logoutTime;
      public String name;
      public class_238 hitbox;
      public class_1657 playerEntity;
      private List<WireframeEntityRenderer.RenderablePart> parts;
      public class_243 pos;
      private class_1799 heldItem;

      public GhostPlayer(class_1657 player) {
         this.playerEntity = player;
         this.uuid = player.method_5667();
         this.name = player.method_5477().getString();
         this.hitbox = player.method_5829();
         this.pos = player.method_19538();
         this.logoutTime = System.currentTimeMillis();
         this.heldItem = player.method_6047();
      }

      public void render3D(Render3DEvent event) {
         if (this.parts == null && this.playerEntity != null) {
            this.parts = WireframeEntityRenderer.cloneEntityForRendering(event, this.playerEntity, this.pos);
         }

         if (this.parts != null) {
            WireframeEntityRenderer.render(event, this.pos, this.parts, 1.0D, (Color)LogoutSpots.this.sideColor.get(), (Color)LogoutSpots.this.lineColor.get(), (ShapeMode)LogoutSpots.this.shapeMode.get());
            if (!this.heldItem.method_7960()) {
               WireframeEntityRenderer.renderHeldItem(event, this.playerEntity, event.renderer);
            }

         }
      }

      public void render2D(Render2DEvent event) {
         if (PlayerUtils.isWithinCamera(this.pos.field_1352, this.pos.field_1351, this.pos.field_1350, (double)((Integer)LogoutSpots.this.mc.field_1690.method_42503().method_41753() * 32))) {
            TextRenderer text = TextRenderer.get();
            double scale = (Double)LogoutSpots.this.nametageScale.get();
            Vector3d nametagPos = new Vector3d((this.hitbox.field_1323 + this.hitbox.field_1320) / 2.0D, this.hitbox.field_1325 + 0.5D, (this.hitbox.field_1321 + this.hitbox.field_1324) / 2.0D);
            if (NametagUtils.to2D(nametagPos, scale)) {
               NametagUtils.begin(nametagPos);
               String timeText = (Boolean)LogoutSpots.this.showTime.get() ? " " + this.getTimeText() : "";
               String totemPopsText = " " + -MeteorClient.INFO.getPops(this.uuid);
               double i = text.getWidth(this.name) / 2.0D + text.getWidth(timeText) / 2.0D + text.getWidth(totemPopsText) / 2.0D;
               Renderer2D.COLOR.begin();
               Renderer2D.COLOR.quad(-i, 0.0D, i * 2.0D, text.getHeight(), (Color)LogoutSpots.this.textBackgroundColor.get());
               Renderer2D.COLOR.render((class_4587)null);
               text.beginBig();
               double hX = text.render(this.name, -i, 0.0D, (Color)LogoutSpots.this.nameColor.get());
               hX = text.render(timeText, hX, 0.0D, (Color)LogoutSpots.this.timeColor.get());
               text.render(totemPopsText, hX, 0.0D, (Color)LogoutSpots.this.totemPopsColor.get());
               text.end();
               NametagUtils.end();
            }
         }
      }

      private String getTimeText() {
         double timeSinceLogout = (double)(System.currentTimeMillis() - this.logoutTime) / 1000.0D;
         int totalSeconds = (int)timeSinceLogout;
         int hours = totalSeconds / 3600;
         int minutes = totalSeconds % 3600 / 60;
         int seconds = totalSeconds % 60;
         return String.format("%02d:%02d:%02d", hours, minutes, seconds);
      }
   }
}
