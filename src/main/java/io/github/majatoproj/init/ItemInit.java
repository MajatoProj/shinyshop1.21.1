package io.github.majatoproj.init;

import io.github.majatoproj.ShinyShop;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;



public class ItemInit {
    //Add items here
    public static final Item TOKEN_SHARD = register("token_shard", new Item(new Item.Settings()));
    public static final Item SHINY_TOKEN = register("shiny_token", new Item(new Item.Settings()));
    public static final Item VANITY_TOKEN = register("vanity_token", new Item(new Item.Settings()));


    //Register items in registry of shinyshop
    public static <T extends Item> T register(String name, T item) {
        return Registry.register(Registries.ITEM, ShinyShop.id(name),item);
    }

    //Function to load in itemInit on initialisation from main
    public static void load() {}
}
