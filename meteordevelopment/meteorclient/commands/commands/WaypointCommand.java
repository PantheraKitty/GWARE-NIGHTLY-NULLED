package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Iterator;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.WaypointArgumentType;
import meteordevelopment.meteorclient.systems.waypoints.Waypoint;
import meteordevelopment.meteorclient.systems.waypoints.Waypoints;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.minecraft.class_124;
import net.minecraft.class_2172;
import net.minecraft.class_2267;
import net.minecraft.class_2277;
import net.minecraft.class_2338;

public class WaypointCommand extends Command {
   public WaypointCommand() {
      super("waypoint", "Manages waypoints.", "wp");
   }

   public void build(LiteralArgumentBuilder<class_2172> builder) {
      builder.then(literal("list").executes((context) -> {
         if (Waypoints.get().isEmpty()) {
            this.error("No created waypoints.", new Object[0]);
         } else {
            this.info(String.valueOf(class_124.field_1068) + "Created Waypoints:", new Object[0]);
            Iterator var2 = Waypoints.get().iterator();

            while(var2.hasNext()) {
               Waypoint waypoint = (Waypoint)var2.next();
               this.info("Name: (highlight)'%s'(default), Dimension: (highlight)%s(default), Pos: (highlight)%s(default)", new Object[]{waypoint.name.get(), waypoint.dimension.get(), this.waypointPos(waypoint)});
            }
         }

         return 1;
      }));
      builder.then(literal("get").then(argument("waypoint", WaypointArgumentType.create()).executes((context) -> {
         Waypoint waypoint = WaypointArgumentType.get(context);
         String var10001 = String.valueOf(class_124.field_1068);
         this.info("Name: " + var10001 + (String)waypoint.name.get(), new Object[0]);
         var10001 = String.valueOf(class_124.field_1068);
         this.info("Actual Dimension: " + var10001 + String.valueOf(waypoint.dimension.get()), new Object[0]);
         var10001 = String.valueOf(class_124.field_1068);
         this.info("Position: " + var10001 + this.waypointFullPos(waypoint), new Object[0]);
         this.info("Visible: " + ((Boolean)waypoint.visible.get() ? String.valueOf(class_124.field_1060) + "True" : String.valueOf(class_124.field_1061) + "False"), new Object[0]);
         return 1;
      })));
      builder.then(((LiteralArgumentBuilder)literal("add").then(argument("pos", class_2277.method_9737()).then(argument("waypoint", StringArgumentType.greedyString()).executes((context) -> {
         return this.addWaypoint(context, true);
      })))).then(argument("waypoint", StringArgumentType.greedyString()).executes((context) -> {
         return this.addWaypoint(context, false);
      })));
      builder.then(literal("delete").then(argument("waypoint", WaypointArgumentType.create()).executes((context) -> {
         Waypoint waypoint = WaypointArgumentType.get(context);
         this.info("The waypoint (highlight)'%s'(default) has been deleted.", new Object[]{waypoint.name.get()});
         Waypoints.get().remove(waypoint);
         return 1;
      })));
      builder.then(literal("toggle").then(argument("waypoint", WaypointArgumentType.create()).executes((context) -> {
         Waypoint waypoint = WaypointArgumentType.get(context);
         waypoint.visible.set(!(Boolean)waypoint.visible.get());
         Waypoints.get().save();
         return 1;
      })));
   }

   private String waypointPos(Waypoint waypoint) {
      int var10000 = ((class_2338)waypoint.pos.get()).method_10263();
      return "X: " + var10000 + " Z: " + ((class_2338)waypoint.pos.get()).method_10260();
   }

   private String waypointFullPos(Waypoint waypoint) {
      int var10000 = ((class_2338)waypoint.pos.get()).method_10263();
      return "X: " + var10000 + ", Y: " + ((class_2338)waypoint.pos.get()).method_10264() + ", Z: " + ((class_2338)waypoint.pos.get()).method_10260();
   }

   private int addWaypoint(CommandContext<class_2172> context, boolean withCoords) {
      if (mc.field_1724 == null) {
         return -1;
      } else {
         class_2338 pos = withCoords ? ((class_2267)context.getArgument("pos", class_2267.class)).method_9704(mc.field_1724.method_5671()) : mc.field_1724.method_24515().method_10086(2);
         Waypoint waypoint = (new Waypoint.Builder()).name(StringArgumentType.getString(context, "waypoint")).pos(pos).dimension(PlayerUtils.getDimension()).build();
         Waypoints.get().add(waypoint);
         this.info("Created waypoint with name: (highlight)%s(default)", new Object[]{waypoint.name.get()});
         return 1;
      }
   }
}
