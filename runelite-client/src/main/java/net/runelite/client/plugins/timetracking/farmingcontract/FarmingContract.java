package net.runelite.client.plugins.timetracking.farmingcontract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import net.runelite.client.plugins.timetracking.farming.Produce;

@Getter
@AllArgsConstructor
enum FarmingContract
{
	// Flower Patch
	MARIGOLD(1, ItemID.MARIGOLDS, "Marigold"),
	ROSEMARY(2, ItemID.ROSEMARY, "Rosemary"),
	NASTURTIUM(3, ItemID.NASTURTIUMS, "Nastrium"),
	WOAD(4, ItemID.WOAD_LEAF, "Woad"),
	LIMPWURT(5, ItemID.LIMPWURT_ROOT, "Limpwurt"),
	WHITE_LILY(6, ItemID.WHITE_LILY, "White Lilies"),

	// Allotment Patch
	POTATOES(100, ItemID.POTATO, "Potatoes"),
	ONIONS(101, ItemID.ONION, "Onions"),
	TOMATOES(102, ItemID.TOMATO, "Tomatoes"),
	SWEETCORN(103, ItemID.SWEETCORN, "Sweetcorn"),
	STRAWBERRIES(104, ItemID.STRAWBERRY, "Strawberries"),
	WATERMELONS(105, ItemID.WATERMELON, "Watermelons"),
	SNAPE_GRASS(106, ItemID.SNAPE_GRASS, "Snape Grass"),

	// Cactus Patch
	CACTUS(200, ItemID.CACTUS_SPINE, "Cactus"),
	POTATO_CACTUS(201, ItemID.POTATO_CACTUS, "Potato Cacti"),

	// Bush Patch
	REBERRIES(300, ItemID.REDBERRIES, "Redberries"),
	DWELLBERRIES(301, ItemID.DWELLBERRIES, "Dwellberries"),
	JANGERBERRIES(302, ItemID.JANGERBERRIES, "Jangerberries"),
	WHITEBERRIES(303, ItemID.WHITE_BERRIES, "White Berries"),
	POISON_IVY(304, ItemID.POISON_IVY_BERRIES, "Poison Ivy Berries"),
	CADAVABERRIES(305, ItemID.CADAVA_BERRIES, "Cadavaberries"),

	// Tree Patch
	OAK_TREE(400, ItemID.OAK_LOGS, "Oak Tree"),
	WILLOW_TREE(401, ItemID.WILLOW_TREE, "Willow Tree"),
	MAPLE_TREE(402, ItemID.MAPLE_TREE, "Maple Tree"),
	YEW_TREE(403, ItemID.YEW_TREE, "Yew Tree"),
	MAGIC_TREE(404, ItemID.MAGIC_TREE, "Magic Tree"),

	// Herb Patch
	GUAM(500, ItemID.GUAM_LEAF, "Guam"),
	TARROMIN(501, ItemID.TARROMIN, "Tarromin"),
	HARRALANDER(502, ItemID.HARRALANDER, "Harrakabder"),
	RANARR(503, ItemID.RANARR_WEED, "Rannar"),
	TOADFLAX(504, ItemID.TOADFLAX, "Toadflax"),
	AVANTOE(505, ItemID.AVANTOE, "Avantoe"),
	IRIT(506, ItemID.IRIT_LEAF, "Irit"),
	KWUARM(507, ItemID.KWUARM, "Kwuarm"),
	LANTADYME(508, ItemID.LANTADYME, "Lantadyme"),
	CADANTINE(509, ItemID.CADANTINE, "Cadantine"),
	DWARF_WEED(510, ItemID.DWARF_WEED, "Dwarf Weed"),
	TORSTOL(511, ItemID.TORSTOL, "Torstol"),
	SNAPDRAGON(512, ItemID.SNAPDRAGON, "Snapdragon"),

	// Fruit Tree Patch
	APPLE_TREE(600, ItemID.COOKING_APPLE, "Cooking Apples"),
	BANANA_TREE(601, ItemID.BANANA, "Banana"),
	ORANGE_TREE(602, ItemID.ORANGE, "Oranges"),
	CURRY_TREE(603, ItemID.CURRY_LEAF, "Curry Leaves"),
	PINEAPPLE_PLANT(604, ItemID.PINEAPPLE, "Pineapples"),
	PAPAYA_TREE(605, ItemID.PAPAYA_FRUIT, "Papaya Fruits"),
	DRAGONFRUIT_TREE(606, ItemID.DRAGONFRUIT, "Dragonfruits"),
	PALM_TREE(607, ItemID.COCONUT, "Coconuts"),

	// Redwood Tree Patch
	REDWOOD_TREE(700, ItemID.REDWOOD_LOGS, "Redwood Tree"),

	// Celastrus Tree Patch
	CELASTRUS_TREE(800, ItemID.BATTLESTAFF, "Celastrus Tree");

	private int id;
	private int itemID;
	private String name;

	@Nullable
	Produce getProduce()
	{
		for (Produce produce : Produce.values())
		{
			if (produce.getItemID() == itemID || produce.getName().equalsIgnoreCase(name))
			{
				return produce;
			}
		}

		return null;
	}

	@Nullable
	PatchImplementation getPatchImplementation()
	{
		switch (this)
		{
			case MARIGOLD:
			case ROSEMARY:
			case NASTURTIUM:
			case WOAD:
			case LIMPWURT:
			case WHITE_LILY:
				return PatchImplementation.FLOWER;
			case POTATOES:
			case ONIONS:
			case TOMATOES:
			case SWEETCORN:
			case STRAWBERRIES:
			case WATERMELONS:
			case SNAPE_GRASS:
				return PatchImplementation.ALLOTMENT;
			case CADAVABERRIES:
			case REBERRIES:
			case DWELLBERRIES:
			case JANGERBERRIES:
			case WHITEBERRIES:
			case POISON_IVY:
				return PatchImplementation.BUSH;
			case GUAM:
			case TARROMIN:
			case HARRALANDER:
			case RANARR:
			case TOADFLAX:
			case IRIT:
			case AVANTOE:
			case KWUARM:
			case SNAPDRAGON:
			case CADANTINE:
			case LANTADYME:
			case DWARF_WEED:
			case TORSTOL:
				return PatchImplementation.HERB;
			case OAK_TREE:
			case WILLOW_TREE:
			case MAPLE_TREE:
			case YEW_TREE:
			case MAGIC_TREE:
				return PatchImplementation.TREE;
			case CACTUS:
			case POTATO_CACTUS:
				return PatchImplementation.CACTUS;
			case REDWOOD_TREE:
				return PatchImplementation.REDWOOD;
			case CELASTRUS_TREE:
				return PatchImplementation.CELASTRUS;

		}

		return null;
	}

	boolean requiresHealthCheck()
	{
		switch (this)
		{
			case CADAVABERRIES:
			case REBERRIES:
			case DWELLBERRIES:
			case JANGERBERRIES:
			case WHITEBERRIES:
			case POISON_IVY:
			case OAK_TREE:
			case WILLOW_TREE:
			case MAPLE_TREE:
			case YEW_TREE:
			case MAGIC_TREE:
			case CACTUS:
			case POTATO_CACTUS:
			case REDWOOD_TREE:
			case CELASTRUS_TREE:
				return true;

		}

		return false;
	}

	@Nullable
	Tab getTab()
	{
		PatchImplementation patchImplementation = getPatchImplementation();

		if (patchImplementation == null)
		{
			return null;
		}

		return patchImplementation.getTab();
	}

	@Nullable
	static FarmingContract getById(int id)
	{
		for (FarmingContract farmingContract : FarmingContract.values())
		{
			if (farmingContract.getId() == id)
			{
				return farmingContract;
			}
		}

		return null;
	}

	@Nullable
	static FarmingContract getByName(@Nonnull String name)
	{
		for (FarmingContract farmingContract : FarmingContract.values())
		{
			if (farmingContract.getName().equalsIgnoreCase(name))
			{
				return farmingContract;
			}
		}

		return null;
	}
}
