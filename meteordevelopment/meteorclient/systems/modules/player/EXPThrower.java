package meteordevelopment.meteorclient.systems.modules.player;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1802;

public class EXPThrower extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<Integer> throwsPerTick;

   public EXPThrower() {
      super(Categories.Player, "exp-thrower", "Automatically throws XP bottles from your hotbar.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.throwsPerTick = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("throws-per-tick")).description("Number of xp bottles to throw every tick.")).defaultValue(1)).min(1).sliderMax(5).build());
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      FindItemResult result = InvUtils.find(class_1802.field_8287);
      if (result.found() && !this.mc.field_1724.method_6115()) {
         if (MeteorClient.SWAP.beginSwap(result, true)) {
            MeteorClient.ROTATION.requestRotation(this.mc.field_1724.method_36454(), 90.0F, 0.0D);

            for(int i = 0; i < (Integer)this.throwsPerTick.get(); ++i) {
               this.mc.field_1761.method_2919(this.mc.field_1724, class_1268.field_5808);
            }

            MeteorClient.SWAP.endSwap(true);
         }

      }
   }
}
