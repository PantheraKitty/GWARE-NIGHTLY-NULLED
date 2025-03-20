package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_634;
import net.minecraft.class_7637;
import net.minecraft.class_7699;
import net.minecraft.class_5455.class_6890;
import net.minecraft.class_7610.class_7612;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_634.class})
public interface ClientPlayNetworkHandlerAccessor {
   @Accessor("chunkLoadDistance")
   int getChunkLoadDistance();

   @Accessor("messagePacker")
   class_7612 getMessagePacker();

   @Accessor("lastSeenMessagesCollector")
   class_7637 getLastSeenMessagesCollector();

   @Accessor("combinedDynamicRegistries")
   class_6890 getCombinedDynamicRegistries();

   @Accessor("enabledFeatures")
   class_7699 getEnabledFeatures();
}
