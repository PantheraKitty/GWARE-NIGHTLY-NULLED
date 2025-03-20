package meteordevelopment.meteorclient.utils.notebot;

import java.util.HashMap;
import java.util.Map;
import meteordevelopment.meteorclient.utils.notebot.instrumentdetect.InstrumentDetectFunction;
import meteordevelopment.meteorclient.utils.notebot.song.Note;
import net.minecraft.class_2338;
import net.minecraft.class_2428;
import net.minecraft.class_2680;
import net.minecraft.class_2766;
import org.jetbrains.annotations.Nullable;

public class NotebotUtils {
   public static Note getNoteFromNoteBlock(class_2680 noteBlock, class_2338 blockPos, NotebotUtils.NotebotMode mode, InstrumentDetectFunction instrumentDetectFunction) {
      class_2766 instrument = null;
      int level = (Integer)noteBlock.method_11654(class_2428.field_11324);
      if (mode == NotebotUtils.NotebotMode.ExactInstruments) {
         instrument = instrumentDetectFunction.detectInstrument(noteBlock, blockPos);
      }

      return new Note(instrument, level);
   }

   public static enum NotebotMode {
      AnyInstrument,
      ExactInstruments;

      // $FF: synthetic method
      private static NotebotUtils.NotebotMode[] $values() {
         return new NotebotUtils.NotebotMode[]{AnyInstrument, ExactInstruments};
      }
   }

   public static enum OptionalInstrument {
      None((class_2766)null),
      Harp(class_2766.field_12648),
      Basedrum(class_2766.field_12653),
      Snare(class_2766.field_12643),
      Hat(class_2766.field_12645),
      Bass(class_2766.field_12651),
      Flute(class_2766.field_12650),
      Bell(class_2766.field_12644),
      Guitar(class_2766.field_12654),
      Chime(class_2766.field_12647),
      Xylophone(class_2766.field_12655),
      IronXylophone(class_2766.field_18284),
      CowBell(class_2766.field_18285),
      Didgeridoo(class_2766.field_18286),
      Bit(class_2766.field_18287),
      Banjo(class_2766.field_18288),
      Pling(class_2766.field_18289);

      public static final Map<class_2766, NotebotUtils.OptionalInstrument> BY_MINECRAFT_INSTRUMENT = new HashMap();
      private final class_2766 minecraftInstrument;

      private OptionalInstrument(@Nullable class_2766 minecraftInstrument) {
         this.minecraftInstrument = minecraftInstrument;
      }

      public class_2766 toMinecraftInstrument() {
         return this.minecraftInstrument;
      }

      public static NotebotUtils.OptionalInstrument fromMinecraftInstrument(class_2766 instrument) {
         return instrument != null ? (NotebotUtils.OptionalInstrument)BY_MINECRAFT_INSTRUMENT.get(instrument) : null;
      }

      // $FF: synthetic method
      private static NotebotUtils.OptionalInstrument[] $values() {
         return new NotebotUtils.OptionalInstrument[]{None, Harp, Basedrum, Snare, Hat, Bass, Flute, Bell, Guitar, Chime, Xylophone, IronXylophone, CowBell, Didgeridoo, Bit, Banjo, Pling};
      }

      static {
         NotebotUtils.OptionalInstrument[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            NotebotUtils.OptionalInstrument optionalInstrument = var0[var2];
            BY_MINECRAFT_INSTRUMENT.put(optionalInstrument.minecraftInstrument, optionalInstrument);
         }

      }
   }
}
