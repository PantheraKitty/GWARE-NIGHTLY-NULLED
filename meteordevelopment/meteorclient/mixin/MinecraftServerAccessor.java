package meteordevelopment.meteorclient.mixin;

import net.minecraft.class_32.class_5143;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({MinecraftServer.class})
public interface MinecraftServerAccessor {
   @Accessor("session")
   class_5143 getSession();
}
