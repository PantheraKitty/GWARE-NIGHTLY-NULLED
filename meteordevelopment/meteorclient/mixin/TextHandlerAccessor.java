package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_5225;
import net.minecraft.class_5225.class_5231;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_5225.class})
public interface TextHandlerAccessor {
   @Accessor("widthRetriever")
   class_5231 getWidthRetriever();
}
