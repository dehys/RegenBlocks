package com.dehys.regenblocks.events;

import com.dehys.regenblocks.Plugin;
import com.dehys.regenblocks.modules.RegenBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakEvent implements Listener{


    private final Plugin plugin;

    public BlockBreakEvent(Plugin plugin) {
        this.plugin = plugin;
    }


    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBlockBreak(org.bukkit.event.block.BlockBreakEvent e) {
        Block block = e.getBlock();
        World world = block.getWorld();
        Location location = block.getLocation();
        Material material = block.getType();

        if (e.getPlayer().hasPermission("regenblocks.break") && e.getPlayer().isSneaking()) return;

        for (World w : plugin.worlds) {
            if(w == block.getWorld()) {
                for (String m : plugin.recordedMaterials) {

                    String materialName = m.split(":")[0];
                    long regenTime = Long.parseLong(m.split(":")[1]);

                    if(materialName.equalsIgnoreCase(block.getType().name())){
                        if (e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
                            if(!(block.getDrops(e.getPlayer().getItemInHand()).isEmpty())) {
                                world.dropItemNaturally(location, new ItemStack(material, 1));
                            }
                        }else {
                            for (ItemStack is : block.getDrops(e.getPlayer().getItemInHand())) {
                                world.dropItemNaturally(location, is);
                            }
                        }
                        new RegenBlock(world, location, material, Plugin.getClock.getTickTime()+regenTime);
                        block.setType(plugin.replacementBlock);
                        e.setCancelled(true);
                        return;
                    }
                }
                return;
            }
        }
    }

}
