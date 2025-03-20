package javassist.bytecode.annotation;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.ConstPool;
import javassist.bytecode.Descriptor;

public class Annotation {
   ConstPool pool;
   int typeIndex;
   Map<String, Annotation.Pair> members;

   public Annotation(int type, ConstPool cp) {
      this.pool = cp;
      this.typeIndex = type;
      this.members = null;
   }

   public Annotation(String typeName, ConstPool cp) {
      this(cp.addUtf8Info(Descriptor.of(typeName)), cp);
   }

   public Annotation(ConstPool cp, CtClass clazz) throws NotFoundException {
      this(cp.addUtf8Info(Descriptor.of(clazz.getName())), cp);
      if (!clazz.isInterface()) {
         throw new RuntimeException("Only interfaces are allowed for Annotation creation.");
      } else {
         CtMethod[] methods = clazz.getDeclaredMethods();
         if (methods.length > 0) {
            this.members = new LinkedHashMap();
         }

         CtMethod[] var4 = methods;
         int var5 = methods.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            CtMethod m = var4[var6];
            this.addMemberValue(m.getName(), createMemberValue(cp, m.getReturnType()));
         }

      }
   }

   public static MemberValue createMemberValue(ConstPool cp, CtClass type) throws NotFoundException {
      if (type == CtClass.booleanType) {
         return new BooleanMemberValue(cp);
      } else if (type == CtClass.byteType) {
         return new ByteMemberValue(cp);
      } else if (type == CtClass.charType) {
         return new CharMemberValue(cp);
      } else if (type == CtClass.shortType) {
         return new ShortMemberValue(cp);
      } else if (type == CtClass.intType) {
         return new IntegerMemberValue(cp);
      } else if (type == CtClass.longType) {
         return new LongMemberValue(cp);
      } else if (type == CtClass.floatType) {
         return new FloatMemberValue(cp);
      } else if (type == CtClass.doubleType) {
         return new DoubleMemberValue(cp);
      } else if (type.getName().equals("java.lang.Class")) {
         return new ClassMemberValue(cp);
      } else if (type.getName().equals("java.lang.String")) {
         return new StringMemberValue(cp);
      } else if (type.isArray()) {
         CtClass arrayType = type.getComponentType();
         MemberValue member = createMemberValue(cp, arrayType);
         return new ArrayMemberValue(member, cp);
      } else if (type.isInterface()) {
         Annotation info = new Annotation(cp, type);
         return new AnnotationMemberValue(info, cp);
      } else {
         EnumMemberValue emv = new EnumMemberValue(cp);
         emv.setType(type.getName());
         return emv;
      }
   }

   public void addMemberValue(int nameIndex, MemberValue value) {
      Annotation.Pair p = new Annotation.Pair();
      p.name = nameIndex;
      p.value = value;
      this.addMemberValue(p);
   }

   public void addMemberValue(String name, MemberValue value) {
      Annotation.Pair p = new Annotation.Pair();
      p.name = this.pool.addUtf8Info(name);
      p.value = value;
      if (this.members == null) {
         this.members = new LinkedHashMap();
      }

      this.members.put(name, p);
   }

   private void addMemberValue(Annotation.Pair pair) {
      String name = this.pool.getUtf8Info(pair.name);
      if (this.members == null) {
         this.members = new LinkedHashMap();
      }

      this.members.put(name, pair);
   }

   public String toString() {
      StringBuffer buf = new StringBuffer("@");
      buf.append(this.getTypeName());
      if (this.members != null) {
         buf.append("(");
         Iterator var2 = this.members.keySet().iterator();

         while(var2.hasNext()) {
            String name = (String)var2.next();
            buf.append(name).append("=").append(this.getMemberValue(name)).append(", ");
         }

         buf.setLength(buf.length() - 2);
         buf.append(")");
      }

      return buf.toString();
   }

   public String getTypeName() {
      return Descriptor.toClassName(this.pool.getUtf8Info(this.typeIndex));
   }

   public Set<String> getMemberNames() {
      return this.members == null ? null : this.members.keySet();
   }

   public MemberValue getMemberValue(String name) {
      return this.members != null && this.members.get(name) != null ? ((Annotation.Pair)this.members.get(name)).value : null;
   }

   public Object toAnnotationType(ClassLoader cl, ClassPool cp) throws ClassNotFoundException, NoSuchClassError {
      Class clazz = MemberValue.loadClass(cl, this.getTypeName());

      try {
         return AnnotationImpl.make(cl, clazz, cp, this);
      } catch (IllegalArgumentException var5) {
         throw new ClassNotFoundException(clazz.getName(), var5);
      } catch (IllegalAccessError var6) {
         throw new ClassNotFoundException(clazz.getName(), var6);
      }
   }

   public void write(AnnotationsWriter writer) throws IOException {
      String typeName = this.pool.getUtf8Info(this.typeIndex);
      if (this.members == null) {
         writer.annotation(typeName, 0);
      } else {
         writer.annotation(typeName, this.members.size());
         Iterator var3 = this.members.values().iterator();

         while(var3.hasNext()) {
            Annotation.Pair pair = (Annotation.Pair)var3.next();
            writer.memberValuePair(pair.name);
            pair.value.write(writer);
         }

      }
   }

   public int hashCode() {
      return this.getTypeName().hashCode() + (this.members == null ? 0 : this.members.hashCode());
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (obj != null && obj instanceof Annotation) {
         Annotation other = (Annotation)obj;
         if (!this.getTypeName().equals(other.getTypeName())) {
            return false;
         } else {
            Map<String, Annotation.Pair> otherMembers = other.members;
            if (this.members == otherMembers) {
               return true;
            } else if (this.members == null) {
               return otherMembers == null;
            } else {
               return otherMembers == null ? false : this.members.equals(otherMembers);
            }
         }
      } else {
         return false;
      }
   }

   static class Pair {
      int name;
      MemberValue value;
   }
}
