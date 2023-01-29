package josegamerpt.realmines.gui;

import josegamerpt.realmines.RealMines;
import josegamerpt.realmines.mines.BlockMine;
import josegamerpt.realmines.mines.RMine;
import josegamerpt.realmines.config.Language;
import josegamerpt.realmines.utils.GUIBuilder;
import josegamerpt.realmines.utils.Items;
import josegamerpt.realmines.utils.PlayerInput;
import josegamerpt.realmines.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GUIManager {

    private RealMines rm;

    public GUIManager(RealMines rm)
    {
        this.rm = rm;
    }

    public void openMineChooserType(Player target, String name) {
        new BukkitRunnable() {
            @Override
            public void run() {
                GUIBuilder inventory = new GUIBuilder(Text.color(Language.file().getString("GUI.Choose-Name").replaceAll("%mine%", name)), 27, target.getUniqueId());

                inventory.addItem(e -> {
                            target.closeInventory();
                            RealMines.getInstance().getMineManager().createMine(target, name);
                        }, Items.createItemLore(Material.CHEST, 1, Language.file().getString("GUI.Items.Blocks.Name"), Collections.emptyList()),
                        11);

                inventory.addItem(e -> {
                            target.closeInventory();
                            RealMines.getInstance().getMineManager().createSchematicMine(target, name);
                        }, Items.createItemLore(Material.FILLED_MAP, 1, Language.file().getString("GUI.Items.Schematic.Name"), Collections.emptyList()),
                        15);

                inventory.openInventory(target);
            }
        }.runTaskLater(rm, 2);
    }

    public void openMine(RMine m, Player target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                GUIBuilder inventory = new GUIBuilder(Text.color(m.getColorIcon() + " " + m.getDisplayName() + " &r" + Text.getProgressBar(m.getRemainingBlocks(), m.getBlockCount(), 10, '■', ChatColor.GREEN, ChatColor.RED)), 27, target.getUniqueId(),
                        Items.createItem(Material.BLACK_STAINED_GLASS_PANE, 1, "&f"));

                if (m instanceof BlockMine) {
                    inventory.addItem(e -> {
                                target.closeInventory();
                                Bukkit.getScheduler().scheduleSyncDelayedTask(rm, () -> {
                                    MineBlocksViewer v = new MineBlocksViewer(rm, target, m);
                                    v.openInventory(target);
                                }, 2);
                            }, Items.createItemLore(Material.CHEST, 1, Language.file().getString("GUI.Items.Blocks.Name"), Language.file().getStringList("GUI.Items.Blocks.Description")),
                            10);
                }

                inventory.addItem(e -> {
                    target.closeInventory();
					Bukkit.getScheduler().scheduleSyncDelayedTask(rm, () -> {
						MineResetMenu mrm = new MineResetMenu(rm, target, m);
						mrm.openInventory(target);
					}, 2);
                        }, Items.createItemLore(Material.ANVIL, 1, Language.file().getString("GUI.Items.Resets.Name"), Language.file().getStringList("GUI.Items.Resets.Description")),
                        12);
                inventory.addItem(e -> {
                    target.closeInventory();
                    rm.getMineManager().teleport(target, m, false);
                }, Items.createItemLore(Material.ENDER_PEARL, 1, Language.file().getString("GUI.Items.Teleport.Name"), Language.file().getStringList("GUI.Items.Teleport.Description")), 20);

                inventory.addItem(e -> {
                    target.closeInventory();
					Bukkit.getScheduler().scheduleSyncDelayedTask(rm, () -> {
						MaterialPicker s = new MaterialPicker(rm, m, target, MaterialPicker.PickType.ICON, "");
						s.openInventory(target);
					}, 2);
                }, Items.createItemLore(m.getIcon(), 1, Language.file().getString("GUI.Items.Icon.Name"), Language.file().getStringList("GUI.Items.Icon.Description")), 2);

                inventory.addItem(e -> {
                    target.closeInventory();
                    new PlayerInput(target, s -> {
                        m.setDisplayName(s);
                        rm.getGUIManager().openMine(m, target);
                    }, s -> rm.getGUIManager().openMine(m, target));
                }, Items.createItemLore(Material.PAPER, 1, Language.file().getString("GUI.Items.Name.Name"), Language.file().getStringList("GUI.Items.Name.Description")), 4);

                inventory.addItem(e -> {
                    m.clear();
                    Text.send(target, Language.file().getString("System.Mine-Clear"));
                }, Items.createItemLore(Material.TNT, 1, Language.file().getString("GUI.Items.Clear.Name"), Language.file().getStringList("GUI.Items.Clear.Description")), 22);

                inventory.addItem(e -> m.reset(), Items.createItemLore(Material.DROPPER, 1, Language.file().getString("GUI.Items.Reset.Name"), Language.file().getStringList("GUI.Items.Reset.Description")), 14);

                inventory.addItem(e -> m.setHighlight(!m.isHighlighted()), Items.createItemLore(Material.REDSTONE_TORCH, 1, Language.file().getString("GUI.Items.Boundaries.Name"), Language.file().getStringList("GUI.Items.Boundaries.Description")), 6);

                inventory.addItem(e -> {
                    target.closeInventory();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(rm, () -> {
                        MineColorPicker mcp = new MineColorPicker(rm, target, m);
                        mcp.openInventory(target);
                    }, 2);
                }, Items.getMineColor(m.getColor(), Language.file().getString("GUI.Items.MineColor.Name"), Language.file().getStringList("GUI.Items.MineColor.Description")), 24);

                if (m instanceof BlockMine) {
                    inventory.addItem(e -> {
                        target.closeInventory();
                        Bukkit.getScheduler().scheduleSyncDelayedTask(rm, () -> {
                            MineFaces m1 = new MineFaces(rm, target, m);
                            m1.openInventory(target);
                        }, 2);
                    }, Items.createItemLore(Material.SCAFFOLDING, 1, Language.file().getString("GUI.Items.Faces.Name"), Language.file().getStringList("GUI.Items.Faces.Description")), 16);
                }

                inventory.addItem(e -> {
                    target.closeInventory();
                    Bukkit.getScheduler().scheduleSyncDelayedTask(rm, () -> {
                        MineViewer m1 = new MineViewer(rm, target);
                        m1.openInventory(target);
                    }, 2);
                }, Items.createItemLore(Material.RED_BED, 1, Language.file().getString("GUI.Items.Back.Name"), Language.file().getStringList("GUI.Items.Back.Description")), 26);

                inventory.addItem(event -> {
                }, makeMineIcon(m), 13);

                inventory.openInventory(target);
            }
        }.runTaskLater(rm, 2);
    }

    public static ItemStack makeMineIcon(RMine m) {
         return Items.createItemLore(Material.TRIPWIRE_HOOK, 1, m.getColorIcon() + " &6&l" + m.getDisplayName(), var(m));
    }

    private static List<String> var(RMine m) {
        List<String> ret = new ArrayList<>();
        List<String> config = Language.file().getStringList("GUI.Items.Mine.Description");
        config.remove(config.size() - 1);
        config.forEach(s -> ret.add(Text.color(s.replaceAll("%remainingblocks%", m.getRemainingBlocks() + "").replaceAll("%totalblocks%", m.getBlockCount() + "").replaceAll("%bar%", getBar(m)))));
        return ret;
    }

    public static String getBar(RMine m) {
        return Text.getProgressBar(m.getRemainingBlocks(), m.getBlockCount(), 10, '■', ChatColor.GREEN, ChatColor.RED);
    }
}
