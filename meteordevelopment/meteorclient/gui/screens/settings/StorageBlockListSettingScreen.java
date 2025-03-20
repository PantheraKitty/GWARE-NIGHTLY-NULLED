package meteordevelopment.meteorclient.gui.screens.settings;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.StorageBlockListSetting;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import net.minecraft.class_2591;

public class StorageBlockListSettingScreen extends RegistryListSettingScreen<class_2591<?>> {
   private static final Map<class_2591<?>, StorageBlockListSettingScreen.BlockEntityTypeInfo> BLOCK_ENTITY_TYPE_INFO_MAP = new Object2ObjectOpenHashMap();
   private static final StorageBlockListSettingScreen.BlockEntityTypeInfo UNKNOWN;

   public StorageBlockListSettingScreen(GuiTheme theme, Setting<List<class_2591<?>>> setting) {
      super(theme, "Select Storage Blocks", setting, (Collection)setting.get(), StorageBlockListSetting.REGISTRY);
   }

   protected WWidget getValueWidget(class_2591<?> value) {
      class_1792 item = ((StorageBlockListSettingScreen.BlockEntityTypeInfo)BLOCK_ENTITY_TYPE_INFO_MAP.getOrDefault(value, UNKNOWN)).item();
      return this.theme.itemWithLabel(item.method_7854(), this.getValueName(value));
   }

   protected String getValueName(class_2591<?> value) {
      return ((StorageBlockListSettingScreen.BlockEntityTypeInfo)BLOCK_ENTITY_TYPE_INFO_MAP.getOrDefault(value, UNKNOWN)).name();
   }

   static {
      UNKNOWN = new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8077, "Unknown");
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_16411, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_16307, "Barrel"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_16415, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_16306, "Blast Furnace"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_11894, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8740, "Brewing Stand"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_17380, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_17346, "Campfire"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_11914, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8106, "Chest"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_40329, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_40215, "Chiseled Bookshelf"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_46808, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_46791, "Crafter"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_11887, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8357, "Dispenser"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_42781, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_42699, "Decorated Pot"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_11899, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8878, "Dropper"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_11901, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8466, "Ender Chest"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_11903, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8732, "Furnace"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_11888, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8239, "Hopper"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_11896, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8545, "Shulker Box"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_16414, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_16309, "Smoker"));
      BLOCK_ENTITY_TYPE_INFO_MAP.put(class_2591.field_11891, new StorageBlockListSettingScreen.BlockEntityTypeInfo(class_1802.field_8247, "Trapped Chest"));
   }

   private static record BlockEntityTypeInfo(class_1792 item, String name) {
      private BlockEntityTypeInfo(class_1792 item, String name) {
         this.item = item;
         this.name = name;
      }

      public class_1792 item() {
         return this.item;
      }

      public String name() {
         return this.name;
      }
   }
}
