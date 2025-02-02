package dev.hoot.api.items;

import dev.hoot.api.game.Game;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.WidgetInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Inventory extends Items
{
	private Inventory()
	{
	}

	private static final Inventory INVENTORY = new Inventory();

	@Override
	protected List<Item> all(Predicate<Item> filter)
	{
		List<Item> items = new ArrayList<>();
		ItemContainer container = Game.getClient().getItemContainer(InventoryID.INVENTORY);
		if (container == null)
		{
			return items;
		}

		for (Item item : container.getItems())
		{
			if (item.getId() != -1 && item.getName() != null && !item.getName().equals("null"))
			{
				item.setActionParam(item.getSlot());
				item.setWidgetId(WidgetInfo.INVENTORY.getPackedId());

				if (filter.test(item))
				{
					items.add(item);
				}
			}
		}

		return items;
	}

	public static List<Item> getAll(Predicate<Item> filter)
	{
		return INVENTORY.all(filter);
	}

	public static List<Item> getAll()
	{
		return getAll(x -> true);
	}

	public static List<Item> getAll(int... ids)
	{
		return INVENTORY.all(ids);
	}

	public static List<Item> getAll(String... names)
	{
		return INVENTORY.all(names);
	}

	public static Item getFirst(Predicate<Item> filter)
	{
		return INVENTORY.first(filter);
	}

	public static Item getFirst(int... ids)
	{
		return INVENTORY.first(ids);
	}

	public static Item getFirst(String... names)
	{
		return INVENTORY.first(names);
	}

	public static boolean contains(Predicate<Item> filter)
	{
		return INVENTORY.exists(filter);
	}

	public static boolean contains(int... id)
	{
		return INVENTORY.exists(id);
	}

	public static boolean contains(String... name)
	{
		return INVENTORY.exists(name);
	}

	public static int getCount(boolean stacks, Predicate<Item> filter)
	{
		return INVENTORY.count(stacks, filter);
	}

	public static int getCount(boolean stacks, int... ids)
	{
		return INVENTORY.count(stacks, ids);
	}

	public static int getCount(boolean stacks, String... names)
	{
		return INVENTORY.count(stacks, names);
	}

	public static int getCount(Predicate<Item> filter)
	{
		return INVENTORY.count(false, filter);
	}

	public static int getCount(int... ids)
	{
		return INVENTORY.count(false, ids);
	}

	public static int getCount(String... names)
	{
		return INVENTORY.count(false, names);
	}

	public static boolean isFull()
	{
		return getFreeSlots() == 0;
	}

	public static boolean isEmpty()
	{
		return getFreeSlots() == 28;
	}

	public static int getFreeSlots()
	{
		return 28 - getAll().size();
	}
}
