package javassist.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javassist.CannotCompileException;

public class StackMap extends AttributeInfo {
   public static final String tag = "StackMap";
   public static final int TOP = 0;
   public static final int INTEGER = 1;
   public static final int FLOAT = 2;
   public static final int DOUBLE = 3;
   public static final int LONG = 4;
   public static final int NULL = 5;
   public static final int THIS = 6;
   public static final int OBJECT = 7;
   public static final int UNINIT = 8;

   StackMap(ConstPool cp, byte[] newInfo) {
      super(cp, "StackMap", newInfo);
   }

   StackMap(ConstPool cp, int name_id, DataInputStream in) throws IOException {
      super(cp, name_id, in);
   }

   public int numOfEntries() {
      return ByteArray.readU16bit(this.info, 0);
   }

   public AttributeInfo copy(ConstPool newCp, Map<String, String> classnames) {
      StackMap.Copier copier = new StackMap.Copier(this, newCp, classnames);
      copier.visit();
      return copier.getStackMap();
   }

   public void insertLocal(int index, int tag, int classInfo) throws BadBytecode {
      byte[] data = (new StackMap.InsertLocal(this, index, tag, classInfo)).doit();
      this.set(data);
   }

   void shiftPc(int where, int gapSize, boolean exclusive) throws BadBytecode {
      (new StackMap.Shifter(this, where, gapSize, exclusive)).visit();
   }

   void shiftForSwitch(int where, int gapSize) throws BadBytecode {
      (new StackMap.SwitchShifter(this, where, gapSize)).visit();
   }

   public void removeNew(int where) throws CannotCompileException {
      byte[] data = (new StackMap.NewRemover(this, where)).doit();
      this.set(data);
   }

   public void print(PrintWriter out) {
      (new StackMap.Printer(this, out)).print();
   }

   public static class Writer {
      private ByteArrayOutputStream output = new ByteArrayOutputStream();

      public byte[] toByteArray() {
         return this.output.toByteArray();
      }

      public StackMap toStackMap(ConstPool cp) {
         return new StackMap(cp, this.output.toByteArray());
      }

      public void writeVerifyTypeInfo(int tag, int data) {
         this.output.write(tag);
         if (tag == 7 || tag == 8) {
            this.write16bit(data);
         }

      }

      public void write16bit(int value) {
         this.output.write(value >>> 8 & 255);
         this.output.write(value & 255);
      }
   }

   static class Printer extends StackMap.Walker {
      private PrintWriter writer;

      public Printer(StackMap map, PrintWriter out) {
         super(map);
         this.writer = out;
      }

      public void print() {
         int num = ByteArray.readU16bit(this.info, 0);
         this.writer.println(num + " entries");
         this.visit();
      }

      public int locals(int pos, int offset, int num) {
         this.writer.println("  * offset " + offset);
         return super.locals(pos, offset, num);
      }
   }

   static class NewRemover extends StackMap.SimpleCopy {
      int posOfNew;

      NewRemover(StackMap map, int where) {
         super(map);
         this.posOfNew = where;
      }

      public int stack(int pos, int offset, int num) {
         return this.stackTypeInfoArray(pos, offset, num);
      }

      private int stackTypeInfoArray(int pos, int offset, int num) {
         int p = pos;
         int count = 0;

         int k;
         byte tag;
         int offsetOfNew;
         for(k = 0; k < num; ++k) {
            tag = this.info[p];
            if (tag == 7) {
               p += 3;
            } else if (tag == 8) {
               offsetOfNew = ByteArray.readU16bit(this.info, p + 1);
               if (offsetOfNew == this.posOfNew) {
                  ++count;
               }

               p += 3;
            } else {
               ++p;
            }
         }

         this.writer.write16bit(num - count);

         for(k = 0; k < num; ++k) {
            tag = this.info[pos];
            if (tag == 7) {
               offsetOfNew = ByteArray.readU16bit(this.info, pos + 1);
               this.objectVariable(pos, offsetOfNew);
               pos += 3;
            } else if (tag == 8) {
               offsetOfNew = ByteArray.readU16bit(this.info, pos + 1);
               if (offsetOfNew != this.posOfNew) {
                  this.uninitialized(pos, offsetOfNew);
               }

               pos += 3;
            } else {
               this.typeInfo(pos, tag);
               ++pos;
            }
         }

         return pos;
      }
   }

   static class SwitchShifter extends StackMap.Walker {
      private int where;
      private int gap;

      public SwitchShifter(StackMap smt, int where, int gap) {
         super(smt);
         this.where = where;
         this.gap = gap;
      }

      public int locals(int pos, int offset, int num) {
         if (this.where == pos + offset) {
            ByteArray.write16bit(offset - this.gap, this.info, pos - 4);
         } else if (this.where == pos) {
            ByteArray.write16bit(offset + this.gap, this.info, pos - 4);
         }

         return super.locals(pos, offset, num);
      }
   }

   static class Shifter extends StackMap.Walker {
      private int where;
      private int gap;
      private boolean exclusive;

      public Shifter(StackMap smt, int where, int gap, boolean exclusive) {
         super(smt);
         this.where = where;
         this.gap = gap;
         this.exclusive = exclusive;
      }

      public int locals(int pos, int offset, int num) {
         if (this.exclusive) {
            if (this.where > offset) {
               return super.locals(pos, offset, num);
            }
         } else if (this.where >= offset) {
            return super.locals(pos, offset, num);
         }

         ByteArray.write16bit(offset + this.gap, this.info, pos - 4);
         return super.locals(pos, offset, num);
      }

      public void uninitialized(int pos, int offset) {
         if (this.where <= offset) {
            ByteArray.write16bit(offset + this.gap, this.info, pos + 1);
         }

      }
   }

   static class InsertLocal extends StackMap.SimpleCopy {
      private int varIndex;
      private int varTag;
      private int varData;

      InsertLocal(StackMap map, int varIndex, int varTag, int varData) {
         super(map);
         this.varIndex = varIndex;
         this.varTag = varTag;
         this.varData = varData;
      }

      public int typeInfoArray(int pos, int offset, int num, boolean isLocals) {
         if (isLocals && num >= this.varIndex) {
            this.writer.write16bit(num + 1);

            for(int k = 0; k < num; ++k) {
               if (k == this.varIndex) {
                  this.writeVarTypeInfo();
               }

               pos = this.typeInfoArray2(k, pos);
            }

            if (num == this.varIndex) {
               this.writeVarTypeInfo();
            }

            return pos;
         } else {
            return super.typeInfoArray(pos, offset, num, isLocals);
         }
      }

      private void writeVarTypeInfo() {
         if (this.varTag == 7) {
            this.writer.writeVerifyTypeInfo(7, this.varData);
         } else if (this.varTag == 8) {
            this.writer.writeVerifyTypeInfo(8, this.varData);
         } else {
            this.writer.writeVerifyTypeInfo(this.varTag, 0);
         }

      }
   }

   static class SimpleCopy extends StackMap.Walker {
      StackMap.Writer writer = new StackMap.Writer();

      SimpleCopy(StackMap map) {
         super(map);
      }

      byte[] doit() {
         this.visit();
         return this.writer.toByteArray();
      }

      public void visit() {
         int num = ByteArray.readU16bit(this.info, 0);
         this.writer.write16bit(num);
         super.visit();
      }

      public int locals(int pos, int offset, int num) {
         this.writer.write16bit(offset);
         return super.locals(pos, offset, num);
      }

      public int typeInfoArray(int pos, int offset, int num, boolean isLocals) {
         this.writer.write16bit(num);
         return super.typeInfoArray(pos, offset, num, isLocals);
      }

      public void typeInfo(int pos, byte tag) {
         this.writer.writeVerifyTypeInfo(tag, 0);
      }

      public void objectVariable(int pos, int clazz) {
         this.writer.writeVerifyTypeInfo(7, clazz);
      }

      public void uninitialized(int pos, int offset) {
         this.writer.writeVerifyTypeInfo(8, offset);
      }
   }

   static class Copier extends StackMap.Walker {
      byte[] dest;
      ConstPool srcCp;
      ConstPool destCp;
      Map<String, String> classnames;

      Copier(StackMap map, ConstPool newCp, Map<String, String> classnames) {
         super(map);
         this.srcCp = map.getConstPool();
         this.dest = new byte[this.info.length];
         this.destCp = newCp;
         this.classnames = classnames;
      }

      public void visit() {
         int num = ByteArray.readU16bit(this.info, 0);
         ByteArray.write16bit(num, this.dest, 0);
         super.visit();
      }

      public int locals(int pos, int offset, int num) {
         ByteArray.write16bit(offset, this.dest, pos - 4);
         return super.locals(pos, offset, num);
      }

      public int typeInfoArray(int pos, int offset, int num, boolean isLocals) {
         ByteArray.write16bit(num, this.dest, pos - 2);
         return super.typeInfoArray(pos, offset, num, isLocals);
      }

      public void typeInfo(int pos, byte tag) {
         this.dest[pos] = tag;
      }

      public void objectVariable(int pos, int clazz) {
         this.dest[pos] = 7;
         int newClazz = this.srcCp.copy(clazz, this.destCp, this.classnames);
         ByteArray.write16bit(newClazz, this.dest, pos + 1);
      }

      public void uninitialized(int pos, int offset) {
         this.dest[pos] = 8;
         ByteArray.write16bit(offset, this.dest, pos + 1);
      }

      public StackMap getStackMap() {
         return new StackMap(this.destCp, this.dest);
      }
   }

   public static class Walker {
      byte[] info;

      public Walker(StackMap sm) {
         this.info = sm.get();
      }

      public void visit() {
         int num = ByteArray.readU16bit(this.info, 0);
         int pos = 2;

         for(int i = 0; i < num; ++i) {
            int offset = ByteArray.readU16bit(this.info, pos);
            int numLoc = ByteArray.readU16bit(this.info, pos + 2);
            pos = this.locals(pos + 4, offset, numLoc);
            int numStack = ByteArray.readU16bit(this.info, pos);
            pos = this.stack(pos + 2, offset, numStack);
         }

      }

      public int locals(int pos, int offset, int num) {
         return this.typeInfoArray(pos, offset, num, true);
      }

      public int stack(int pos, int offset, int num) {
         return this.typeInfoArray(pos, offset, num, false);
      }

      public int typeInfoArray(int pos, int offset, int num, boolean isLocals) {
         for(int k = 0; k < num; ++k) {
            pos = this.typeInfoArray2(k, pos);
         }

         return pos;
      }

      int typeInfoArray2(int k, int pos) {
         byte tag = this.info[pos];
         int offsetOfNew;
         if (tag == 7) {
            offsetOfNew = ByteArray.readU16bit(this.info, pos + 1);
            this.objectVariable(pos, offsetOfNew);
            pos += 3;
         } else if (tag == 8) {
            offsetOfNew = ByteArray.readU16bit(this.info, pos + 1);
            this.uninitialized(pos, offsetOfNew);
            pos += 3;
         } else {
            this.typeInfo(pos, tag);
            ++pos;
         }

         return pos;
      }

      public void typeInfo(int pos, byte tag) {
      }

      public void objectVariable(int pos, int clazz) {
      }

      public void uninitialized(int pos, int offset) {
      }
   }
}
