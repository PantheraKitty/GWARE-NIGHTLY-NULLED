package meteordevelopment.meteorclient.mixin;

import java.util.List;
import net.minecraft.class_303;
import net.minecraft.class_338;
import net.minecraft.class_303.class_7590;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_338.class})
public interface ChatHudAccessor {
   @Accessor("visibleMessages")
   List<class_7590> getVisibleMessages();

   @Accessor("messages")
   List<class_303> getMessages();
}
