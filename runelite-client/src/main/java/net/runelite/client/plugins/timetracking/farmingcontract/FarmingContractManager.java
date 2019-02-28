package net.runelite.client.plugins.timetracking.farmingcontract;

import java.awt.Color;
import java.awt.Polygon;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.timetracking.StatusEstimateFormatter;
import net.runelite.client.plugins.timetracking.Tab;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.plugins.timetracking.TimeTrackingPlugin;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.FarmingPatch;
import net.runelite.client.plugins.timetracking.farming.FarmingRegion;
import net.runelite.client.plugins.timetracking.farming.FarmingTracker;
import net.runelite.client.plugins.timetracking.farming.FarmingWorld;
import net.runelite.client.plugins.timetracking.farming.PatchImplementation;
import net.runelite.client.plugins.timetracking.farming.PatchPrediction;
import net.runelite.client.plugins.timetracking.farming.Produce;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

@Slf4j
public class FarmingContractManager
{
	private static final int GUILDMASTER_JANE_WIDGET_MODEL_ID = 8628;
	private static final String CONTRACT_ASSIGNED_MESSAGE = "You have been assigned to grow %s.";
	private static final String CONTRACT_ASSIGN_REGEX = "(?:We need you to grow|Please could you grow) (?:some|a|an) ([a-zA-Z ]+)(?: for us\\?|\\.)";
	private static final String CONTRACT_REWARDED = "You'll be wanting a reward then. Here you go.";
	private static final String CONTRACT_COMPLETED = "You've completed a Farming Guild Contract. You should return to Guildmaster Jane.";
	private static final String FARMING_REGION_NAME = "Farming Guild";
	private static final String HTML_STRIP = "\\<[^>]*>";
	private static final String CONFIG_KEY_CONTRACT = "contract";

	private final Client client;
	private final ItemManager itemManager;
	private final ChatMessageManager chatMessageManager;
	private final TimeTrackingConfig config;
	private final TimeTrackingPlugin plugin;
	private final FarmingWorld farmingWorld;
	private final FarmingTracker farmingTracker;
	private final InfoBoxManager infoBoxManager;
	private final ConfigManager configManager;
	private final Polygon farmingGuildPolygon;

	private FarmingContract contract;
	private FarmingContractInfoBox infoBox;
	private FarmingContractState state;
	private boolean isInside;
	private long completionTime;

	@Inject
	public FarmingContractManager(
		@Nonnull Client client,
		@Nonnull ItemManager itemManager,
		@Nonnull ChatMessageManager chatMessageManager,
		@Nonnull TimeTrackingConfig config,
		@Nonnull TimeTrackingPlugin plugin,
		@Nonnull FarmingWorld farmingWorld,
		@Nonnull FarmingTracker farmingTracker,
		@Nonnull InfoBoxManager infoBoxManager,
		@Nonnull ConfigManager configManager
	)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.chatMessageManager = chatMessageManager;
		this.config = config;
		this.plugin = plugin;
		this.farmingWorld = farmingWorld;
		this.farmingTracker = farmingTracker;
		this.infoBoxManager = infoBoxManager;
		this.configManager = configManager;
		this.farmingGuildPolygon = createFarmingGuildPolygon();
		this.state = FarmingContractState.UNKNOWN;
	}

	// Contract

	@Nullable
	public FarmingContract getContract()
	{
		return contract;
	}

	public void setContract(@Nullable FarmingContract contract)
	{
		this.contract = contract;
		setStorageContract(contract);
		handleContractState();
	}

	public boolean hasContract()
	{
		return contract != null;
	}

	@Nullable
	public Tab getContractTab()
	{
		FarmingContract contract = getContract();

		if (contract == null)
		{
			return null;
		}

		return contract.getTab();
	}

	@Nonnull
	FarmingContractState getState()
	{
		return state;
	}

	long getContractTimeLeft()
	{
		return completionTime - Instant.now().getEpochSecond();
	}

	public boolean shouldHighlightFarmingTabPanel(@Nonnull FarmingPatch patch)
	{
		if (contract == null)
		{
			return false;
		}

		if (!patch.getRegion().getName().equalsIgnoreCase(FARMING_REGION_NAME))
		{
			return false;
		}

		if (contract.getPatchImplementation() != patch.getImplementation())
		{
			return false;
		}

		return true;
	}

	// Events

	public void onChatMessage(@Nonnull ChatMessage e)
	{
		handleContractCompleted(e.getType(), e.getMessage());
	}

	private void handleContractCompleted(@Nonnull ChatMessageType type, @Nonnull String message)
	{
		if (type != ChatMessageType.SERVER)
		{
			return;
		}

		if (!message.equals(CONTRACT_COMPLETED))
		{
			return;
		}

		setContract(null);
	}

	public void onUsernameChanged()
	{
		contract = getStorageContract();
		handleContractState();
	}

	public void onGameTick()
	{
		handleContractState();
		handleFarmingGuild();
		handleGuildmasterJaneWidgetDialog();
		handleInfoBox();
	}

	private void handleFarmingGuild()
	{
		Player player = client.getLocalPlayer();

		WorldPoint playerLocation = player.getWorldLocation();

		isInside = farmingGuildPolygon.contains(playerLocation.getX(), playerLocation.getY());
	}

	private void handleInfoBox()
	{
		if (isInside)
		{
			if (infoBox != null)
			{
				if (contract == null)
				{
					infoBoxManager.removeInfoBox(infoBox);
					infoBox = null;
				}
				else if (infoBox.getContract() != contract)
				{
					infoBoxManager.removeInfoBox(infoBox);
					infoBox = new FarmingContractInfoBox(itemManager.getImage(contract.getItemID()), plugin, contract, config, this);
					infoBoxManager.addInfoBox(infoBox);
				}
			}
			else if (contract != null)
			{
				infoBox = new FarmingContractInfoBox(itemManager.getImage(contract.getItemID()), plugin, contract, config, this);
				infoBoxManager.addInfoBox(infoBox);
			}
		}
		else if (infoBox != null)
		{
			infoBoxManager.removeInfoBox(infoBox);
			infoBox = null;
		}
	}

	private void handleGuildmasterJaneWidgetDialog()
	{
		Widget npcDialog = client.getWidget(WidgetInfo.DIALOG_NPC_HEAD_MODEL);

		if (npcDialog == null)
		{
			return;
		}

		if (npcDialog.getModelId() != GUILDMASTER_JANE_WIDGET_MODEL_ID)
		{
			return;
		}

		String dialogText = client.getWidget(WidgetInfo.DIALOG_NPC_TEXT)
			.getText()
			.replaceAll(HTML_STRIP, "");

		handleContractRewarded(dialogText);
		handleContractAssigning(dialogText);
	}

	private void handleContractRewarded(@Nonnull String dialogText)
	{
		if (!dialogText.equals(CONTRACT_REWARDED))
		{
			return;
		}

		if (contract == null)
		{
			return;
		}

		setContract(null);
	}

	private void handleContractAssigning(@Nonnull String dialogText)
	{
		Pattern pattern = Pattern.compile(CONTRACT_ASSIGN_REGEX);
		Matcher matcher = pattern.matcher(dialogText);

		if (!matcher.find())
		{
			return;
		}

		String name = matcher.group(1);

		FarmingContract farmingContract = FarmingContract.getByName(name);

		if (farmingContract == null)
		{
			return;
		}

		FarmingContract currentFarmingContract = getContract();

		if (farmingContract == currentFarmingContract)
		{
			return;
		}

		String chatMessage = String.format(CONTRACT_ASSIGNED_MESSAGE, farmingContract.getName());

		sendChatMessage(chatMessage);
		setContract(farmingContract);
	}

	private void handleContractState()
	{
		FarmingContract contract = getContract();

		if (contract == null)
		{
			state = FarmingContractState.UNKNOWN;
			return;
		}

		PatchImplementation patchImplementation = contract.getPatchImplementation();

		if (patchImplementation == null)
		{
			state = FarmingContractState.UNKNOWN;
			return;
		}

		List<PatchPrediction> predictions = new ArrayList<>();

		for (FarmingRegion farmingRegion : farmingWorld.getRegions().values())
		{
			if (!farmingRegion.getName().equalsIgnoreCase(FARMING_REGION_NAME))
			{
				continue;
			}

			for (FarmingPatch patch : farmingRegion.getPatches())
			{
				if (patch.getImplementation() != patchImplementation)
				{
					continue;
				}

				PatchPrediction prediction = farmingTracker.predictPatch(patch);

				if (prediction == null)
				{
					continue;
				}

				predictions.add(prediction);
			}
		}

		long shortestCompletionTime = Long.MAX_VALUE;

		for (PatchPrediction prediction : predictions)
		{
			if (prediction.getProduce() == null || prediction.getProduce() == Produce.WEEDS)
			{
				state = FarmingContractState.PATCH_READY;
				continue;
			}

			if (prediction.getProduce() != contract.getProduce() || (contract.requiresHealthCheck() && prediction.getCropState() == CropState.HARVESTABLE))
			{
				state = FarmingContractState.PATCHES_OCCUPIED;
				continue;
			}

			long completionTime = prediction.getDoneEstimate();

			if (completionTime - Instant.now().getEpochSecond() > 0)
			{
				if (completionTime < shortestCompletionTime)
				{
					shortestCompletionTime = completionTime;
				}

				continue;
			}

			state = FarmingContractState.READY;
			return;
		}

		if (shortestCompletionTime != Long.MAX_VALUE)
		{
			state = FarmingContractState.GROWING;
			completionTime = shortestCompletionTime;
		}
	}

	// Summary

	@Nonnull
	public FarmingContractSummary getSummary()
	{
		switch (state)
		{
			case PATCH_READY:
				return new FarmingContractSummary(contract.getName(), Color.GRAY);
			case PATCHES_OCCUPIED:
				return new FarmingContractSummary(contract.getName(), ColorScheme.PROGRESS_ERROR_COLOR);
			case GROWING:
				return new FarmingContractSummary("Ready at " + StatusEstimateFormatter.getFormattedEstimate(getContractTimeLeft(), config.estimateRelative()), Color.GRAY);
			case READY:
				return new FarmingContractSummary("Ready", ColorScheme.PROGRESS_COMPLETE_COLOR);
			case UNKNOWN:
			default:
				return new FarmingContractSummary("Unknown", Color.GRAY);
		}
	}

	// Util

	private void sendChatMessage(@Nonnull String message)
	{
		String chatMessage = new ChatMessageBuilder()
			.append(ChatColorType.HIGHLIGHT)
			.append(message)
			.build();

		QueuedMessage queuedMessage = QueuedMessage.builder()
			.type(ChatMessageType.GAME)
			.runeLiteFormattedMessage(chatMessage)
			.build();

		chatMessageManager.queue(queuedMessage);
	}

	// Storage

	@Nullable
	private FarmingContract getStorageContract()
	{
		try
		{
			return FarmingContract.getById(Integer.parseInt(configManager.getConfiguration(getConfigGroup(), CONFIG_KEY_CONTRACT)));
		}
		catch (NumberFormatException ignored)
		{
			return null;
		}
	}

	private void setStorageContract(@Nullable FarmingContract contract)
	{
		if (contract != null)
		{
			configManager.setConfiguration(getConfigGroup(), CONFIG_KEY_CONTRACT, String.valueOf(contract.getId()));
		}
		else
		{
			configManager.unsetConfiguration(getConfigGroup(), CONFIG_KEY_CONTRACT);
		}
	}

	@Nonnull
	private String getConfigGroup()
	{
		return TimeTrackingConfig.CONFIG_GROUP + "." + client.getUsername();
	}

	// Farming Guild

	private Polygon createFarmingGuildPolygon()
	{
		Polygon polygon = new Polygon();
		polygon.addPoint(1252, 3723);
		polygon.addPoint(1252, 3728);
		polygon.addPoint(1255, 3728);
		polygon.addPoint(1255, 3725);
		polygon.addPoint(1260, 3720);
		polygon.addPoint(1269, 3720);
		polygon.addPoint(1274, 3725);
		polygon.addPoint(1274, 3734);
		polygon.addPoint(1269, 3739);
		polygon.addPoint(1266, 3739);
		polygon.addPoint(1266, 3744);
		polygon.addPoint(1268, 3745);
		polygon.addPoint(1268, 3750);
		polygon.addPoint(1267, 3751);
		polygon.addPoint(1262, 3751);
		polygon.addPoint(1261, 3750);
		polygon.addPoint(1261, 3745);
		polygon.addPoint(1263, 3744);
		polygon.addPoint(1263, 3739);
		polygon.addPoint(1260, 3739);
		polygon.addPoint(1255, 3734);
		polygon.addPoint(1255, 3731);
		polygon.addPoint(1252, 3731);
		polygon.addPoint(1252, 3738);
		polygon.addPoint(1255, 3738);
		polygon.addPoint(1255, 3743);
		polygon.addPoint(1250, 3743);
		polygon.addPoint(1250, 3746);
		polygon.addPoint(1255, 3746);
		polygon.addPoint(1258, 3749);
		polygon.addPoint(1258, 3760);
		polygon.addPoint(1255, 3763);
		polygon.addPoint(1242, 3763);
		polygon.addPoint(1239, 3760);
		polygon.addPoint(1238, 3760);
		polygon.addPoint(1236, 3762);
		polygon.addPoint(1231, 3762);
		polygon.addPoint(1231, 3765);
		polygon.addPoint(1226, 3765);
		polygon.addPoint(1226, 3762);
		polygon.addPoint(1222, 3762);
		polygon.addPoint(1220, 3760);
		polygon.addPoint(1220, 3749);
		polygon.addPoint(1222, 3747);
		polygon.addPoint(1226, 3747);
		polygon.addPoint(1226, 3744);
		polygon.addPoint(1231, 3744);
		polygon.addPoint(1231, 3747);
		polygon.addPoint(1236, 3747);
		polygon.addPoint(1238, 3749);
		polygon.addPoint(1239, 3749);
		polygon.addPoint(1242, 3746);
		polygon.addPoint(1247, 3746);
		polygon.addPoint(1247, 3743);
		polygon.addPoint(1242, 3743);
		polygon.addPoint(1242, 3738);
		polygon.addPoint(1245, 3738);
		polygon.addPoint(1245, 3731);
		polygon.addPoint(1242, 3731);
		polygon.addPoint(1242, 3734);
		polygon.addPoint(1237, 3739);
		polygon.addPoint(1228, 3739);
		polygon.addPoint(1223, 3734);
		polygon.addPoint(1223, 3725);
		polygon.addPoint(1228, 3720);
		polygon.addPoint(1237, 3720);
		polygon.addPoint(1242, 3725);
		polygon.addPoint(1242, 3728);
		polygon.addPoint(1245, 3728);
		polygon.addPoint(1245, 3723);
		return polygon;
	}
}
