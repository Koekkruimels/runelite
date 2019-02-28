/*
 * Copyright (c) 2018, Daniel Teo <https://github.com/takuyakanbr>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.timetracking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.annotation.Nonnull;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.ImageUtil;

class OverviewItemPanel extends JPanel
{
	interface Selectable
	{
		void onSelected();
	}

	interface IsSelectable
	{
		boolean isSelectable();
	}

	private static final ImageIcon ARROW_RIGHT_ICON;

	private static final Color HOVER_COLOR = ColorScheme.DARKER_GRAY_HOVER_COLOR;

	private final JPanel textContainer;
	private final JLabel statusLabel;
	private final JLabel arrowLabel;

	private final IsSelectable isSelectable;
	private final Selectable selectable;

	private boolean isHighlighted;

	static
	{
		ARROW_RIGHT_ICON = new ImageIcon(ImageUtil.getResourceStreamFromClass(TimeTrackingPlugin.class, "/util/arrow_right.png"));
	}

	OverviewItemPanel(@Nonnull ItemManager itemManager, @Nonnull TimeTrackingPanel pluginPanel, @Nonnull Tab tab, @Nonnull String title)
	{
		this(itemManager, () -> pluginPanel.switchTab(tab), () -> true, title, tab.getItemID());
	}

	OverviewItemPanel(@Nonnull ItemManager itemManager, @Nonnull Selectable selectable, @Nonnull IsSelectable isSelectable, @Nonnull String title, int iconItemID)
	{
		this.selectable = selectable;
		this.isSelectable = isSelectable;

		setBackground(ColorScheme.DARKER_GRAY_COLOR);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(7, 7, 7, 7));

		JLabel iconLabel = new JLabel();
		iconLabel.setMinimumSize(new Dimension(36, 32));
		itemManager.getImage(iconItemID).addTo(iconLabel);
		add(iconLabel, BorderLayout.WEST);

		textContainer = new JPanel();
		textContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		textContainer.setLayout(new GridLayout(2, 1));
		textContainer.setBorder(new EmptyBorder(5, 7, 5, 7));

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				selectable.onSelected();

				setHighlighted(false);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				setHighlighted(true);
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				setHighlighted(true);
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setHighlighted(false);
			}
		});

		JLabel titleLabel = new JLabel(title);
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(FontManager.getRunescapeSmallFont());

		statusLabel = new JLabel();
		statusLabel.setForeground(Color.GRAY);
		statusLabel.setFont(FontManager.getRunescapeSmallFont());

		textContainer.add(titleLabel);
		textContainer.add(statusLabel);

		add(textContainer, BorderLayout.CENTER);

		arrowLabel = new JLabel(ARROW_RIGHT_ICON);
		arrowLabel.setVisible(isSelectable.isSelectable());
		add(arrowLabel, BorderLayout.EAST);
	}

	void updateStatus(String text, Color color)
	{
		statusLabel.setText(text);
		statusLabel.setForeground(color);

		boolean isSelectable = this.isSelectable.isSelectable();

		arrowLabel.setVisible(isSelectable);

		if (isHighlighted && !isSelectable)
		{
			setHighlighted(false);
		}
	}

	private void setHighlighted(boolean highlighted)
	{
		if (highlighted && !isSelectable.isSelectable())
		{
			return;
		}

		setBackground(highlighted ? HOVER_COLOR : ColorScheme.DARKER_GRAY_COLOR);
		setCursor(new Cursor(highlighted && getMousePosition(true) != null ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
		textContainer.setBackground(highlighted ? HOVER_COLOR : ColorScheme.DARKER_GRAY_COLOR);

		isHighlighted = highlighted;
	}
}
