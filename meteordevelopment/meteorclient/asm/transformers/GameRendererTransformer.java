package meteordevelopment.meteorclient.asm.transformers;

import java.util.ListIterator;
import meteordevelopment.meteorclient.asm.AsmTransformer;
import meteordevelopment.meteorclient.asm.Descriptor;
import meteordevelopment.meteorclient.asm.MethodInfo;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class GameRendererTransformer extends AsmTransformer {
   private final MethodInfo getFovMethod = new MethodInfo("net/minecraft/class_4184", (String)null, new Descriptor(new String[]{"Lnet/minecraft/class_4184;", "F", "Z", "D"}), true);

   public GameRendererTransformer() {
      super(mapClassName("net/minecraft/class_757"));
   }

   public void transform(ClassNode klass) {
      MethodNode method = this.getMethod(klass, this.getFovMethod);
      if (method == null) {
         error("[Meteor Client] Could not find method GameRenderer.getFov()");
      }

      int injectionCount = 0;
      ListIterator var4 = method.instructions.iterator();

      while(true) {
         while(var4.hasNext()) {
            AbstractInsnNode insn = (AbstractInsnNode)var4.next();
            InsnList insns;
            if (insn instanceof LdcInsnNode) {
               LdcInsnNode in = (LdcInsnNode)insn;
               if (in.cst instanceof Double && (Double)in.cst == 90.0D) {
                  insns = new InsnList();
                  this.generateEventCall(insns, new LdcInsnNode(in.cst));
                  method.instructions.insert(insn, insns);
                  method.instructions.remove(insn);
                  ++injectionCount;
                  continue;
               }
            }

            label56: {
               if (insn instanceof MethodInsnNode) {
                  MethodInsnNode in1 = (MethodInsnNode)insn;
                  if (in1.name.equals("intValue")) {
                     AbstractInsnNode var10 = insn.getNext();
                     if (var10 instanceof InsnNode) {
                        InsnNode _in = (InsnNode)var10;
                        if (_in.getOpcode() == 135) {
                           break label56;
                        }
                     }
                  }
               }

               if (!(insn instanceof MethodInsnNode)) {
                  continue;
               }

               MethodInsnNode in2 = (MethodInsnNode)insn;
               if (!in2.owner.equals(klass.name) || !in2.name.startsWith("redirect") || !in2.name.endsWith("getFov")) {
                  continue;
               }
            }

            insns = new InsnList();
            insns.add(new VarInsnNode(57, method.maxLocals));
            this.generateEventCall(insns, new VarInsnNode(24, method.maxLocals));
            method.instructions.insert(insn.getNext(), insns);
            ++injectionCount;
         }

         if (injectionCount < 2) {
            error("[Meteor Client] Failed to modify GameRenderer.getFov()");
         }

         return;
      }
   }

   private void generateEventCall(InsnList insns, AbstractInsnNode loadPreviousFov) {
      insns.add(new FieldInsnNode(178, "meteordevelopment/meteorclient/MeteorClient", "EVENT_BUS", "Lmeteordevelopment/orbit/IEventBus;"));
      insns.add(loadPreviousFov);
      insns.add(new MethodInsnNode(184, "meteordevelopment/meteorclient/events/render/GetFovEvent", "get", "(D)Lmeteordevelopment/meteorclient/events/render/GetFovEvent;"));
      insns.add(new MethodInsnNode(185, "meteordevelopment/orbit/IEventBus", "post", "(Ljava/lang/Object;)Ljava/lang/Object;"));
      insns.add(new TypeInsnNode(192, "meteordevelopment/meteorclient/events/render/GetFovEvent"));
      insns.add(new FieldInsnNode(180, "meteordevelopment/meteorclient/events/render/GetFovEvent", "fov", "D"));
   }
}
