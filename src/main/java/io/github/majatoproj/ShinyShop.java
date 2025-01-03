package io.github.majatoproj;

import io.github.majatoproj.init.ItemInit;
import io.github.majatoproj.init.CommandInit;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class ShinyShop implements ModInitializer {
	public static final String MOD_ID = "shinyshop";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("is dropping fresh shinies!");
		ItemInit.load();
		CommandInit.load();

		CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
			commandDispatcher.register(
					CommandManager.literal("convert")
							.executes(context -> {
								ServerCommandSource source = (ServerCommandSource) context.getSource();
								if (source.getEntity() instanceof ServerPlayerEntity player) {
									return handleConversion(player);
								} else {
									source.sendFeedback((Supplier<Text>) Text.literal("Conversion only works when you have 64 token shards"), false);
									return 0;
								}
							})
			);
		});
	}

	private int handleConversion(ServerPlayerEntity player) {
		Item tokenShard = ItemInit.TOKEN_SHARD;
		Item shinyToken = ItemInit.SHINY_TOKEN;

		int requiredAmount = 64;
		PlayerInventory inventory = player.getInventory();

		// Check if the player has enough token shards
		int totalShards = 0;
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.getItem() == tokenShard) {
				totalShards += stack.getCount();
				if (totalShards >= requiredAmount) {
					break;
				}
			}
		}

		if (totalShards < requiredAmount) {
			player.sendMessage(Text.literal("Not enough token shards, need 64"), false);
			return 0; // Command failed
		}

		// Remove 64 token shards
		int remainingToRemove = requiredAmount;
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.getItem() == tokenShard) {
				int count = stack.getCount();
				if (count <= remainingToRemove) {
					inventory.setStack(i, ItemStack.EMPTY); // Remove the stack
					remainingToRemove -= count;
				} else {
					stack.decrement(remainingToRemove);
					break;
				}
			}
		}

		// Give 1 shiny token
		ItemStack shinyTokenStack = new ItemStack(shinyToken, 1);
		if (!player.giveItemStack(shinyTokenStack)) {
			player.dropItem(shinyTokenStack, false); // Drop if inventory is full
		}

		player.sendMessage(Text.literal("Successfully converted!"), false);
		return 1;
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
	//alles hierna is extra

}