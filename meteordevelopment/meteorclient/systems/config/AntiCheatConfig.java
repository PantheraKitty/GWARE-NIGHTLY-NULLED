package meteordevelopment.meteorclient.systems.config;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.Settings;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.managers.SwapManager;
import net.minecraft.class_2487;

public class AntiCheatConfig extends System<AntiCheatConfig> {
   public final Settings settings = new Settings();
   private final SettingGroup sgRotations;
   private final SettingGroup sgBlockPlacement;
   private final SettingGroup sgSwap;
   public final Setting<Boolean> tickSync;
   public final Setting<Boolean> grimSync;
   public final Setting<Boolean> grimRotation;
   public final Setting<Boolean> grimSnapRotation;
   public final Setting<Boolean> blockRotatePlace;
   public final Setting<Boolean> blockPlaceAirPlace;
   public final Setting<Boolean> forceAirPlace;
   public final Setting<Double> blockPlacePerBlockCooldown;
   public final Setting<Double> blocksPerSecondCap;
   public final Setting<Integer> blockPacketLimit;
   public final Setting<Boolean> swapAntiScreenClose;
   public final Setting<SwapManager.SwapMode> swapMode;

   public AntiCheatConfig() {
      super("anti-cheat-config");
      this.sgRotations = this.settings.createGroup("Rotations");
      this.sgBlockPlacement = this.settings.createGroup("Block Placement");
      this.sgSwap = this.settings.createGroup("Swap");
      this.tickSync = this.sgRotations.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("tick-sync")).description("Lets rotations be rotated. Should always be on.")).defaultValue(true)).build());
      this.grimSync = this.sgRotations.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("grim-sync")).description("Sends a full movement packet every tick")).defaultValue(false)).visible(() -> {
         return (Boolean)this.tickSync.get();
      })).build());
      this.grimRotation = this.sgRotations.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("grim-rotation")).description("Sends a full movement packet when your player look changes")).defaultValue(true)).visible(() -> {
         return (Boolean)this.tickSync.get();
      })).build());
      this.grimSnapRotation = this.sgRotations.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("grim-snap-rotation")).description("Sends a full movement packet when snapping rotation")).defaultValue(true)).build());
      this.blockRotatePlace = this.sgBlockPlacement.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("block-rotate-place")).description("Rotates to place blcks")).defaultValue(false)).build());
      this.blockPlaceAirPlace = this.sgBlockPlacement.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("grim-air-place")).description("Allows modules to air place blocks")).defaultValue(true)).build());
      this.forceAirPlace = this.sgBlockPlacement.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("force-air-place")).description("Only air-places blocks")).defaultValue(true)).visible(() -> {
         return (Boolean)this.blockPlaceAirPlace.get();
      })).build());
      this.blockPlacePerBlockCooldown = this.sgBlockPlacement.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("block-place-cooldown")).description("Amount of time to retry placing blocks in the same place")).defaultValue(0.05D).min(0.0D).sliderMax(0.3D).build());
      this.blocksPerSecondCap = this.sgBlockPlacement.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("blocks-per-second")).description("Maximum number of blocks that can be placed every second")).defaultValue(20.0D).min(0.0D).sliderMax(30.0D).build());
      this.blockPacketLimit = this.sgBlockPlacement.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("block-packet-limit")).description("Number of miliseconds to wait between block packet sends after hitting the rate limit. Paper server side is 300, so try a bit higher if you have issues.")).defaultValue(300)).min(300).sliderMax(350).build());
      this.swapAntiScreenClose = this.sgSwap.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("anti-screen-close")).description("Pauses certain methods of swapping when certain screens are open")).defaultValue(true)).build());
      this.swapMode = this.sgSwap.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("item-swap-mode")).description("How to swap to items")).defaultValue(SwapManager.SwapMode.Auto)).build());
   }

   public static AntiCheatConfig get() {
      return (AntiCheatConfig)Systems.get(AntiCheatConfig.class);
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      tag.method_10582("version", MeteorClient.VERSION.toString());
      tag.method_10566("settings", this.settings.toTag());
      return tag;
   }

   public AntiCheatConfig fromTag(class_2487 tag) {
      if (tag.method_10545("settings")) {
         this.settings.fromTag(tag.method_10562("settings"));
      }

      return this;
   }
}
