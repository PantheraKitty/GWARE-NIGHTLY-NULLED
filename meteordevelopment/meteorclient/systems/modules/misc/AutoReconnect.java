package meteordevelopment.meteorclient.systems.modules.misc;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectObjectImmutablePair;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.ServerConnectBeginEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_639;
import net.minecraft.class_642;

public class AutoReconnect extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Double> time;
   public Pair<class_639, class_642> lastServerConnection;

   public AutoReconnect() {
      super(Categories.Misc, "auto-reconnect", "Automatically reconnects when disconnected from a server.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.time = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("delay")).description("The amount of seconds to wait before reconnecting to the server.")).defaultValue(3.5D).min(0.0D).decimalPlaces(1).build());
      MeteorClient.EVENT_BUS.subscribe((Object)(new AutoReconnect.StaticListener()));
   }

   private class StaticListener {
      @EventHandler
      private void onGameJoined(ServerConnectBeginEvent event) {
         AutoReconnect.this.lastServerConnection = new ObjectObjectImmutablePair(event.address, event.info);
      }
   }
}
