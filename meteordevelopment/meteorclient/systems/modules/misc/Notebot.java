package meteordevelopment.meteorclient.systems.modules.misc;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.notebot.NotebotUtils;
import meteordevelopment.meteorclient.utils.notebot.decoder.SongDecoders;
import meteordevelopment.meteorclient.utils.notebot.instrumentdetect.InstrumentDetectMode;
import meteordevelopment.meteorclient.utils.notebot.song.Note;
import meteordevelopment.meteorclient.utils.notebot.song.Song;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.NametagUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.class_1268;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2428;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2766;
import net.minecraft.class_2846;
import net.minecraft.class_2885;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3965;
import net.minecraft.class_2846.class_2847;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class Notebot extends Module {
   private final SettingGroup sgGeneral;
   private final SettingGroup sgNoteMap;
   private final SettingGroup sgRender;
   public final Setting<Integer> tickDelay;
   public final Setting<Integer> concurrentTuneBlocks;
   public final Setting<NotebotUtils.NotebotMode> mode;
   public final Setting<InstrumentDetectMode> instrumentDetectMode;
   public final Setting<Boolean> polyphonic;
   public final Setting<Boolean> autoRotate;
   public final Setting<Boolean> autoPlay;
   public final Setting<Boolean> roundOutOfRange;
   public final Setting<Boolean> swingArm;
   public final Setting<Integer> checkNoteblocksAgainDelay;
   public final Setting<Boolean> renderText;
   public final Setting<Boolean> renderBoxes;
   public final Setting<ShapeMode> shapeMode;
   public final Setting<SettingColor> untunedSideColor;
   public final Setting<SettingColor> untunedLineColor;
   public final Setting<SettingColor> tunedSideColor;
   public final Setting<SettingColor> tunedLineColor;
   public final Setting<SettingColor> tuneHitSideColor;
   private final Setting<SettingColor> tuneHitLineColor;
   public final Setting<SettingColor> scannedNoteblockSideColor;
   private final Setting<SettingColor> scannedNoteblockLineColor;
   public final Setting<Double> noteTextScale;
   public final Setting<Boolean> showScannedNoteblocks;
   private CompletableFuture<Song> loadingSongFuture;
   private Song song;
   private final Map<Note, class_2338> noteBlockPositions;
   private final Multimap<Note, class_2338> scannedNoteblocks;
   private final List<class_2338> clickedBlocks;
   private Notebot.Stage stage;
   private Notebot.PlayingMode playingMode;
   private boolean isPlaying;
   private int currentTick;
   private int ticks;
   private WLabel status;
   private boolean anyNoteblockTuned;
   private final Map<class_2338, Integer> tuneHits;
   private int waitTicks;

   public Notebot() {
      super(Categories.Misc, "notebot", "Plays noteblock nicely");
      this.sgGeneral = this.settings.getDefaultGroup();
      this.sgNoteMap = this.settings.createGroup("Note Map", false);
      this.sgRender = this.settings.createGroup("Render", true);
      this.tickDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("tick-delay")).description("The delay when loading a song.")).defaultValue(1)).sliderRange(1, 20).min(1).build());
      this.concurrentTuneBlocks = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("concurrent-tune-blocks")).description("How many noteblocks can be tuned at the same time. On Paper it is recommended to set it to 1 to avoid bugs.")).defaultValue(1)).min(1).sliderRange(1, 20).build());
      this.mode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("mode")).description("Select mode of notebot")).defaultValue(NotebotUtils.NotebotMode.ExactInstruments)).build());
      this.instrumentDetectMode = this.sgGeneral.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("instrument-detect-mode")).description("Select an instrument detect mode. Can be useful when server has a plugin that modifies noteblock state (e.g ItemsAdder) but noteblock can still play the right note")).defaultValue(InstrumentDetectMode.BlockState)).build());
      this.polyphonic = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("polyphonic")).description("Whether or not to allow multiple notes to be played at the same time")).defaultValue(true)).build());
      this.autoRotate = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-rotate")).description("Should client look at note block when it wants to hit it")).defaultValue(true)).build());
      this.autoPlay = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("auto-play")).description("Auto plays random songs")).defaultValue(false)).build());
      this.roundOutOfRange = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("round-out-of-range")).description("Rounds out of range notes")).defaultValue(false)).build());
      this.swingArm = this.sgGeneral.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("swing-arm")).description("Should swing arm on hit")).defaultValue(true)).build());
      this.checkNoteblocksAgainDelay = this.sgGeneral.add(((IntSetting.Builder)((IntSetting.Builder)((IntSetting.Builder)(new IntSetting.Builder()).name("check-noteblocks-again-delay")).description("How much delay should be between end of tuning and checking again")).defaultValue(10)).min(1).sliderRange(1, 20).build());
      this.renderText = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-text")).description("Whether or not to render the text above noteblocks.")).defaultValue(true)).build());
      this.renderBoxes = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("render-boxes")).description("Whether or not to render the outline around the noteblocks.")).defaultValue(true)).build());
      this.shapeMode = this.sgRender.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name("shape-mode")).description("How the shapes are rendered.")).defaultValue(ShapeMode.Both)).build());
      this.untunedSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("untuned-side-color")).description("The color of the sides of the untuned blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 10)).build());
      this.untunedLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("untuned-line-color")).description("The color of the lines of the untuned blocks being rendered.")).defaultValue(new SettingColor(204, 0, 0, 255)).build());
      this.tunedSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("tuned-side-color")).description("The color of the sides of the tuned blocks being rendered.")).defaultValue(new SettingColor(0, 204, 0, 10)).build());
      this.tunedLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("tuned-line-color")).description("The color of the lines of the tuned blocks being rendered.")).defaultValue(new SettingColor(0, 204, 0, 255)).build());
      this.tuneHitSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("hit-side-color")).description("The color of the sides being rendered on noteblock tune hit.")).defaultValue(new SettingColor(255, 153, 0, 10)).build());
      this.tuneHitLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("hit-line-color")).description("The color of the lines being rendered on noteblock tune hit.")).defaultValue(new SettingColor(255, 153, 0, 255)).build());
      this.scannedNoteblockSideColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("scanned-noteblock-side-color")).description("The color of the sides of the scanned noteblocks being rendered.")).defaultValue(new SettingColor(255, 255, 0, 30)).build());
      this.scannedNoteblockLineColor = this.sgRender.add(((ColorSetting.Builder)((ColorSetting.Builder)(new ColorSetting.Builder()).name("scanned-noteblock-line-color")).description("The color of the lines of the scanned noteblocks being rendered.")).defaultValue(new SettingColor(255, 255, 0, 255)).build());
      this.noteTextScale = this.sgRender.add(((DoubleSetting.Builder)((DoubleSetting.Builder)(new DoubleSetting.Builder()).name("note-text-scale")).description("The scale.")).defaultValue(1.5D).min(0.0D).build());
      this.showScannedNoteblocks = this.sgRender.add(((BoolSetting.Builder)((BoolSetting.Builder)((BoolSetting.Builder)(new BoolSetting.Builder()).name("show-scanned-noteblocks")).description("Show scanned Noteblocks")).defaultValue(false)).build());
      this.loadingSongFuture = null;
      this.noteBlockPositions = new HashMap();
      this.scannedNoteblocks = MultimapBuilder.linkedHashKeys().arrayListValues().build();
      this.clickedBlocks = new ArrayList();
      this.stage = Notebot.Stage.None;
      this.playingMode = Notebot.PlayingMode.None;
      this.isPlaying = false;
      this.currentTick = 0;
      this.ticks = 0;
      this.anyNoteblockTuned = false;
      this.tuneHits = new HashMap();
      this.waitTicks = -1;
      class_2766[] var1 = class_2766.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         class_2766 inst = var1[var3];
         NotebotUtils.OptionalInstrument optionalInstrument = NotebotUtils.OptionalInstrument.fromMinecraftInstrument(inst);
         if (optionalInstrument != null) {
            this.sgNoteMap.add(((EnumSetting.Builder)((EnumSetting.Builder)((EnumSetting.Builder)(new EnumSetting.Builder()).name(this.beautifyText(inst.name()))).defaultValue(optionalInstrument)).visible(() -> {
               return this.mode.get() == NotebotUtils.NotebotMode.ExactInstruments;
            })).build());
         }
      }

   }

   public String getInfoString() {
      if (this.stage == Notebot.Stage.None) {
         return "None";
      } else {
         String var10000 = this.playingMode.toString();
         return var10000 + " | " + this.stage.toString();
      }
   }

   public void onActivate() {
      this.ticks = 0;
      this.resetVariables();
   }

   private void resetVariables() {
      if (this.loadingSongFuture != null) {
         this.loadingSongFuture.cancel(true);
         this.loadingSongFuture = null;
      }

      this.clickedBlocks.clear();
      this.tuneHits.clear();
      this.anyNoteblockTuned = false;
      this.currentTick = 0;
      this.playingMode = Notebot.PlayingMode.None;
      this.isPlaying = false;
      this.stage = Notebot.Stage.None;
      this.song = null;
      this.noteBlockPositions.clear();
   }

   @EventHandler
   private void onRender3D(Render3DEvent event) {
      if ((Boolean)this.renderBoxes.get()) {
         if (this.stage == Notebot.Stage.SetUp || this.stage == Notebot.Stage.Tune || this.stage == Notebot.Stage.WaitingToCheckNoteblocks || this.isPlaying) {
            Iterator var2;
            double x1;
            double y1;
            double z1;
            double x2;
            if ((Boolean)this.showScannedNoteblocks.get()) {
               var2 = this.scannedNoteblocks.values().iterator();

               while(var2.hasNext()) {
                  class_2338 blockPos = (class_2338)var2.next();
                  double x1 = (double)blockPos.method_10263();
                  double y1 = (double)blockPos.method_10264();
                  x1 = (double)blockPos.method_10260();
                  y1 = (double)(blockPos.method_10263() + 1);
                  z1 = (double)(blockPos.method_10264() + 1);
                  x2 = (double)(blockPos.method_10260() + 1);
                  event.renderer.box(x1, y1, x1, y1, z1, x2, (Color)this.scannedNoteblockSideColor.get(), (Color)this.scannedNoteblockLineColor.get(), (ShapeMode)this.shapeMode.get(), 0);
               }
            } else {
               var2 = this.noteBlockPositions.entrySet().iterator();

               while(var2.hasNext()) {
                  Entry<Note, class_2338> entry = (Entry)var2.next();
                  Note note = (Note)entry.getKey();
                  class_2338 blockPos = (class_2338)entry.getValue();
                  class_2680 state = this.mc.field_1687.method_8320(blockPos);
                  if (state.method_26204() == class_2246.field_10179) {
                     int level = (Integer)state.method_11654(class_2428.field_11324);
                     x1 = (double)blockPos.method_10263();
                     y1 = (double)blockPos.method_10264();
                     z1 = (double)blockPos.method_10260();
                     x2 = (double)(blockPos.method_10263() + 1);
                     double y2 = (double)(blockPos.method_10264() + 1);
                     double z2 = (double)(blockPos.method_10260() + 1);
                     Color sideColor;
                     Color lineColor;
                     if (this.clickedBlocks.contains(blockPos)) {
                        sideColor = (Color)this.tuneHitSideColor.get();
                        lineColor = (Color)this.tuneHitLineColor.get();
                     } else if (note.getNoteLevel() == level) {
                        sideColor = (Color)this.tunedSideColor.get();
                        lineColor = (Color)this.tunedLineColor.get();
                     } else {
                        sideColor = (Color)this.untunedSideColor.get();
                        lineColor = (Color)this.untunedLineColor.get();
                     }

                     event.renderer.box(x1, y1, z1, x2, y2, z2, sideColor, lineColor, (ShapeMode)this.shapeMode.get(), 0);
                  }
               }
            }

         }
      }
   }

   @EventHandler
   private void onRender2D(Render2DEvent event) {
      if ((Boolean)this.renderText.get()) {
         if (this.stage == Notebot.Stage.SetUp || this.stage == Notebot.Stage.Tune || this.stage == Notebot.Stage.WaitingToCheckNoteblocks || this.isPlaying) {
            Vector3d pos = new Vector3d();
            Iterator var3 = this.noteBlockPositions.values().iterator();

            while(var3.hasNext()) {
               class_2338 blockPos = (class_2338)var3.next();
               class_2680 state = this.mc.field_1687.method_8320(blockPos);
               if (state.method_26204() == class_2246.field_10179) {
                  double x = (double)blockPos.method_10263() + 0.5D;
                  double y = (double)(blockPos.method_10264() + 1);
                  double z = (double)blockPos.method_10260() + 0.5D;
                  pos.set(x, y, z);
                  String levelText = String.valueOf(state.method_11654(class_2428.field_11324));
                  String tuneHitsText = null;
                  if (this.tuneHits.containsKey(blockPos)) {
                     Object var10000 = this.tuneHits.get(blockPos);
                     tuneHitsText = " -" + String.valueOf(var10000);
                  }

                  if (NametagUtils.to2D(pos, (Double)this.noteTextScale.get(), true)) {
                     TextRenderer text = TextRenderer.get();
                     NametagUtils.begin(pos);
                     text.beginBig();
                     double xScreen = text.getWidth(levelText) / 2.0D;
                     if (tuneHitsText != null) {
                        xScreen += text.getWidth(tuneHitsText) / 2.0D;
                     }

                     double hX = text.render(levelText, -xScreen, 0.0D, Color.GREEN);
                     if (tuneHitsText != null) {
                        text.render(tuneHitsText, hX, 0.0D, Color.RED);
                     }

                     text.end();
                     NametagUtils.end();
                  }
               }
            }

         }
      }
   }

   @EventHandler
   private void onTick(TickEvent.Pre event) {
      ++this.ticks;
      this.clickedBlocks.clear();
      if (this.stage == Notebot.Stage.WaitingToCheckNoteblocks) {
         --this.waitTicks;
         if (this.waitTicks == 0) {
            this.waitTicks = -1;
            this.info("Checking noteblocks again...", new Object[0]);
            this.setupTuneHitsMap();
            this.stage = Notebot.Stage.Tune;
         }
      } else if (this.stage == Notebot.Stage.SetUp) {
         this.scanForNoteblocks();
         if (this.scannedNoteblocks.isEmpty()) {
            this.error("Can't find any nearby noteblock!", new Object[0]);
            this.stop();
            return;
         }

         this.setupNoteblocksMap();
         if (this.noteBlockPositions.isEmpty()) {
            this.error("Can't find any valid noteblock to play song.", new Object[0]);
            this.stop();
            return;
         }

         this.setupTuneHitsMap();
         this.stage = Notebot.Stage.Tune;
      } else if (this.stage == Notebot.Stage.Tune) {
         this.tune();
      } else if (this.stage == Notebot.Stage.Playing) {
         if (!this.isPlaying) {
            return;
         }

         if (this.mc.field_1724 == null || this.currentTick > this.song.getLastTick()) {
            this.onSongEnd();
            return;
         }

         if (this.song.getNotesMap().containsKey(this.currentTick)) {
            if (this.playingMode == Notebot.PlayingMode.Preview) {
               this.onTickPreview();
            } else {
               if (this.mc.field_1724.method_31549().field_7477) {
                  this.error("You need to be in survival mode.", new Object[0]);
                  this.stop();
                  return;
               }

               this.onTickPlay();
            }
         }

         ++this.currentTick;
         this.updateStatus();
      }

   }

   private void setupNoteblocksMap() {
      this.noteBlockPositions.clear();
      List<Note> uniqueNotesToUse = new ArrayList(this.song.getRequirements());
      Map<class_2766, List<class_2338>> incorrectNoteBlocks = new HashMap();
      Iterator var3 = this.scannedNoteblocks.asMap().entrySet().iterator();

      Entry entry;
      while(var3.hasNext()) {
         entry = (Entry)var3.next();
         Note note = (Note)entry.getKey();
         List<class_2338> noteblocks = new ArrayList((Collection)entry.getValue());
         if (uniqueNotesToUse.contains(note)) {
            this.noteBlockPositions.put(note, (class_2338)noteblocks.removeFirst());
            uniqueNotesToUse.remove(note);
         }

         if (!noteblocks.isEmpty()) {
            if (!incorrectNoteBlocks.containsKey(note.getInstrument())) {
               incorrectNoteBlocks.put(note.getInstrument(), new ArrayList());
            }

            ((List)incorrectNoteBlocks.get(note.getInstrument())).addAll(noteblocks);
         }
      }

      var3 = incorrectNoteBlocks.entrySet().iterator();

      while(true) {
         List positions;
         List foundNotes;
         label64:
         do {
            while(var3.hasNext()) {
               entry = (Entry)var3.next();
               positions = (List)entry.getValue();
               if (this.mode.get() == NotebotUtils.NotebotMode.ExactInstruments) {
                  class_2766 inst = (class_2766)entry.getKey();
                  foundNotes = (List)uniqueNotesToUse.stream().filter((notex) -> {
                     return notex.getInstrument() == inst;
                  }).collect(Collectors.toList());
                  continue label64;
               }

               Iterator var13 = positions.iterator();

               while(var13.hasNext()) {
                  class_2338 pos = (class_2338)var13.next();
                  if (uniqueNotesToUse.isEmpty()) {
                     break;
                  }

                  Note note = (Note)uniqueNotesToUse.removeFirst();
                  this.noteBlockPositions.put(note, pos);
               }
            }

            if (!uniqueNotesToUse.isEmpty()) {
               var3 = uniqueNotesToUse.iterator();

               while(var3.hasNext()) {
                  Note note = (Note)var3.next();
                  this.warning("Missing note: " + String.valueOf(note.getInstrument()) + ", " + note.getNoteLevel(), new Object[0]);
               }

               this.warning(uniqueNotesToUse.size() + " missing notes!", new Object[0]);
            }

            return;
         } while(foundNotes.isEmpty());

         Iterator var16 = positions.iterator();

         while(var16.hasNext()) {
            class_2338 pos = (class_2338)var16.next();
            if (foundNotes.isEmpty()) {
               break;
            }

            Note note = (Note)foundNotes.removeFirst();
            this.noteBlockPositions.put(note, pos);
            uniqueNotesToUse.remove(note);
         }
      }
   }

   private void setupTuneHitsMap() {
      this.tuneHits.clear();
      Iterator var1 = this.noteBlockPositions.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<Note, class_2338> entry = (Entry)var1.next();
         int targetLevel = ((Note)entry.getKey()).getNoteLevel();
         class_2338 blockPos = (class_2338)entry.getValue();
         class_2680 blockState = this.mc.field_1687.method_8320(blockPos);
         int currentLevel = (Integer)blockState.method_11654(class_2428.field_11324);
         if (targetLevel != currentLevel) {
            this.tuneHits.put(blockPos, calcNumberOfHits(currentLevel, targetLevel));
         }
      }

   }

   public WWidget getWidget(GuiTheme theme) {
      WTable table = theme.table();
      WButton openSongGUI = (WButton)table.add(theme.button("Open Song GUI")).expandX().minWidth(100.0D).widget();
      openSongGUI.action = () -> {
         this.mc.method_1507(theme.notebotSongs());
      };
      table.row();
      WButton alignCenter = (WButton)table.add(theme.button("Align Center")).expandX().minWidth(100.0D).widget();
      alignCenter.action = () -> {
         if (this.mc.field_1724 != null) {
            class_243 pos = class_243.method_24955(this.mc.field_1724.method_24515());
            this.mc.field_1724.method_5814(pos.field_1352, this.mc.field_1724.method_23318(), pos.field_1350);
         }
      };
      table.row();
      this.status = (WLabel)table.add(theme.label(this.getStatus())).expandCellX().widget();
      WButton pause = (WButton)table.add(theme.button(this.isPlaying ? "Pause" : "Resume")).right().widget();
      pause.action = () -> {
         this.pause();
         pause.set(this.isPlaying ? "Pause" : "Resume");
         this.updateStatus();
      };
      WButton stop = (WButton)table.add(theme.button("Stop")).right().widget();
      stop.action = this::stop;
      return table;
   }

   public String getStatus() {
      if (!this.isActive()) {
         return "Module disabled.";
      } else if (this.song == null) {
         return "No song loaded.";
      } else if (this.isPlaying) {
         return String.format("Playing song. %d/%d", this.currentTick, this.song.getLastTick());
      } else if (this.stage == Notebot.Stage.Playing) {
         return "Ready to play.";
      } else {
         return this.stage != Notebot.Stage.SetUp && this.stage != Notebot.Stage.Tune && this.stage != Notebot.Stage.WaitingToCheckNoteblocks ? String.format("Stage: %s.", this.stage.toString()) : "Setting up the noteblocks.";
      }
   }

   public void play() {
      if (this.mc.field_1724 != null) {
         if (this.mc.field_1724.method_31549().field_7477 && this.playingMode != Notebot.PlayingMode.Preview) {
            this.error("You need to be in survival mode.", new Object[0]);
         } else if (this.stage == Notebot.Stage.Playing) {
            this.isPlaying = true;
            this.info("Playing.", new Object[0]);
         } else {
            this.error("No song loaded.", new Object[0]);
         }

      }
   }

   public void pause() {
      if (!this.isActive()) {
         this.toggle();
      }

      if (this.isPlaying) {
         this.info("Pausing.", new Object[0]);
         this.isPlaying = false;
      } else {
         this.info("Resuming.", new Object[0]);
         this.isPlaying = true;
      }

   }

   public void stop() {
      this.info("Stopping.", new Object[0]);
      this.disable();
      this.updateStatus();
   }

   public void onSongEnd() {
      if ((Boolean)this.autoPlay.get() && this.playingMode != Notebot.PlayingMode.Preview) {
         this.playRandomSong();
      } else {
         this.stop();
      }

   }

   public void playRandomSong() {
      File[] files = MeteorClient.FOLDER.toPath().resolve("notebot").toFile().listFiles();
      if (files != null) {
         File randomSong = files[ThreadLocalRandom.current().nextInt(files.length)];
         if (SongDecoders.hasDecoder(randomSong)) {
            this.loadSong(randomSong);
         } else {
            this.playRandomSong();
         }

      }
   }

   public void disable() {
      this.resetVariables();
      if (!this.isActive()) {
         this.toggle();
      }

   }

   public void loadSong(File file) {
      if (!this.isActive()) {
         this.toggle();
      }

      this.resetVariables();
      this.playingMode = Notebot.PlayingMode.Noteblocks;
      if (!this.loadFileToMap(file, () -> {
         this.stage = Notebot.Stage.SetUp;
      })) {
         this.onSongEnd();
      } else {
         this.updateStatus();
      }
   }

   public void previewSong(File file) {
      if (!this.isActive()) {
         this.toggle();
      }

      this.resetVariables();
      this.playingMode = Notebot.PlayingMode.Preview;
      this.loadFileToMap(file, () -> {
         this.stage = Notebot.Stage.Playing;
         this.play();
      });
      this.updateStatus();
   }

   public boolean loadFileToMap(File file, Runnable callback) {
      if (file.exists() && file.isFile()) {
         if (!SongDecoders.hasDecoder(file)) {
            this.error("File is in wrong format. Decoder not found.", new Object[0]);
            return false;
         } else {
            this.info("Loading song \"%s\".", new Object[]{FilenameUtils.getBaseName(file.getName())});
            this.loadingSongFuture = CompletableFuture.supplyAsync(() -> {
               try {
                  return SongDecoders.parse(file);
               } catch (Exception var2) {
                  throw new RuntimeException(var2);
               }
            });
            this.loadingSongFuture.completeOnTimeout((Object)null, 60L, TimeUnit.SECONDS);
            this.stage = Notebot.Stage.LoadingSong;
            long time1 = System.currentTimeMillis();
            this.loadingSongFuture.whenComplete((song, ex) -> {
               if (ex == null) {
                  if (song == null) {
                     this.error("Loading song '" + FilenameUtils.getBaseName(file.getName()) + "' timed out.", new Object[0]);
                     this.onSongEnd();
                     return;
                  }

                  this.song = song;
                  long time2 = System.currentTimeMillis();
                  long diff = time2 - time1;
                  this.info("Song '" + FilenameUtils.getBaseName(file.getName()) + "' has been loaded to the memory! Took " + diff + "ms", new Object[0]);
                  callback.run();
               } else if (ex instanceof CancellationException) {
                  this.error("Loading song '" + FilenameUtils.getBaseName(file.getName()) + "' was cancelled.", new Object[0]);
               } else {
                  this.error("An error occurred while loading song '" + FilenameUtils.getBaseName(file.getName()) + "'. See the logs for more details", new Object[0]);
                  MeteorClient.LOG.error("An error occurred while loading song '" + FilenameUtils.getBaseName(file.getName()) + "'", ex);
                  this.onSongEnd();
               }

            });
            return true;
         }
      } else {
         this.error("File not found", new Object[0]);
         return false;
      }
   }

   private void scanForNoteblocks() {
      if (this.mc.field_1761 != null && this.mc.field_1687 != null && this.mc.field_1724 != null) {
         this.scannedNoteblocks.clear();
         int min = (int)(-this.mc.field_1724.method_55754()) - 2;
         int max = (int)this.mc.field_1724.method_55754() + 2;

         for(int y = min; y < max; ++y) {
            for(int x = min; x < max; ++x) {
               for(int z = min; z < max; ++z) {
                  class_2338 pos = this.mc.field_1724.method_24515().method_10069(x, y + 1, z);
                  class_2680 blockState = this.mc.field_1687.method_8320(pos);
                  if (blockState.method_26204() == class_2246.field_10179 && this.mc.field_1724.method_56093(pos, 1.0D) && this.isValidScanSpot(pos)) {
                     Note note = NotebotUtils.getNoteFromNoteBlock(blockState, pos, (NotebotUtils.NotebotMode)this.mode.get(), ((InstrumentDetectMode)this.instrumentDetectMode.get()).getInstrumentDetectFunction());
                     this.scannedNoteblocks.put(note, pos);
                  }
               }
            }
         }

      }
   }

   private void onTickPreview() {
      Iterator var1 = this.song.getNotesMap().get(this.currentTick).iterator();

      while(var1.hasNext()) {
         Note note = (Note)var1.next();
         if (this.mode.get() == NotebotUtils.NotebotMode.ExactInstruments) {
            this.mc.field_1724.method_5783((class_3414)note.getInstrument().method_11886().comp_349(), 2.0F, (float)Math.pow(2.0D, (double)(note.getNoteLevel() - 12) / 12.0D));
         } else {
            this.mc.field_1724.method_5783((class_3414)class_3417.field_15114.comp_349(), 2.0F, (float)Math.pow(2.0D, (double)(note.getNoteLevel() - 12) / 12.0D));
         }
      }

   }

   private void tune() {
      if (this.tuneHits.isEmpty()) {
         if (this.anyNoteblockTuned) {
            this.anyNoteblockTuned = false;
            this.waitTicks = (Integer)this.checkNoteblocksAgainDelay.get();
            this.stage = Notebot.Stage.WaitingToCheckNoteblocks;
            this.info("Delaying check for noteblocks", new Object[0]);
         } else {
            this.stage = Notebot.Stage.Playing;
            this.info("Loading done.", new Object[0]);
            this.play();
         }

      } else if (this.ticks >= (Integer)this.tickDelay.get()) {
         this.tuneBlocks();
         this.ticks = 0;
      }
   }

   private void tuneBlocks() {
      if (this.mc.field_1687 == null || this.mc.field_1724 == null) {
         this.disable();
      }

      if ((Boolean)this.swingArm.get()) {
         this.mc.field_1724.method_6104(class_1268.field_5808);
      }

      int iterations = 0;
      Iterator iterator = this.tuneHits.entrySet().iterator();

      do {
         if (!iterator.hasNext()) {
            return;
         }

         Entry<class_2338, Integer> entry = (Entry)iterator.next();
         class_2338 pos = (class_2338)entry.getKey();
         int hitsNumber = (Integer)entry.getValue();
         if ((Boolean)this.autoRotate.get()) {
            Rotations.rotate(Rotations.getYaw(pos), Rotations.getPitch(pos), 100, () -> {
               this.tuneNoteblockWithPackets(pos);
            });
         } else {
            this.tuneNoteblockWithPackets(pos);
         }

         this.clickedBlocks.add(pos);
         --hitsNumber;
         entry.setValue(hitsNumber);
         if (hitsNumber == 0) {
            iterator.remove();
         }

         ++iterations;
      } while(iterations != (Integer)this.concurrentTuneBlocks.get());

   }

   private void tuneNoteblockWithPackets(class_2338 pos) {
      this.mc.field_1724.field_3944.method_52787(new class_2885(class_1268.field_5808, new class_3965(class_243.method_24953(pos), class_2350.field_11033, pos, false), 0));
      this.anyNoteblockTuned = true;
   }

   public void updateStatus() {
      if (this.status != null) {
         this.status.set(this.getStatus());
      }

   }

   private static int calcNumberOfHits(int from, int to) {
      return from > to ? 25 - from + to : to - from;
   }

   private void onTickPlay() {
      Collection<Note> notes = this.song.getNotesMap().get(this.currentTick);
      if (!notes.isEmpty()) {
         if ((Boolean)this.autoRotate.get()) {
            Optional<Note> firstNote = notes.stream().findFirst();
            if (firstNote.isPresent()) {
               class_2338 firstPos = (class_2338)this.noteBlockPositions.get(firstNote.get());
               if (firstPos != null) {
                  Rotations.rotate(Rotations.getYaw(firstPos), Rotations.getPitch(firstPos));
               }
            }
         }

         if ((Boolean)this.swingArm.get()) {
            this.mc.field_1724.method_6104(class_1268.field_5808);
         }

         Iterator var5 = notes.iterator();

         while(var5.hasNext()) {
            Note note = (Note)var5.next();
            class_2338 pos = (class_2338)this.noteBlockPositions.get(note);
            if (pos == null) {
               return;
            }

            if ((Boolean)this.polyphonic.get()) {
               this.playRotate(pos);
            } else {
               this.playRotate(pos);
            }
         }
      }

   }

   private void playRotate(class_2338 pos) {
      if (this.mc.field_1761 != null) {
         try {
            this.mc.field_1724.field_3944.method_52787(new class_2846(class_2847.field_12968, pos, class_2350.field_11033, 0));
         } catch (NullPointerException var3) {
         }

      }
   }

   private boolean isValidScanSpot(class_2338 pos) {
      return this.mc.field_1687.method_8320(pos).method_26204() != class_2246.field_10179 ? false : this.mc.field_1687.method_8320(pos.method_10084()).method_26215();
   }

   @Nullable
   public class_2766 getMappedInstrument(@NotNull class_2766 inst) {
      if (this.mode.get() == NotebotUtils.NotebotMode.ExactInstruments) {
         NotebotUtils.OptionalInstrument optionalInstrument = (NotebotUtils.OptionalInstrument)this.sgNoteMap.getByIndex(inst.ordinal()).get();
         return optionalInstrument.toMinecraftInstrument();
      } else {
         return inst;
      }
   }

   private String beautifyText(String text) {
      text = text.toLowerCase(Locale.ROOT);
      String[] arr = text.split("_");
      StringBuilder sb = new StringBuilder();
      String[] var4 = arr;
      int var5 = arr.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         String s = var4[var6];
         sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1));
      }

      return sb.toString().trim();
   }

   public static enum Stage {
      None,
      LoadingSong,
      SetUp,
      Tune,
      WaitingToCheckNoteblocks,
      Playing;

      // $FF: synthetic method
      private static Notebot.Stage[] $values() {
         return new Notebot.Stage[]{None, LoadingSong, SetUp, Tune, WaitingToCheckNoteblocks, Playing};
      }
   }

   public static enum PlayingMode {
      None,
      Preview,
      Noteblocks;

      // $FF: synthetic method
      private static Notebot.PlayingMode[] $values() {
         return new Notebot.PlayingMode[]{None, Preview, Noteblocks};
      }
   }
}
