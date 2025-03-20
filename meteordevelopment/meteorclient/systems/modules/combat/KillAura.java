package meteordevelopment.meteorclient.systems.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.pathing.PathManagers;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.Target;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.world.TickRate;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1429;
import net.minecraft.class_1493;
import net.minecraft.class_1560;
import net.minecraft.class_1590;
import net.minecraft.class_1657;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_1829;
import net.minecraft.class_1835;
import net.minecraft.class_1934;
import net.minecraft.class_238;
import net.minecraft.class_2868;
import net.minecraft.class_3532;
import net.minecraft.class_6025;
import net.minecraft.class_9362;

public class KillAura extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgTargeting;
   private final SettingGroup sgTiming;
   private final Setting<KillAura.Weapon> weapon;
   private final Setting<KillAura.RotationMode> rotation;
   private final Setting<Boolean> autoSwitch;
   private final Setting<Boolean> onlyOnClick;
   private final Setting<Boolean> onlyOnLook;
   private final Setting<Boolean> pauseOnCombat;
   private final Setting<KillAura.ShieldMode> shieldMode;
   private final Setting<Set<class_1299<?>>> entities;
   private final Setting<SortPriority> priority;
   private final Setting<Integer> maxTargets;
   private final Setting<Double> range;
   private final Setting<Double> wallsRange;
   private final Setting<KillAura.EntityAge> mobAgeFilter;
   private final Setting<Boolean> ignoreNamed;
   private final Setting<Boolean> ignorePassive;
   private final Setting<Boolean> ignoreTamed;
   private final Setting<Boolean> pauseOnLag;
   private final Setting<Boolean> pauseOnUse;
   private final Setting<Boolean> tpsSync;
   private final Setting<Boolean> customDelay;
   private final Setting<Integer> hitDelay;
   private final Setting<Integer> switchDelay;
   private final List<class_1297> targets;
   private int switchTimer;
   private int hitTimer;
   private boolean wasPathing;
   public boolean attacking;

   public KillAura() {
      super(Categories.Combat, "kill-aura", "Attacks specified entities around you.");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgTargeting = this.settings.createGroup("Targeting");
      this.sgTiming = this.settings.createGroup("Timing");
      this.weapon = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("weapon")).description("Only attacks an entity when a specified weapon is in your hand.")).defaultValue(KillAura.Weapon.All)).build());
      this.rotation = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("rotate")).description("Determines when you should rotate towards the target.")).defaultValue(KillAura.RotationMode.Always)).build());
      this.autoSwitch = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-switch")).description("Switches to your selected weapon when attacking the target.")).defaultValue(false)).build());
      this.onlyOnClick = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-click")).description("Only attacks when holding left click.")).defaultValue(false)).build());
      this.onlyOnLook = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("only-on-look")).description("Only attacks when looking at an entity.")).defaultValue(false)).build());
      this.pauseOnCombat = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-baritone")).description("Freezes Baritone temporarily until you are finished attacking the entity.")).defaultValue(true)).build());
      this.shieldMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shield-mode")).description("Will try and use an axe to break target shields.")).defaultValue(KillAura.ShieldMode.Break)).visible(() -> {
         return (Boolean)this.autoSwitch.get() && this.weapon.get() != KillAura.Weapon.Axe;
      })).build());
      this.entities = this.sgTargeting.add(((EntityTypeListSetting.Builder)((EntityTypeListSetting.Builder)(new EntityTypeListSetting.Builder()).name("entities")).description("Entities to attack.")).onlyAttackable().defaultValue(class_1299.field_6097).build());
      this.priority = this.sgTargeting.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("priority")).description("How to filter targets within range.")).defaultValue(SortPriority.ClosestAngle)).build());
      this.maxTargets = this.sgTargeting.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("max-targets")).description("How many entities to target at once.")).defaultValue(1)).min(1).sliderRange(1, 5).visible(() -> {
         return !(Boolean)this.onlyOnLook.get();
      })).build());
      this.range = this.sgTargeting.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("range")).description("The maximum range the entity can be to attack it.")).defaultValue(4.5D).min(0.0D).sliderMax(6.0D).build());
      this.wallsRange = this.sgTargeting.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("walls-range")).description("The maximum range the entity can be attacked through walls.")).defaultValue(3.5D).min(0.0D).sliderMax(6.0D).build());
      this.mobAgeFilter = this.sgTargeting.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mob-age-filter")).description("Determines the age of the mobs to target (baby, adult, or both).")).defaultValue(KillAura.EntityAge.Adult)).build());
      this.ignoreNamed = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-named")).description("Whether or not to attack mobs with a name.")).defaultValue(false)).build());
      this.ignorePassive = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-passive")).description("Will only attack sometimes passive mobs if they are targeting you.")).defaultValue(true)).build());
      this.ignoreTamed = this.sgTargeting.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("ignore-tamed")).description("Will avoid attacking mobs you tamed.")).defaultValue(false)).build());
      this.pauseOnLag = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-lag")).description("Pauses if the server is lagging.")).defaultValue(true)).build());
      this.pauseOnUse = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("pause-on-use")).description("Does not attack while using an item.")).defaultValue(false)).build());
      this.tpsSync = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("TPS-sync")).description("Tries to sync attack delay with the server's TPS.")).defaultValue(true)).build());
      this.customDelay = this.sgTiming.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("custom-delay")).description("Use a custom delay instead of the vanilla cooldown.")).defaultValue(false)).build());
      SettingGroup var10001 = this.sgTiming;
      IntSetting.Builder var10002 = ((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("hit-delay")).description("How fast you hit the entity in ticks.")).defaultValue(11)).min(0).sliderMax(60);
      Setting var10003 = this.customDelay;
      Objects.requireNonNull(var10003);
      this.hitDelay = var10001.add(((IntSetting.Builder)var10002.visible(var10003::get)).build());
      this.switchDelay = this.sgTiming.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("switch-delay")).description("How many ticks to wait before hitting an entity after switching hotbar slots.")).defaultValue(0)).min(0).sliderMax(10).build());
      this.targets = new ArrayList();
      this.wasPathing = false;
   }

   public void onDeactivate() {
      this.targets.clear();
      this.attacking = false;
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      if (this.mc.field_1724.method_5805() && PlayerUtils.getGameMode() != class_1934.field_9219) {
         if (!(Boolean)this.pauseOnUse.get() || !this.mc.field_1761.method_2923() && !this.mc.field_1724.method_6115()) {
            if (!(Boolean)this.onlyOnClick.get() || this.mc.field_1690.field_1886.method_1434()) {
               if (!(TickRate.INSTANCE.getTimeSinceLastTick() >= 1.0F) || !(Boolean)this.pauseOnLag.get()) {
                  class_1297 primary;
                  if ((Boolean)this.onlyOnLook.get()) {
                     primary = this.mc.field_1692;
                     if (primary == null) {
                        return;
                     }

                     if (!this.entityCheck(primary)) {
                        return;
                     }

                     this.targets.clear();
                     this.targets.add(this.mc.field_1692);
                  } else {
                     this.targets.clear();
                     TargetUtils.getList(this.targets, this::entityCheck, (SortPriority)this.priority.get(), (Integer)this.maxTargets.get());
                  }

                  if (this.targets.isEmpty()) {
                     this.attacking = false;
                     if (this.wasPathing) {
                        PathManagers.get().resume();
                        this.wasPathing = false;
                     }

                  } else {
                     primary = (class_1297)this.targets.getFirst();
                     if ((Boolean)this.autoSwitch.get()) {
                        Predicate var10000;
                        switch(((KillAura.Weapon)this.weapon.get()).ordinal()) {
                        case 0:
                           var10000 = (stack) -> {
                              return stack.method_7909() instanceof class_1829;
                           };
                           break;
                        case 1:
                           var10000 = (stack) -> {
                              return stack.method_7909() instanceof class_1743;
                           };
                           break;
                        case 2:
                           var10000 = (stack) -> {
                              return stack.method_7909() instanceof class_9362;
                           };
                           break;
                        case 3:
                           var10000 = (stack) -> {
                              return stack.method_7909() instanceof class_1835;
                           };
                           break;
                        case 4:
                           var10000 = (stack) -> {
                              return stack.method_7909() instanceof class_1743 || stack.method_7909() instanceof class_1829 || stack.method_7909() instanceof class_9362 || stack.method_7909() instanceof class_1835;
                           };
                           break;
                        default:
                           var10000 = (o) -> {
                              return true;
                           };
                        }

                        Predicate<class_1799> predicate = var10000;
                        FindItemResult weaponResult = InvUtils.findInHotbar(predicate);
                        if (this.shouldShieldBreak()) {
                           FindItemResult axeResult = InvUtils.findInHotbar((itemStack) -> {
                              return itemStack.method_7909() instanceof class_1743;
                           });
                           if (axeResult.found()) {
                              weaponResult = axeResult;
                           }
                        }

                        InvUtils.swap(weaponResult.slot(), false);
                     }

                     if (this.itemInHand()) {
                        this.attacking = true;
                        if (this.rotation.get() == KillAura.RotationMode.Always) {
                           Rotations.rotate(Rotations.getYaw(primary), Rotations.getPitch(primary, Target.Body));
                        }

                        if ((Boolean)this.pauseOnCombat.get() && PathManagers.get().isPathing() && !this.wasPathing) {
                           PathManagers.get().pause();
                           this.wasPathing = true;
                        }

                        if (this.delayCheck()) {
                           this.targets.forEach(this::attack);
                        }

                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   private void onSendPacket(PacketEvent.Send event) {
      if (event.packet instanceof class_2868) {
         this.switchTimer = (Integer)this.switchDelay.get();
      }

   }

   private boolean shouldShieldBreak() {
      Iterator var1 = this.targets.iterator();

      while(var1.hasNext()) {
         class_1297 target = (class_1297)var1.next();
         if (target instanceof class_1657) {
            class_1657 player = (class_1657)target;
            if (player.method_6061(this.mc.field_1687.method_48963().method_48802(this.mc.field_1724)) && this.shieldMode.get() == KillAura.ShieldMode.Break) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean entityCheck(class_1297 entity) {
      if (!entity.equals(this.mc.field_1724) && !entity.equals(this.mc.field_1719)) {
         if (entity instanceof class_1309) {
            class_1309 livingEntity = (class_1309)entity;
            if (livingEntity.method_29504()) {
               return false;
            }
         }

         if (entity.method_5805()) {
            class_238 hitbox = entity.method_5829();
            if (!PlayerUtils.isWithin(class_3532.method_15350(this.mc.field_1724.method_23317(), hitbox.field_1323, hitbox.field_1320), class_3532.method_15350(this.mc.field_1724.method_23318(), hitbox.field_1322, hitbox.field_1325), class_3532.method_15350(this.mc.field_1724.method_23321(), hitbox.field_1321, hitbox.field_1324), (Double)this.range.get())) {
               return false;
            } else if (!((Set)this.entities.get()).contains(entity.method_5864())) {
               return false;
            } else if ((Boolean)this.ignoreNamed.get() && entity.method_16914()) {
               return false;
            } else if (!PlayerUtils.canSeeEntity(entity) && !PlayerUtils.isWithin(entity, (Double)this.wallsRange.get())) {
               return false;
            } else {
               if ((Boolean)this.ignoreTamed.get() && entity instanceof class_6025) {
                  class_6025 tameable = (class_6025)entity;
                  if (tameable.method_6139() != null && tameable.method_6139().equals(this.mc.field_1724.method_5667())) {
                     return false;
                  }
               }

               if ((Boolean)this.ignorePassive.get()) {
                  if (entity instanceof class_1560) {
                     class_1560 enderman = (class_1560)entity;
                     if (!enderman.method_7028()) {
                        return false;
                     }
                  }

                  if (entity instanceof class_1590) {
                     class_1590 piglin = (class_1590)entity;
                     if (!piglin.method_6510()) {
                        return false;
                     }
                  }

                  if (entity instanceof class_1493) {
                     class_1493 wolf = (class_1493)entity;
                     if (!wolf.method_6510()) {
                        return false;
                     }
                  }
               }

               if (entity instanceof class_1657) {
                  class_1657 player = (class_1657)entity;
                  if (player.method_7337()) {
                     return false;
                  }

                  if (!Friends.get().shouldAttack(player)) {
                     return false;
                  }

                  if (this.shieldMode.get() == KillAura.ShieldMode.Ignore && player.method_6061(this.mc.field_1687.method_48963().method_48802(this.mc.field_1724))) {
                     return false;
                  }
               }

               if (entity instanceof class_1429) {
                  class_1429 animal = (class_1429)entity;
                  boolean var10000;
                  switch(((KillAura.EntityAge)this.mobAgeFilter.get()).ordinal()) {
                  case 0:
                     var10000 = animal.method_6109();
                     break;
                  case 1:
                     var10000 = !animal.method_6109();
                     break;
                  case 2:
                     var10000 = true;
                     break;
                  default:
                     throw new MatchException((String)null, (Throwable)null);
                  }

                  return var10000;
               } else {
                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean delayCheck() {
      if (this.switchTimer > 0) {
         --this.switchTimer;
         return false;
      } else {
         float delay = (Boolean)this.customDelay.get() ? (float)(Integer)this.hitDelay.get() : 0.5F;
         if ((Boolean)this.tpsSync.get()) {
            delay /= TickRate.INSTANCE.getTickRate() / 20.0F;
         }

         if ((Boolean)this.customDelay.get()) {
            if ((float)this.hitTimer < delay) {
               ++this.hitTimer;
               return false;
            } else {
               return true;
            }
         } else {
            return this.mc.field_1724.method_7261(delay) >= 1.0F;
         }
      }
   }

   private void attack(class_1297 target) {
      if (this.rotation.get() == KillAura.RotationMode.OnHit) {
         Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target, Target.Body));
      }

      this.mc.field_1761.method_2918(this.mc.field_1724, target);
      this.mc.field_1724.method_6104(class_1268.field_5808);
      this.hitTimer = 0;
   }

   private boolean itemInHand() {
      if (this.shouldShieldBreak()) {
         return this.mc.field_1724.method_6047().method_7909() instanceof class_1743;
      } else {
         boolean var10000;
         switch(((KillAura.Weapon)this.weapon.get()).ordinal()) {
         case 0:
            var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1829;
            break;
         case 1:
            var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1743;
            break;
         case 2:
            var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_9362;
            break;
         case 3:
            var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1835;
            break;
         case 4:
            var10000 = this.mc.field_1724.method_6047().method_7909() instanceof class_1743 || this.mc.field_1724.method_6047().method_7909() instanceof class_1829 || this.mc.field_1724.method_6047().method_7909() instanceof class_9362 || this.mc.field_1724.method_6047().method_7909() instanceof class_1835;
            break;
         default:
            var10000 = true;
         }

         return var10000;
      }
   }

   public class_1297 getTarget() {
      return !this.targets.isEmpty() ? (class_1297)this.targets.getFirst() : null;
   }

   public String getInfoString() {
      return !this.targets.isEmpty() ? EntityUtils.getName(this.getTarget()) : null;
   }

   public static enum Weapon {
      Sword,
      Axe,
      Mace,
      Trident,
      All,
      Any;

      // $FF: synthetic method
      private static KillAura.Weapon[] $values() {
         return new KillAura.Weapon[]{Sword, Axe, Mace, Trident, All, Any};
      }
   }

   public static enum RotationMode {
      Always,
      OnHit,
      None;

      // $FF: synthetic method
      private static KillAura.RotationMode[] $values() {
         return new KillAura.RotationMode[]{Always, OnHit, None};
      }
   }

   public static enum ShieldMode {
      Ignore,
      Break,
      None;

      // $FF: synthetic method
      private static KillAura.ShieldMode[] $values() {
         return new KillAura.ShieldMode[]{Ignore, Break, None};
      }
   }

   public static enum EntityAge {
      Baby,
      Adult,
      Both;

      // $FF: synthetic method
      private static KillAura.EntityAge[] $values() {
         return new KillAura.EntityAge[]{Baby, Adult, Both};
      }
   }
}
