package meteordevelopment.meteorclient.asm;

import java.util.Iterator;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public abstract class AsmTransformer {
   public final String targetName;

   protected AsmTransformer(String targetName) {
      this.targetName = targetName;
   }

   public abstract void transform(ClassNode var1);

   protected MethodNode getMethod(ClassNode klass, MethodInfo methodInfo) {
      Iterator var3 = klass.methods.iterator();

      MethodNode method;
      do {
         if (!var3.hasNext()) {
            return null;
         }

         method = (MethodNode)var3.next();
      } while(!methodInfo.equals(method));

      return method;
   }

   protected static void error(String message) {
      System.err.println(message);
      throw new RuntimeException(message);
   }

   protected static String mapClassName(String name) {
      return FabricLoader.getInstance().getMappingResolver().mapClassName("intermediary", name.replace('/', '.'));
   }
}
