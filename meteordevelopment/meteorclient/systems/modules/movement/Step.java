package meteordevelopment.meteorclient.systems.modules.movement;

import com.google.common.collect.Streams;
import java.util.Objects;
import java.util.OptionalDouble;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.DamageUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_5134;

public class Step extends Module {
   private final SettingGroup sgGeneral;
   public final Setting<Double> height;
   private final Setting<Step.ActiveWhen> activeWhen;
   private final Setting<Boolean> safeStep;
   private final Setting<Integer> stepHealth;
   private float prevStepHeight;
   private boolean prevPathManagerStep;

   public Step() {
      super(Categories.Movement, "step", "Allows you to walk up full blocks instantly.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.height = this.sgGeneral.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("height")).description("Step height.")).defaultValue(1.0D).min(0.0D).build());
      this.activeWhen = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("active-when")).description("Step is active when you meet these requirements.")).defaultValue(Step.ActiveWhen.Always)).build());
      this.safeStep = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("safe-step")).description("Doesn't let you step out of a hole if you are low on health or there is a crystal nearby.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgGeneral;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("step-health")).description("The health you stop being able to step at.")).defaultValue(5)).range(1, 36).sliderRange(1, 36);
      Setting var10003 = this.safeStep;
      Objects.requireNonNull(var10003);
      this.stepHealth = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
   }

   public void onActivate() {
      this.prevStepHeight = this.mc.field_1724.method_49476();
      this.prevPathManagerStep = (Boolean)PathManagers.get().getSettings().getStep().get();
      PathManagers.get().getSettings().getStep().set(true);
   }

   @EventHandler
   private void onTick(TickEvent.Post event) {
      boolean work = this.activeWhen.get() == Step.ActiveWhen.Always || this.activeWhen.get() == Step.ActiveWhen.Sneaking && this.mc.field_1724.method_5715() || this.activeWhen.get() == Step.ActiveWhen.NotSneaking && !this.mc.field_1724.method_5715();
      this.mc.field_1724.method_5857(this.mc.field_1724.method_5829().method_989(0.0D, 1.0D, 0.0D));
      if (!work || (Boolean)this.safeStep.get() && (!(this.getHealth() > (float)(Integer)this.stepHealth.get()) || !((double)this.getHealth() - this.getExplosionDamage() > (double)(Integer)this.stepHealth.get()))) {
         this.mc.field_1724.method_5996(class_5134.field_47761).method_6192((double)this.prevStepHeight);
      } else {
         this.mc.field_1724.method_5996(class_5134.field_47761).method_6192((Double)this.height.get());
      }

      this.mc.field_1724.method_5857(this.mc.field_1724.method_5829().method_989(0.0D, -1.0D, 0.0D));
   }

   public void onDeactivate() {
      this.mc.field_1724.method_5996(class_5134.field_47761).method_6192((double)this.prevStepHeight);
      PathManagers.get().getSettings().getStep().set(this.prevPathManagerStep);
   }

   private float getHealth() {
      return this.mc.field_1724.method_6032() + this.mc.field_1724.method_6067();
   }

   private double getExplosionDamage() {
      OptionalDouble crystalDamage = Streams.stream(this.mc.field_1687.method_18112()).filter((entity) -> {
         return entity instanceof class_1511;
      }).filter(class_1297::method_5805).mapToDouble((entity) -> {
         return (double)DamageUtils.crystalDamage(this.mc.field_1724, entity.method_19538());
      }).max();
      return crystalDamage.orElse(0.0D);
   }

   public static enum ActiveWhen {
      Always,
      Sneaking,
      NotSneaking;

      // $FF: synthetic method
      private static Step.ActiveWhen[] $values() {
         return new Step.ActiveWhen[]{Always, Sneaking, NotSneaking};
      }
   }
}
