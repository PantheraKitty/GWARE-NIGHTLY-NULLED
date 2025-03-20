package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_6360.class_6363;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_6363.class})
public interface ReloadStateAccessor {
   @Accessor("finished")
   boolean isFinished();
}
