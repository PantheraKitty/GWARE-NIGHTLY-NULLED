package meteordevelopment.meteorclient.systems.modules;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.meteor.ActiveModulesChangedEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.ModuleBindChangedEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.pathing.BaritoneUtils;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.modules.combat.AnchorAura;
import meteordevelopment.meteorclient.systems.modules.combat.AntiAnvil;
import meteordevelopment.meteorclient.systems.modules.combat.AntiBed;
import meteordevelopment.meteorclient.systems.modules.combat.AntiDigDown;
import meteordevelopment.meteorclient.systems.modules.combat.ArrowDodge;
import meteordevelopment.meteorclient.systems.modules.combat.AutoAnvil;
import meteordevelopment.meteorclient.systems.modules.combat.AutoArmor;
import meteordevelopment.meteorclient.systems.modules.combat.AutoEXP;
import meteordevelopment.meteorclient.systems.modules.combat.AutoMine;
import meteordevelopment.meteorclient.systems.modules.combat.AutoTrap;
import meteordevelopment.meteorclient.systems.modules.combat.AutoWeapon;
import meteordevelopment.meteorclient.systems.modules.combat.AutoWeb;
import meteordevelopment.meteorclient.systems.modules.combat.BasePlace;
import meteordevelopment.meteorclient.systems.modules.combat.BedAura;
import meteordevelopment.meteorclient.systems.modules.combat.BowAimbot;
import meteordevelopment.meteorclient.systems.modules.combat.BowSpam;
import meteordevelopment.meteorclient.systems.modules.combat.Burrow;
import meteordevelopment.meteorclient.systems.modules.combat.ChineseAura;
import meteordevelopment.meteorclient.systems.modules.combat.Criticals;
import meteordevelopment.meteorclient.systems.modules.combat.ForceSwim;
import meteordevelopment.meteorclient.systems.modules.combat.Hitboxes;
import meteordevelopment.meteorclient.systems.modules.combat.HoleFiller;
import meteordevelopment.meteorclient.systems.modules.combat.KillAura;
import meteordevelopment.meteorclient.systems.modules.combat.Offhand;
import meteordevelopment.meteorclient.systems.modules.combat.PearlPhase;
import meteordevelopment.meteorclient.systems.modules.combat.Quiver;
import meteordevelopment.meteorclient.systems.modules.combat.SelfAnvil;
import meteordevelopment.meteorclient.systems.modules.combat.SelfWeb;
import meteordevelopment.meteorclient.systems.modules.combat.Surround;
import meteordevelopment.meteorclient.systems.modules.combat.SwordAura;
import meteordevelopment.meteorclient.systems.modules.combat.autocrystal.AutoCrystal;
import meteordevelopment.meteorclient.systems.modules.misc.AntiPacketKick;
import meteordevelopment.meteorclient.systems.modules.misc.AutoLog;
import meteordevelopment.meteorclient.systems.modules.misc.AutoReconnect;
import meteordevelopment.meteorclient.systems.modules.misc.AutoRespawn;
import meteordevelopment.meteorclient.systems.modules.misc.BetterBeacons;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import meteordevelopment.meteorclient.systems.modules.misc.BookBot;
import meteordevelopment.meteorclient.systems.modules.misc.DebugModule;
import meteordevelopment.meteorclient.systems.modules.misc.DiscordPresence;
import meteordevelopment.meteorclient.systems.modules.misc.IllegalDisconnect;
import meteordevelopment.meteorclient.systems.modules.misc.InventoryTweaks;
import meteordevelopment.meteorclient.systems.modules.misc.MessageAura;
import meteordevelopment.meteorclient.systems.modules.misc.NameProtect;
import meteordevelopment.meteorclient.systems.modules.misc.Notebot;
import meteordevelopment.meteorclient.systems.modules.misc.Notifier;
import meteordevelopment.meteorclient.systems.modules.misc.PacketCanceller;
import meteordevelopment.meteorclient.systems.modules.misc.PacketSaver;
import meteordevelopment.meteorclient.systems.modules.misc.ServerSpoof;
import meteordevelopment.meteorclient.systems.modules.misc.SoundBlocker;
import meteordevelopment.meteorclient.systems.modules.misc.Spam;
import meteordevelopment.meteorclient.systems.modules.misc.TridentDupe;
import meteordevelopment.meteorclient.systems.modules.misc.swarm.Swarm;
import meteordevelopment.meteorclient.systems.modules.movement.AirJump;
import meteordevelopment.meteorclient.systems.modules.movement.Anchor;
import meteordevelopment.meteorclient.systems.modules.movement.AntiAFK;
import meteordevelopment.meteorclient.systems.modules.movement.AntiVoid;
import meteordevelopment.meteorclient.systems.modules.movement.AutoJump;
import meteordevelopment.meteorclient.systems.modules.movement.AutoWalk;
import meteordevelopment.meteorclient.systems.modules.movement.AutoWasp;
import meteordevelopment.meteorclient.systems.modules.movement.Blink;
import meteordevelopment.meteorclient.systems.modules.movement.BoatFly;
import meteordevelopment.meteorclient.systems.modules.movement.ClickTP;
import meteordevelopment.meteorclient.systems.modules.movement.ElytraBoost;
import meteordevelopment.meteorclient.systems.modules.movement.ElytraFakeFly;
import meteordevelopment.meteorclient.systems.modules.movement.ElytraSpeed;
import meteordevelopment.meteorclient.systems.modules.movement.EntityControl;
import meteordevelopment.meteorclient.systems.modules.movement.EntitySpeed;
import meteordevelopment.meteorclient.systems.modules.movement.FastClimb;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.meteorclient.systems.modules.movement.GUIMove;
import meteordevelopment.meteorclient.systems.modules.movement.GrimDisabler;
import meteordevelopment.meteorclient.systems.modules.movement.HighJump;
import meteordevelopment.meteorclient.systems.modules.movement.Jesus;
import meteordevelopment.meteorclient.systems.modules.movement.LongJump;
import meteordevelopment.meteorclient.systems.modules.movement.MovementFix;
import meteordevelopment.meteorclient.systems.modules.movement.NoFall;
import meteordevelopment.meteorclient.systems.modules.movement.NoJumpDelay;
import meteordevelopment.meteorclient.systems.modules.movement.NoSlow;
import meteordevelopment.meteorclient.systems.modules.movement.Parkour;
import meteordevelopment.meteorclient.systems.modules.movement.ReverseStep;
import meteordevelopment.meteorclient.systems.modules.movement.SafeWalk;
import meteordevelopment.meteorclient.systems.modules.movement.Scaffold;
import meteordevelopment.meteorclient.systems.modules.movement.Slippy;
import meteordevelopment.meteorclient.systems.modules.movement.Sneak;
import meteordevelopment.meteorclient.systems.modules.movement.Spider;
import meteordevelopment.meteorclient.systems.modules.movement.Sprint;
import meteordevelopment.meteorclient.systems.modules.movement.Step;
import meteordevelopment.meteorclient.systems.modules.movement.TridentBoost;
import meteordevelopment.meteorclient.systems.modules.movement.VanillaFakeFly;
import meteordevelopment.meteorclient.systems.modules.movement.Velocity;
import meteordevelopment.meteorclient.systems.modules.movement.elytrafly.ElytraFly;
import meteordevelopment.meteorclient.systems.modules.movement.speed.Speed;
import meteordevelopment.meteorclient.systems.modules.player.AntiHunger;
import meteordevelopment.meteorclient.systems.modules.player.AutoClicker;
import meteordevelopment.meteorclient.systems.modules.player.AutoEat;
import meteordevelopment.meteorclient.systems.modules.player.AutoFish;
import meteordevelopment.meteorclient.systems.modules.player.AutoGap;
import meteordevelopment.meteorclient.systems.modules.player.AutoMend;
import meteordevelopment.meteorclient.systems.modules.player.AutoReplenish;
import meteordevelopment.meteorclient.systems.modules.player.AutoTool;
import meteordevelopment.meteorclient.systems.modules.player.BreakDelay;
import meteordevelopment.meteorclient.systems.modules.player.ChestSwap;
import meteordevelopment.meteorclient.systems.modules.player.EXPThrower;
import meteordevelopment.meteorclient.systems.modules.player.FakePlayer;
import meteordevelopment.meteorclient.systems.modules.player.FastUse;
import meteordevelopment.meteorclient.systems.modules.player.GhostHand;
import meteordevelopment.meteorclient.systems.modules.player.HotbarLock;
import meteordevelopment.meteorclient.systems.modules.player.InstantRebreak;
import meteordevelopment.meteorclient.systems.modules.player.LiquidInteract;
import meteordevelopment.meteorclient.systems.modules.player.MiddleClickExtra;
import meteordevelopment.meteorclient.systems.modules.player.Multitask;
import meteordevelopment.meteorclient.systems.modules.player.NoInteract;
import meteordevelopment.meteorclient.systems.modules.player.NoMiningTrace;
import meteordevelopment.meteorclient.systems.modules.player.NoRotate;
import meteordevelopment.meteorclient.systems.modules.player.OffhandCrash;
import meteordevelopment.meteorclient.systems.modules.player.Portals;
import meteordevelopment.meteorclient.systems.modules.player.PotionSaver;
import meteordevelopment.meteorclient.systems.modules.player.PotionSpoof;
import meteordevelopment.meteorclient.systems.modules.player.Reach;
import meteordevelopment.meteorclient.systems.modules.player.Rotation;
import meteordevelopment.meteorclient.systems.modules.player.SilentMine;
import meteordevelopment.meteorclient.systems.modules.player.SpeedMine;
import meteordevelopment.meteorclient.systems.modules.render.BetterTab;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.systems.modules.render.BlockSelection;
import meteordevelopment.meteorclient.systems.modules.render.Blur;
import meteordevelopment.meteorclient.systems.modules.render.BossStack;
import meteordevelopment.meteorclient.systems.modules.render.Breadcrumbs;
import meteordevelopment.meteorclient.systems.modules.render.BreakIndicators;
import meteordevelopment.meteorclient.systems.modules.render.CameraTweaks;
import meteordevelopment.meteorclient.systems.modules.render.Chams;
import meteordevelopment.meteorclient.systems.modules.render.CityESP;
import meteordevelopment.meteorclient.systems.modules.render.ESP;
import meteordevelopment.meteorclient.systems.modules.render.EntityOwner;
import meteordevelopment.meteorclient.systems.modules.render.FOVChanger;
import meteordevelopment.meteorclient.systems.modules.render.FreeLook;
import meteordevelopment.meteorclient.systems.modules.render.Freecam;
import meteordevelopment.meteorclient.systems.modules.render.Fullbright;
import meteordevelopment.meteorclient.systems.modules.render.HandView;
import meteordevelopment.meteorclient.systems.modules.render.HoleESP;
import meteordevelopment.meteorclient.systems.modules.render.ItemHighlight;
import meteordevelopment.meteorclient.systems.modules.render.ItemPhysics;
import meteordevelopment.meteorclient.systems.modules.render.LightOverlay;
import meteordevelopment.meteorclient.systems.modules.render.LogoutSpots;
import meteordevelopment.meteorclient.systems.modules.render.Nametags;
import meteordevelopment.meteorclient.systems.modules.render.NoRender;
import meteordevelopment.meteorclient.systems.modules.render.PhaseESP;
import meteordevelopment.meteorclient.systems.modules.render.PopChams;
import meteordevelopment.meteorclient.systems.modules.render.StorageESP;
import meteordevelopment.meteorclient.systems.modules.render.TimeChanger;
import meteordevelopment.meteorclient.systems.modules.render.Tracers;
import meteordevelopment.meteorclient.systems.modules.render.Trail;
import meteordevelopment.meteorclient.systems.modules.render.Trajectories;
import meteordevelopment.meteorclient.systems.modules.render.TunnelESP;
import meteordevelopment.meteorclient.systems.modules.render.UnfocusedCPU;
import meteordevelopment.meteorclient.systems.modules.render.VoidESP;
import meteordevelopment.meteorclient.systems.modules.render.WallHack;
import meteordevelopment.meteorclient.systems.modules.render.WaypointsModule;
import meteordevelopment.meteorclient.systems.modules.render.Xray;
import meteordevelopment.meteorclient.systems.modules.render.Zoom;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.BlockESP;
import meteordevelopment.meteorclient.systems.modules.render.marker.Marker;
import meteordevelopment.meteorclient.systems.modules.world.AirPlace;
import meteordevelopment.meteorclient.systems.modules.world.Ambience;
import meteordevelopment.meteorclient.systems.modules.world.AutoBreed;
import meteordevelopment.meteorclient.systems.modules.world.AutoBrewer;
import meteordevelopment.meteorclient.systems.modules.world.AutoMount;
import meteordevelopment.meteorclient.systems.modules.world.AutoNametag;
import meteordevelopment.meteorclient.systems.modules.world.AutoPortal;
import meteordevelopment.meteorclient.systems.modules.world.AutoShearer;
import meteordevelopment.meteorclient.systems.modules.world.AutoSign;
import meteordevelopment.meteorclient.systems.modules.world.AutoSmelter;
import meteordevelopment.meteorclient.systems.modules.world.BuildHeight;
import meteordevelopment.meteorclient.systems.modules.world.Collisions;
import meteordevelopment.meteorclient.systems.modules.world.EChestFarmer;
import meteordevelopment.meteorclient.systems.modules.world.EndermanLook;
import meteordevelopment.meteorclient.systems.modules.world.Excavator;
import meteordevelopment.meteorclient.systems.modules.world.Filler;
import meteordevelopment.meteorclient.systems.modules.world.Flamethrower;
import meteordevelopment.meteorclient.systems.modules.world.HighwayBuilder;
import meteordevelopment.meteorclient.systems.modules.world.InfinityMiner;
import meteordevelopment.meteorclient.systems.modules.world.LiquidFiller;
import meteordevelopment.meteorclient.systems.modules.world.MapAura;
import meteordevelopment.meteorclient.systems.modules.world.MountBypass;
import meteordevelopment.meteorclient.systems.modules.world.NoGhostBlocks;
import meteordevelopment.meteorclient.systems.modules.world.Nuker;
import meteordevelopment.meteorclient.systems.modules.world.SourceFiller;
import meteordevelopment.meteorclient.systems.modules.world.SpawnProofer;
import meteordevelopment.meteorclient.systems.modules.world.StashFinder;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.meteorclient.systems.modules.world.VeinMiner;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.ValueComparableMap;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_2370;
import net.minecraft.class_2378;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2520;
import net.minecraft.class_2960;
import net.minecraft.class_408;
import net.minecraft.class_5321;
import net.minecraft.class_5819;
import net.minecraft.class_6862;
import net.minecraft.class_6880;
import net.minecraft.class_6880.class_6883;
import net.minecraft.class_6885.class_6888;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Modules extends System<Modules> {
   public static final Modules.ModuleRegistry REGISTRY = new Modules.ModuleRegistry();
   private static final List<Category> CATEGORIES = new ArrayList();
   private final List<Module> modules = new ArrayList();
   private final Map<Class<? extends Module>, Module> moduleInstances = new Reference2ReferenceOpenHashMap();
   private final Map<Category, List<Module>> groups = new Reference2ReferenceOpenHashMap();
   private final List<Module> active = new ArrayList();
   private Module moduleToBind;
   private boolean awaitingKeyRelease = false;

   public Modules() {
      super("modules");
   }

   public static Modules get() {
      return (Modules)Systems.get(Modules.class);
   }

   public void init() {
      this.initCombat();
      this.initPlayer();
      this.initMovement();
      this.initRender();
      this.initWorld();
      this.initMisc();
   }

   public void load(File folder) {
      Iterator var2 = this.modules.iterator();

      while(var2.hasNext()) {
         Module module = (Module)var2.next();
         Iterator var4 = module.settings.iterator();

         while(var4.hasNext()) {
            SettingGroup group = (SettingGroup)var4.next();
            Iterator var6 = group.iterator();

            while(var6.hasNext()) {
               Setting<?> setting = (Setting)var6.next();
               setting.reset();
            }
         }
      }

      super.load(folder);
   }

   public void sortModules() {
      Iterator var1 = this.groups.values().iterator();

      while(var1.hasNext()) {
         List<Module> modules = (List)var1.next();
         modules.sort(Comparator.comparing((o) -> {
            return o.title;
         }));
      }

      this.modules.sort(Comparator.comparing((o) -> {
         return o.title;
      }));
   }

   public static void registerCategory(Category category) {
      if (!Categories.REGISTERING) {
         throw new RuntimeException("Modules.registerCategory - Cannot register category outside of onRegisterCategories callback.");
      } else {
         CATEGORIES.add(category);
      }
   }

   public static Iterable<Category> loopCategories() {
      return CATEGORIES;
   }

   /** @deprecated */
   @Deprecated(
      forRemoval = true
   )
   public static Category getCategoryByHash(int hash) {
      Iterator var1 = CATEGORIES.iterator();

      Category category;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         category = (Category)var1.next();
      } while(category.hashCode() != hash);

      return category;
   }

   public <T extends Module> T get(Class<T> klass) {
      return (Module)this.moduleInstances.get(klass);
   }

   public Module get(String name) {
      Iterator var2 = this.moduleInstances.values().iterator();

      Module module;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         module = (Module)var2.next();
      } while(!module.name.equalsIgnoreCase(name));

      return module;
   }

   public boolean isActive(Class<? extends Module> klass) {
      Module module = this.get(klass);
      return module != null && module.isActive();
   }

   public List<Module> getGroup(Category category) {
      return (List)this.groups.computeIfAbsent(category, (category1) -> {
         return new ArrayList();
      });
   }

   public Collection<Module> getAll() {
      return this.moduleInstances.values();
   }

   public List<Module> getList() {
      return this.modules;
   }

   public int getCount() {
      return this.moduleInstances.values().size();
   }

   public List<Module> getActive() {
      synchronized(this.active) {
         return this.active;
      }
   }

   public Map<Module, Integer> searchTitles(String text) {
      Map<Module, Integer> modules = new ValueComparableMap(Comparator.naturalOrder());

      Module module;
      int score;
      for(Iterator var3 = this.moduleInstances.values().iterator(); var3.hasNext(); modules.put(module, (Integer)modules.getOrDefault(module, 0) + score)) {
         module = (Module)var3.next();
         score = Utils.searchLevenshteinDefault(module.title, text, false);
         if ((Boolean)Config.get().moduleAliases.get()) {
            String[] var6 = module.aliases;
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               String alias = var6[var8];
               int aliasScore = Utils.searchLevenshteinDefault(alias, text, false);
               if (aliasScore < score) {
                  score = aliasScore;
               }
            }
         }
      }

      return modules;
   }

   public Set<Module> searchSettingTitles(String text) {
      Map<Module, Integer> modules = new ValueComparableMap(Comparator.naturalOrder());
      Iterator var3 = this.moduleInstances.values().iterator();

      while(var3.hasNext()) {
         Module module = (Module)var3.next();
         int lowest = Integer.MAX_VALUE;
         Iterator var6 = module.settings.iterator();

         while(var6.hasNext()) {
            SettingGroup sg = (SettingGroup)var6.next();
            Iterator var8 = sg.iterator();

            while(var8.hasNext()) {
               Setting<?> setting = (Setting)var8.next();
               int score = Utils.searchLevenshteinDefault(setting.title, text, false);
               if (score < lowest) {
                  lowest = score;
               }
            }
         }

         modules.put(module, (Integer)modules.getOrDefault(module, 0) + lowest);
      }

      return modules.keySet();
   }

   void addActive(Module module) {
      synchronized(this.active) {
         if (!this.active.contains(module)) {
            this.active.add(module);
            MeteorClient.EVENT_BUS.post((Object)ActiveModulesChangedEvent.get());
         }

      }
   }

   void removeActive(Module module) {
      synchronized(this.active) {
         if (this.active.remove(module)) {
            MeteorClient.EVENT_BUS.post((Object)ActiveModulesChangedEvent.get());
         }

      }
   }

   public void setModuleToBind(Module moduleToBind) {
      this.moduleToBind = moduleToBind;
   }

   public void awaitKeyRelease() {
      this.awaitingKeyRelease = true;
   }

   public boolean isBinding() {
      return this.moduleToBind != null;
   }

   @EventHandler(
      priority = 200
   )
   private void onKeyBinding(KeyEvent event) {
      if (event.action == KeyAction.Release && this.onBinding(true, event.key, event.modifiers)) {
         event.cancel();
      }

   }

   @EventHandler(
      priority = 200
   )
   private void onButtonBinding(MouseButtonEvent event) {
      if (event.action == KeyAction.Release && this.onBinding(false, event.button, 0)) {
         event.cancel();
      }

   }

   private boolean onBinding(boolean isKey, int value, int modifiers) {
      if (!this.isBinding()) {
         return false;
      } else if (!this.awaitingKeyRelease) {
         if (this.moduleToBind.keybind.canBindTo(isKey, value, modifiers)) {
            this.moduleToBind.keybind.set(isKey, value, modifiers);
            this.moduleToBind.info("Bound to (highlight)%s(default).", this.moduleToBind.keybind);
         } else {
            if (value != 256) {
               return false;
            }

            this.moduleToBind.keybind.set(Keybind.none());
            this.moduleToBind.info("Removed bind.");
         }

         MeteorClient.EVENT_BUS.post((Object)ModuleBindChangedEvent.get(this.moduleToBind));
         this.moduleToBind = null;
         return true;
      } else if (isKey && (value == 257 || value == 335)) {
         this.awaitingKeyRelease = false;
         return false;
      } else {
         return false;
      }
   }

   @EventHandler(
      priority = 100
   )
   private void onKey(KeyEvent event) {
      if (event.action != KeyAction.Repeat) {
         this.onAction(true, event.key, event.modifiers, event.action == KeyAction.Press);
      }
   }

   @EventHandler(
      priority = 100
   )
   private void onMouseButton(MouseButtonEvent event) {
      if (event.action != KeyAction.Repeat) {
         this.onAction(false, event.button, 0, event.action == KeyAction.Press);
      }
   }

   private void onAction(boolean isKey, int value, int modifiers, boolean isPress) {
      if (MeteorClient.mc.field_1755 == null && !Input.isKeyPressed(292)) {
         Iterator var5 = this.moduleInstances.values().iterator();

         while(var5.hasNext()) {
            Module module = (Module)var5.next();
            if (module.keybind.matches(isKey, value, modifiers) && isPress) {
               module.toggle();
               module.sendToggledMsg();
            }
         }

      }
   }

   @EventHandler(
      priority = 201
   )
   private void onOpenScreen(OpenScreenEvent event) {
      if (Utils.canUpdate()) {
         Iterator var2 = this.moduleInstances.values().iterator();

         while(var2.hasNext()) {
            Module module = (Module)var2.next();
            if (module.toggleOnBindRelease && module.isActive()) {
               module.toggle();
               module.sendToggledMsg();
            }
         }

      }
   }

   @EventHandler
   private void onGameJoined(GameJoinedEvent event) {
      synchronized(this.active) {
         Iterator var3 = this.modules.iterator();

         while(var3.hasNext()) {
            Module module = (Module)var3.next();
            if (module.isActive() && !module.runInMainMenu) {
               MeteorClient.EVENT_BUS.subscribe((Object)module);
               module.onActivate();
            }
         }

      }
   }

   @EventHandler
   private void onGameLeft(GameLeftEvent event) {
      synchronized(this.active) {
         Iterator var3 = this.modules.iterator();

         while(var3.hasNext()) {
            Module module = (Module)var3.next();
            if (module.isActive() && !module.runInMainMenu) {
               MeteorClient.EVENT_BUS.unsubscribe((Object)module);
               module.onDeactivate();
            }
         }

      }
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      Iterator var2 = this.moduleInstances.values().iterator();

      while(true) {
         while(true) {
            Module module;
            do {
               if (!var2.hasNext()) {
                  return;
               }

               module = (Module)var2.next();
            } while(!module.toggleOnBindRelease);

            if (module.keybind.isPressed() && !(MeteorClient.mc.field_1755 instanceof class_408)) {
               if (!module.isActive()) {
                  module.toggle();
               }
            } else if (module.isActive()) {
               module.toggle();
            }
         }
      }
   }

   public void disableAll() {
      synchronized(this.active) {
         Iterator var2 = this.modules.iterator();

         while(var2.hasNext()) {
            Module module = (Module)var2.next();
            if (module.isActive()) {
               module.toggle();
            }
         }

      }
   }

   public class_2487 toTag() {
      class_2487 tag = new class_2487();
      class_2499 modulesTag = new class_2499();
      Iterator var3 = this.getAll().iterator();

      while(var3.hasNext()) {
         Module module = (Module)var3.next();
         class_2487 moduleTag = module.toTag();
         if (moduleTag != null) {
            modulesTag.add(moduleTag);
         }
      }

      tag.method_10566("modules", modulesTag);
      return tag;
   }

   public Modules fromTag(class_2487 tag) {
      this.disableAll();
      class_2499 modulesTag = tag.method_10554("modules", 10);
      Iterator var3 = modulesTag.iterator();

      while(var3.hasNext()) {
         class_2520 moduleTagI = (class_2520)var3.next();
         class_2487 moduleTag = (class_2487)moduleTagI;
         Module module = this.get(moduleTag.method_10558("name"));
         if (module != null) {
            module.fromTag(moduleTag);
         }
      }

      return this;
   }

   public void add(Module module) {
      if (!CATEGORIES.contains(module.category)) {
         throw new RuntimeException("Modules.addModule - Module's category was not registered.");
      } else {
         AtomicReference<Module> removedModule = new AtomicReference();
         if (this.moduleInstances.values().removeIf((module1) -> {
            if (module1.name.equals(module.name)) {
               removedModule.set(module1);
               module1.settings.unregisterColorSettings();
               return true;
            } else {
               return false;
            }
         })) {
            this.getGroup(((Module)removedModule.get()).category).remove(removedModule.get());
         }

         this.moduleInstances.put(module.getClass(), module);
         this.modules.add(module);
         this.getGroup(module.category).add(module);
         module.settings.registerColorSettings(module);
      }
   }

   private void initCombat() {
      this.add(new AnchorAura());
      this.add(new BasePlace());
      this.add(new AntiAnvil());
      this.add(new AntiBed());
      this.add(new ArrowDodge());
      this.add(new AutoAnvil());
      this.add(new AutoArmor());
      this.add(new AutoEXP());
      this.add(new AutoTrap());
      this.add(new AutoWeapon());
      this.add(new AutoWeb());
      this.add(new BedAura());
      this.add(new BowAimbot());
      this.add(new BowSpam());
      this.add(new Burrow());
      this.add(new Criticals());
      this.add(new Hitboxes());
      this.add(new HoleFiller());
      this.add(new KillAura());
      this.add(new Offhand());
      this.add(new Quiver());
      this.add(new SelfAnvil());
      this.add(new SelfWeb());
      this.add(new Surround());
      this.add(new AutoCrystal());
      this.add(new AutoMine());
      this.add(new ForceSwim());
      this.add(new SwordAura());
      this.add(new AntiDigDown());
      this.add(new ChineseAura());
   }

   private void initPlayer() {
      this.add(new AntiHunger());
      this.add(new AutoEat());
      this.add(new AutoClicker());
      this.add(new AutoFish());
      this.add(new AutoGap());
      this.add(new AutoMend());
      this.add(new AutoReplenish());
      this.add(new AutoTool());
      this.add(new BreakDelay());
      this.add(new ChestSwap());
      this.add(new EXPThrower());
      this.add(new FakePlayer());
      this.add(new FastUse());
      this.add(new GhostHand());
      this.add(new InstantRebreak());
      this.add(new LiquidInteract());
      this.add(new MiddleClickExtra());
      this.add(new Multitask());
      this.add(new NoInteract());
      this.add(new NoMiningTrace());
      this.add(new NoRotate());
      this.add(new OffhandCrash());
      this.add(new Portals());
      this.add(new PotionSaver());
      this.add(new PotionSpoof());
      this.add(new Reach());
      this.add(new Rotation());
      this.add(new SpeedMine());
      this.add(new PearlPhase());
      this.add(new HotbarLock());
   }

   private void initMovement() {
      this.add(new AirJump());
      this.add(new Anchor());
      this.add(new AntiAFK());
      this.add(new AntiVoid());
      this.add(new AutoJump());
      this.add(new AutoWalk());
      this.add(new AutoWasp());
      this.add(new Blink());
      this.add(new BoatFly());
      this.add(new ClickTP());
      this.add(new ElytraBoost());
      this.add(new ElytraSpeed());
      this.add(new ElytraFly());
      this.add(new EntityControl());
      this.add(new EntitySpeed());
      this.add(new FastClimb());
      this.add(new Flight());
      this.add(new GUIMove());
      this.add(new HighJump());
      this.add(new Jesus());
      this.add(new LongJump());
      this.add(new NoFall());
      this.add(new NoSlow());
      this.add(new Parkour());
      this.add(new ReverseStep());
      this.add(new SafeWalk());
      this.add(new Scaffold());
      this.add(new Slippy());
      this.add(new Sneak());
      this.add(new Speed());
      this.add(new Spider());
      this.add(new Sprint());
      this.add(new Step());
      this.add(new TridentBoost());
      this.add(new Velocity());
      this.add(new ElytraFakeFly());
      this.add(new MovementFix());
      this.add(new GrimDisabler());
      this.add(new NoJumpDelay());
      this.add(new VanillaFakeFly());
   }

   private void initRender() {
      this.add(new BetterTooltips());
      this.add(new BlockSelection());
      this.add(new BossStack());
      this.add(new Breadcrumbs());
      this.add(new BreakIndicators());
      this.add(new CameraTweaks());
      this.add(new Chams());
      this.add(new CityESP());
      this.add(new EntityOwner());
      this.add(new ESP());
      this.add(new Freecam());
      this.add(new FOVChanger());
      this.add(new FreeLook());
      this.add(new Fullbright());
      this.add(new HandView());
      this.add(new HoleESP());
      this.add(new ItemPhysics());
      this.add(new ItemHighlight());
      this.add(new LightOverlay());
      this.add(new LogoutSpots());
      this.add(new Marker());
      this.add(new Nametags());
      this.add(new NoRender());
      this.add(new BlockESP());
      this.add(new StorageESP());
      this.add(new TimeChanger());
      this.add(new Tracers());
      this.add(new Trail());
      this.add(new Trajectories());
      this.add(new UnfocusedCPU());
      this.add(new VoidESP());
      this.add(new WallHack());
      this.add(new WaypointsModule());
      this.add(new Xray());
      this.add(new Zoom());
      this.add(new Blur());
      this.add(new PopChams());
      this.add(new TunnelESP());
      this.add(new BetterTab());
      this.add(new PhaseESP());
   }

   private void initWorld() {
      this.add(new AirPlace());
      this.add(new Ambience());
      this.add(new AutoBreed());
      this.add(new AutoBrewer());
      this.add(new AutoMount());
      this.add(new AutoNametag());
      this.add(new AutoShearer());
      this.add(new AutoSign());
      this.add(new AutoSmelter());
      this.add(new BuildHeight());
      this.add(new Collisions());
      this.add(new EChestFarmer());
      this.add(new EndermanLook());
      this.add(new Flamethrower());
      this.add(new HighwayBuilder());
      this.add(new LiquidFiller());
      this.add(new MountBypass());
      this.add(new NoGhostBlocks());
      this.add(new Nuker());
      this.add(new SilentMine());
      this.add(new StashFinder());
      this.add(new SpawnProofer());
      this.add(new Timer());
      this.add(new VeinMiner());
      this.add(new SourceFiller());
      this.add(new MapAura());
      this.add(new AutoPortal());
      this.add(new Filler());
      if (BaritoneUtils.IS_AVAILABLE) {
         this.add(new Excavator());
         this.add(new InfinityMiner());
      }

   }

   private void initMisc() {
      this.add(new Swarm());
      this.add(new AntiPacketKick());
      this.add(new AutoLog());
      this.add(new AutoReconnect());
      this.add(new AutoRespawn());
      this.add(new BetterBeacons());
      this.add(new BetterChat());
      this.add(new BookBot());
      this.add(new DiscordPresence());
      this.add(new InventoryTweaks());
      this.add(new IllegalDisconnect());
      this.add(new MessageAura());
      this.add(new NameProtect());
      this.add(new Notebot());
      this.add(new Notifier());
      this.add(new PacketCanceller());
      this.add(new ServerSpoof());
      this.add(new SoundBlocker());
      this.add(new TridentDupe());
      this.add(new Spam());
      this.add(new PacketSaver());
      this.add(new DebugModule());
   }

   public static class ModuleRegistry extends class_2370<Module> {
      public ModuleRegistry() {
         super(class_5321.method_29180(MeteorClient.identifier("modules")), Lifecycle.stable());
      }

      public int method_10204() {
         return Modules.get().getAll().size();
      }

      public class_2960 getId(Module entry) {
         return null;
      }

      public Optional<class_5321<Module>> getKey(Module entry) {
         return Optional.empty();
      }

      public int getRawId(Module entry) {
         return 0;
      }

      public Module get(class_5321<Module> key) {
         return null;
      }

      public Module get(class_2960 id) {
         return null;
      }

      public Lifecycle method_31138() {
         return null;
      }

      public Set<class_2960> method_10235() {
         return null;
      }

      public boolean method_10250(class_2960 id) {
         return false;
      }

      @Nullable
      public Module get(int index) {
         return null;
      }

      @NotNull
      public Iterator<Module> iterator() {
         return new Modules.ModuleRegistry.ModuleIterator();
      }

      public boolean method_35842(class_5321<Module> key) {
         return false;
      }

      public Set<Entry<class_5321<Module>, Module>> method_29722() {
         return null;
      }

      public Set<class_5321<Module>> method_42021() {
         return null;
      }

      public Optional<class_6883<Module>> method_10240(class_5819 random) {
         return Optional.empty();
      }

      public class_2378<Module> method_40276() {
         return null;
      }

      public class_6883<Module> createEntry(Module value) {
         return null;
      }

      public Optional<class_6883<Module>> method_40265(int rawId) {
         return Optional.empty();
      }

      public Optional<class_6883<Module>> method_40264(class_5321<Module> key) {
         return Optional.empty();
      }

      public Stream<class_6883<Module>> method_40270() {
         return null;
      }

      public Optional<class_6888<Module>> method_40266(class_6862<Module> tag) {
         return Optional.empty();
      }

      public class_6888<Module> method_40260(class_6862<Module> tag) {
         return null;
      }

      public Stream<Pair<class_6862<Module>, class_6888<Module>>> method_40272() {
         return null;
      }

      public Stream<class_6862<Module>> method_40273() {
         return null;
      }

      public void method_40278() {
      }

      public void method_40257(Map<class_6862<Module>, List<class_6880<Module>>> tagEntries) {
      }

      private static class ModuleIterator implements Iterator<Module> {
         private final Iterator<Module> iterator = Modules.get().getAll().iterator();

         public boolean hasNext() {
            return this.iterator.hasNext();
         }

         public Module next() {
            return (Module)this.iterator.next();
         }
      }
   }
}
