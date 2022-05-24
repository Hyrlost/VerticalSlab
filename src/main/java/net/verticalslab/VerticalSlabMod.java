package net.verticalslab;

import net.fabricmc.api.ModInitializer;
import net.verticalslab.classes.VerticalSlabBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class VerticalSlabMod implements ModInitializer {
	public static final Block VERTICAL_SLAB_BLOCK = new VerticalSlabBlock(FabricBlockSettings.of(Material.WOOD).strength(4.0f));
	@Override
	public void onInitialize() {
		Registry.register(Registry.BLOCK, new Identifier("verticalslab", "vertical_slab"), VERTICAL_SLAB_BLOCK);
		Registry.register(Registry.ITEM, new Identifier("verticalslab", "vertical_slab"), new BlockItem(VERTICAL_SLAB_BLOCK, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
	}
}
