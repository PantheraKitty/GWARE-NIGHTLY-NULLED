package meteordevelopment.meteorclient.gui.screens.settings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.BlockDataSetting;
import meteordevelopment.meteorclient.settings.IBlockData;
import meteordevelopment.meteorclient.utils.misc.IChangeable;
import meteordevelopment.meteorclient.utils.misc.ICopyable;
import meteordevelopment.meteorclient.utils.misc.ISerializable;
import meteordevelopment.meteorclient.utils.misc.Names;
import net.minecraft.class_2248;
import net.minecraft.class_7923;
import org.apache.commons.lang3.StringUtils;

public class BlockDataSettingScreen extends WindowScreen {
   private static final List<class_2248> BLOCKS = new ArrayList(100);
   private final BlockDataSetting<?> setting;
   private WTable table;
   private String filterText = "";

   public BlockDataSettingScreen(GuiTheme theme, BlockDataSetting<?> setting) {
      super(theme, "Configure Blocks");
      this.setting = setting;
   }

   public void initWidgets() {
      WTextBox filter = (WTextBox)this.add(this.theme.textBox("")).minWidth(400.0D).expandX().widget();
      filter.setFocused(true);
      filter.action = () -> {
         this.filterText = filter.get().trim();
         this.table.clear();
         this.initTable();
      };
      this.table = (WTable)this.add(this.theme.table()).expandX().widget();
      this.initTable();
   }

   public <T extends ICopyable<T> & ISerializable<T> & IChangeable & IBlockData<T>> void initTable() {
      Iterator var1 = class_7923.field_41175.iterator();

      while(true) {
         class_2248 block;
         while(var1.hasNext()) {
            block = (class_2248)var1.next();
            T blockData = (ICopyable)((Map)this.setting.get()).get(block);
            if (blockData != null && ((IChangeable)blockData).isChanged()) {
               BLOCKS.addFirst(block);
            } else {
               BLOCKS.add(block);
            }
         }

         var1 = BLOCKS.iterator();

         while(true) {
            String name;
            do {
               if (!var1.hasNext()) {
                  BLOCKS.clear();
                  return;
               }

               block = (class_2248)var1.next();
               name = Names.get(block);
            } while(!StringUtils.containsIgnoreCase(name, this.filterText));

            T blockData = (ICopyable)((Map)this.setting.get()).get(block);
            this.table.add(this.theme.itemWithLabel(block.method_8389().method_7854(), Names.get(block))).expandCellX();
            this.table.add(this.theme.label(blockData != null && ((IChangeable)blockData).isChanged() ? "*" : " "));
            WButton edit = (WButton)this.table.add(this.theme.button(GuiRenderer.EDIT)).widget();
            edit.action = () -> {
               T data = blockData;
               if (blockData == null) {
                  data = ((ICopyable)this.setting.defaultData.get()).copy();
               }

               MeteorClient.mc.method_1507(((IBlockData)data).createScreen(this.theme, block, this.setting));
            };
            WButton reset = (WButton)this.table.add(this.theme.button(GuiRenderer.RESET)).widget();
            reset.action = () -> {
               ((Map)this.setting.get()).remove(block);
               this.setting.onChanged();
               if (blockData != null && ((IChangeable)blockData).isChanged()) {
                  this.table.clear();
                  this.initTable();
               }

            };
            this.table.row();
         }
      }
   }
}
