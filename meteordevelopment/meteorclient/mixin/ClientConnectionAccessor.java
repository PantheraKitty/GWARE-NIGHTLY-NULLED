package meteordevelopment.meteorclient.mixin;

import io.netty.channel.Channel;
import net.minecraft.class_2535;
import net.minecraft.class_2596;
import net.minecraft.class_7648;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({class_2535.class})
public interface ClientConnectionAccessor {
   @Accessor("channel")
   Channel getChannel();

   @Invoker("send")
   void invokeSend(class_2596<?> var1, @Nullable class_7648 var2);
}
