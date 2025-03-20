package meteordevelopment.meteorclient.systems.modules.misc;

import java.util.Iterator;
import java.util.List;
import meteordevelopment.meteorclient.events.world.PlaySoundEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.SoundEventListSetting;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1113;
import net.minecraft.class_3414;
import net.minecraft.class_7923;

public class SoundBlocker extends Module {
   private final SettingGroup sgGeneral;
   private final Setting<List<class_3414>> sounds;
   private final Setting<Double> crystalHitVolume;
   private final Setting<Double> crystalVolume;

   public SoundBlocker() {
      super(Categories.Misc, "sound-blocker", "Cancels out selected sounds.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sounds = this.sgGeneral.add(((SoundEventListSetting.Builder)((SoundEventListSetting.Builder)(new SoundEventListSetting.Builder()).name("sounds")).description("Sounds to block.")).build());
      this.crystalHitVolume = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("crystal-hit-volume")).description("Sets the volume of hitting the crystals")).min(0.0D).defaultValue(0.2D).sliderMax(1.0D).build());
      this.crystalVolume = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("crystal-volume")).description("Sets the volume of the crystals")).min(0.0D).defaultValue(0.2D).sliderMax(1.0D).build());
   }

   @EventHandler
   private void onPlaySound(PlaySoundEvent event) {
      Iterator var2 = ((List)this.sounds.get()).iterator();

      while(var2.hasNext()) {
         class_3414 sound = (class_3414)var2.next();
         if (sound.method_14833().equals(event.sound.method_4775())) {
            event.cancel();
            break;
         }
      }

   }

   public boolean shouldBlock(class_1113 soundInstance) {
      return this.isActive() && ((List)this.sounds.get()).contains(Setting.parseId(class_7923.field_41172, soundInstance.method_4775().method_12832()));
   }

   public double getCrystalVolume() {
      return (Double)this.crystalVolume.get();
   }

   public double getCrystalHitVolume() {
      return (Double)this.crystalHitVolume.get();
   }
}
