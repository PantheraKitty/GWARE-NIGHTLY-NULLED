package meteordevelopment.meteorclient.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.IGetter;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import net.minecraft.class_2248;
import net.minecraft.class_2487;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public class BlockDataSetting<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> extends Setting<Map<class_2248, T>> {
   public final IGetter<T> defaultData;

   public BlockDataSetting(String name, String description, Map<class_2248, T> defaultValue, Consumer<Map<class_2248, T>> onChanged, Consumer<Setting<Map<class_2248, T>>> onModuleActivated, IGetter<T> defaultData, IVisible visible) {
      super(name, description, defaultValue, onChanged, onModuleActivated, visible);
      this.defaultData = defaultData;
   }

   public void resetImpl() {
      this.value = new HashMap((Map)this.defaultValue);
   }

   protected Map<class_2248, T> parseImpl(String str) {
      return new HashMap(0);
   }

   protected boolean isValueValid(Map<class_2248, T> value) {
      return true;
   }

   protected class_2487 save(class_2487 tag) {
      class_2487 valueTag = new class_2487();
      Iterator var3 = ((Map)this.get()).keySet().iterator();

      while(var3.hasNext()) {
         class_2248 block = (class_2248)var3.next();
         valueTag.method_10566(class_7923.field_41175.method_10221(block).toString(), ((ISerializable)((ICopyable)((Map)this.get()).get(block))).toTag());
      }

      tag.method_10566("value", valueTag);
      return tag;
   }

   protected Map<class_2248, T> load(class_2487 tag) {
      ((Map)this.get()).clear();
      class_2487 valueTag = tag.method_10562("value");
      Iterator var3 = valueTag.method_10541().iterator();

      while(var3.hasNext()) {
         String key = (String)var3.next();
         ((Map)this.get()).put((class_2248)class_7923.field_41175.method_10223(class_2960.method_60654(key)), (ICopyable)((ISerializable)((ICopyable)this.defaultData.get()).copy()).fromTag(valueTag.method_10562(key)));
      }

      return (Map)this.get();
   }

   public static class Builder<T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> extends Setting.SettingBuilder<BlockDataSetting.Builder<T>, Map<class_2248, T>, BlockDataSetting<T>> {
      private IGetter<T> defaultData;

      public Builder() {
         super(new HashMap(0));
      }

      public BlockDataSetting.Builder<T> defaultData(IGetter<T> defaultData) {
         this.defaultData = defaultData;
         return this;
      }

      public BlockDataSetting<T> build() {
         return new BlockDataSetting(this.name, this.description, (Map)this.defaultValue, this.onChanged, this.onModuleActivated, this.defaultData, this.visible);
      }
   }
}
