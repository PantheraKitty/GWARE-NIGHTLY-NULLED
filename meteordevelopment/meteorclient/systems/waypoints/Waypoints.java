package meteordevelopment.meteorclient.systems.waypoints;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.files.StreamUtils;
import meteordevelopment.meteorclient.utils.misc.NbtUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import net.minecraft.class_1044;
import net.minecraft.class_2487;
import net.minecraft.class_2520;
import org.jetbrains.annotations.NotNull;

public class Waypoints extends System<Waypoints> implements Iterable<Waypoint> {
   public static final String[] BUILTIN_ICONS = new String[]{"square", "circle", "triangle", "star", "diamond", "skull"};
   public final Map<String, class_1044> icons = new ConcurrentHashMap();
   private final List<Waypoint> waypoints = Collections.synchronizedList(new ArrayList());

   public Waypoints() {
      super((String)null);
   }

   public static Waypoints get() {
      return (Waypoints)Systems.get(Waypoints.class);
   }

   public void init() {
      File iconsFolder = new File(new File(MeteorClient.FOLDER, "waypoints"), "icons");
      iconsFolder.mkdirs();
      String[] var2 = BUILTIN_ICONS;
      int var3 = var2.length;

      int var4;
      File file;
      for(var4 = 0; var4 < var3; ++var4) {
         String builtinIcon = var2[var4];
         file = new File(iconsFolder, builtinIcon + ".png");
         if (!file.exists()) {
            this.copyIcon(file);
         }
      }

      File[] files = iconsFolder.listFiles();
      if (files != null) {
         File[] var11 = files;
         var4 = files.length;

         for(int var12 = 0; var12 < var4; ++var12) {
            file = var11[var12];
            if (file.getName().endsWith(".png")) {
               try {
                  String name = file.getName().replace(".png", "");
                  class_1044 texture = new class_1043(class_1011.method_4309(new FileInputStream(file)));
                  this.icons.put(name, texture);
               } catch (IOException var9) {
                  MeteorClient.LOG.error("Failed to read a waypoint icon", var9);
               }
            }
         }

      }
   }

   public boolean add(Waypoint waypoint) {
      if (this.waypoints.contains(waypoint)) {
         this.save();
         return true;
      } else {
         this.waypoints.add(waypoint);
         this.save();
         return false;
      }
   }

   public boolean remove(Waypoint waypoint) {
      boolean removed = this.waypoints.remove(waypoint);
      if (removed) {
         this.save();
      }

      return removed;
   }

   public Waypoint get(String name) {
      Iterator var2 = this.waypoints.iterator();

      Waypoint waypoint;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         waypoint = (Waypoint)var2.next();
      } while(!((String)waypoint.name.get()).equalsIgnoreCase(name));

      return waypoint;
   }

   @EventHandler
   private void onGameJoined(GameJoinedEvent event) {
      this.load();
   }

   @EventHandler(
      priority = -200
   )
   private void onGameDisconnected(GameLeftEvent event) {
      this.waypoints.clear();
   }

   public static boolean checkDimension(Waypoint waypoint) {
      Dimension playerDim = PlayerUtils.getDimension();
      Dimension waypointDim = (Dimension)waypoint.dimension.get();
      if (playerDim == waypointDim) {
         return true;
      } else if (!(Boolean)waypoint.opposite.get()) {
         return false;
      } else {
         boolean playerOpp = playerDim == Dimension.Overworld || playerDim == Dimension.Nether;
         boolean waypointOpp = waypointDim == Dimension.Overworld || waypointDim == Dimension.Nether;
         return playerOpp && waypointOpp;
      }
   }

   public File getFile() {
      return !Utils.canUpdate() ? null : new File(new File(MeteorClient.FOLDER, "waypoints"), Utils.getFileWorldName() + ".nbt");
   }

   public boolean isEmpty() {
      return this.waypoints.isEmpty();
   }

   @NotNull
   public Iterator<Waypoint> iterator() {
      return new Waypoints.WaypointIterator();
   }

   private void copyIcon(File file) {
      String path = "/assets/meteor-client/textures/icons/waypoints/" + file.getName();
      InputStream in = Waypoints.class.getResourceAsStream(path);
      if (in == null) {
         MeteorClient.LOG.error("Failed to read a resource: {}", path);
      } else {
         StreamUtils.copy(in, file);
      }
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10566("waypoints", NbtUtils.listToTag(this.waypoints));
      return tag;
   }

   public Waypoints fromTag(class_2487 tag) {
      this.waypoints.clear();
      Iterator var2 = tag.method_10554("waypoints", 10).iterator();

      while(var2.hasNext()) {
         class_2520 waypointTag = (class_2520)var2.next();
         this.waypoints.add(new Waypoint(waypointTag));
      }

      return this;
   }

   private final class WaypointIterator implements Iterator<Waypoint> {
      private final Iterator<Waypoint> it;

      private WaypointIterator() {
         this.it = Waypoints.this.waypoints.iterator();
      }

      public boolean hasNext() {
         return this.it.hasNext();
      }

      public Waypoint next() {
         return (Waypoint)this.it.next();
      }

      public void remove() {
         this.it.remove();
         Waypoints.this.save();
      }
   }
}
