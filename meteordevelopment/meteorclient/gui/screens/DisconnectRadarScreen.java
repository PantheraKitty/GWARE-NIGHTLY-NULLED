package meteordevelopment.meteorclient.gui.screens;

import java.util.Iterator;
import java.util.Map.Entry;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.DisconnectRadar;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.class_500;

public class DisconnectRadarScreen extends WindowScreen {
   private final DisconnectRadar module = (DisconnectRadar)Modules.get().get(DisconnectRadar.class);
   private final class_500 multiplayerScreen;

   public DisconnectRadarScreen(GuiTheme theme, class_500 multiplayerScreen) {
      super(theme, "Disconnected");
      this.multiplayerScreen = multiplayerScreen;
   }

   public void initWidgets() {
      WLabel title = (WLabel)this.add(this.theme.label("Disconnected")).widget();
      title.color = new Color(255, 85, 85);
      title.x = ((double)this.field_22789 - title.width) / 2.0D;
      title.y = 20.0D;
      int y = 60;
      if (this.module != null && this.module.lastSeenPlayers != null && !this.module.lastSeenPlayers.isEmpty()) {
         for(Iterator var7 = this.module.lastSeenPlayers.entrySet().iterator(); var7.hasNext(); y += 20) {
            Entry<String, DisconnectRadar.PlayerInfo> entry = (Entry)var7.next();
            StringBuilder text = new StringBuilder((String)entry.getKey());
            if ((Boolean)this.module.showDistance.get()) {
               text.append(" (Distance: ").append(String.format("%.1f", ((DisconnectRadar.PlayerInfo)entry.getValue()).distance)).append("m)");
            }

            if ((Boolean)this.module.showPops.get() && ((DisconnectRadar.PlayerInfo)entry.getValue()).pops > 0) {
               text.append(" (Pops: ").append(((DisconnectRadar.PlayerInfo)entry.getValue()).pops).append(")");
            }

            WLabel label = (WLabel)this.add(this.theme.label(text.toString())).widget();
            label.x = 20.0D;
            label.y = (double)y;
         }
      } else {
         WLabel noPlayers = (WLabel)this.add(this.theme.label("No players detected in range")).widget();
         noPlayers.x = 20.0D;
         noPlayers.y = (double)y;
         y += 20;
      }

      WButton backButton = (WButton)this.add(this.theme.button("Back to Multiplayer")).widget();
      backButton.x = ((double)this.field_22789 - backButton.width) / 2.0D;
      backButton.y = (double)(y + 20);
      backButton.action = () -> {
         this.field_22787.method_1507(this.multiplayerScreen);
      };
   }
}
