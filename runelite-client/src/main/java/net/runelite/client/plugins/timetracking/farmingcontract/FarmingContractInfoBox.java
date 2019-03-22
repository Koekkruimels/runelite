package net.runelite.client.plugins.timetracking.farmingcontract;

import java.awt.Color;
import java.awt.Image;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.timetracking.StatusEstimateFormatter;
import net.runelite.client.plugins.timetracking.TimeTrackingConfig;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ColorUtil;

class FarmingContractInfoBox extends InfoBox
{
	@Getter
	private final FarmingContract contract;
	private final FarmingContractManager manager;
	private final TimeTrackingConfig config;

	FarmingContractInfoBox(@Nonnull Image image, @Nonnull Plugin plugin, @Nonnull FarmingContract contract, @Nonnull TimeTrackingConfig config, @Nonnull FarmingContractManager manager)
	{
		super(image, plugin);
		this.contract = contract;
		this.config = config;
		this.manager = manager;
	}

	@Override
	public String getText()
	{
		return null;
	}

	@Override
	public Color getTextColor()
	{
		return null;
	}

	@Override
	public String getTooltip()
	{
		FarmingContractState state = manager.getState();

		Color contractColor;
		String contractDescription;
		switch (state)
		{
			case READY:
				contractDescription = "Ready";
				contractColor = ColorScheme.PROGRESS_COMPLETE_COLOR;
				break;
			case PATCHES_OCCUPIED:
				contractDescription = "Occupied";
				contractColor = ColorScheme.PROGRESS_ERROR_COLOR;
				break;
			case GROWING:
				contractDescription = "Ready " + StatusEstimateFormatter.getFormattedEstimate(manager.getContractTimeLeft(), config.estimateRelative());
				contractColor = Color.GRAY;
				break;
			case PATCH_READY:
			case UNKNOWN:
			default:
				contractDescription = null;
				contractColor = Color.GRAY;
				break;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(ColorUtil.wrapWithColorTag("Farming Contract", Color.WHITE));
		sb.append("</br>");
		sb.append(ColorUtil.wrapWithColorTag(contract.getName(), contractColor));

		if (contractDescription != null)
		{
			sb.append("</br>");
			sb.append(ColorUtil.wrapWithColorTag(contractDescription, contractColor));
		}

		return sb.toString();
	}
}
