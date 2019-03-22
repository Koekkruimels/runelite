package net.runelite.client.plugins.timetracking.farmingcontract;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import net.runelite.client.plugins.timetracking.farming.Produce;

@Getter
@AllArgsConstructor
enum FarmingContract
{
	// Flower Patch
	MARIGOLD(1, Produce.MARIGOLD, "Marigold"),
	ROSEMARY(2, Produce.ROSEMARY, "Rosemary"),
	NASTURTIUM(3, Produce.NASTURTIUM, "Nasturtium"),
	WOAD(4, Produce.WOAD, "Woad"),
	LIMPWURT(5, Produce.LIMPWURT, "Limpwurt"),
	WHITE_LILY(6, Produce.WHITE_LILY, "White Lilies"),

	// Allotment Patch
	POTATOES(100, Produce.POTATO, "Potatoes"),
	ONIONS(101, Produce.ONION, "Onions"),
	TOMATOES(102, Produce.TOMATO, "Tomatoes"),
	SWEETCORN(103, Produce.SWEETCORN, "Sweetcorn"),
	STRAWBERRIES(104, Produce.STRAWBERRY, "Strawberries"),
	WATERMELONS(105, Produce.WATERMELON, "Watermelons"),
	SNAPE_GRASS(106, Produce.SNAPE_GRASS, "Snape Grass"),

	// Cactus Patch
	CACTUS(200, Produce.CACTUS, "Cactus"),
	POTATO_CACTUS(201, Produce.POTATO_CACTUS, "Potato Cacti"),

	// Bush Patch
	REBERRIES(300, Produce.REDBERRIES, "Redberries"),
	DWELLBERRIES(301, Produce.DWELLBERRIES, "Dwellberries"),
	JANGERBERRIES(302, Produce.JANGERBERRIES, "Jangerberries"),
	WHITEBERRIES(303, Produce.WHITEBERRIES, "White Berries"),
	POISON_IVY(304, Produce.POISON_IVY, "Poison Ivy Berries"),
	CADAVABERRIES(305, Produce.CADAVABERRIES, "Cadavaberries"),

	// Tree Patch
	OAK_TREE(400, Produce.OAK, "Oak Tree"),
	WILLOW_TREE(401, Produce.WILLOW, "Willow Tree"),
	MAPLE_TREE(402, Produce.MAPLE, "Maple Tree"),
	YEW_TREE(403, Produce.YEW, "Yew Tree"),
	MAGIC_TREE(404, Produce.MAGIC, "Magic Tree"),

	// Herb Patch
	GUAM(500, Produce.GUAM, "Guam"),
	TARROMIN(501, Produce.TARROMIN, "Tarromin"),
	HARRALANDER(502, Produce.HARRALANDER, "Harrakabder"),
	RANARR(503, Produce.RANARR, "Rannar"),
	TOADFLAX(504, Produce.TOADFLAX, "Toadflax"),
	AVANTOE(505, Produce.AVANTOE, "Avantoe"),
	IRIT(506, Produce.IRIT, "Irit"),
	KWUARM(507, Produce.KWUARM, "Kwuarm"),
	LANTADYME(508, Produce.LANTADYME, "Lantadyme"),
	CADANTINE(509, Produce.CADANTINE, "Cadantine"),
	DWARF_WEED(510, Produce.DWARF_WEED, "Dwarf Weed"),
	TORSTOL(511, Produce.TORSTOL, "Torstol"),
	SNAPDRAGON(512, Produce.SNAPDRAGON, "Snapdragon"),

	// Fruit Tree Patch
	APPLE_TREE(600, Produce.APPLE, "Cooking Apples"),
	BANANA_TREE(601, Produce.BANANA, "Banana"),
	ORANGE_TREE(602, Produce.ORANGE, "Oranges"),
	CURRY_TREE(603, Produce.CURRY, "Curry Leaves"),
	PINEAPPLE_PLANT(604, Produce.PINEAPPLE, "Pineapples"),
	PAPAYA_TREE(605, Produce.PAPAYA, "Papaya Fruits"),
	DRAGONFRUIT_TREE(606, Produce.DRAGONFRUIT, "Dragonfruits"),
	PALM_TREE(607, Produce.PALM, "Coconuts"),

	// Redwood Tree Patch
	REDWOOD_TREE(700, Produce.REDWOOD, "Redwood Tree"),

	// Celastrus Tree Patch
	CELASTRUS_TREE(800, Produce.CELASTRUS, "Celastrus Tree");

	private int id;
	private Produce produce;
	private String name;

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
