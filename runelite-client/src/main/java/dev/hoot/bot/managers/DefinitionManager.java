package dev.hoot.bot.managers;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import dev.hoot.api.events.NPCCompositionChanged;
import dev.hoot.api.game.Game;
import dev.hoot.api.game.Vars;
import dev.hoot.api.widgets.Widgets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.IndexDataBase;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.ObjectComposition;
import net.runelite.api.PlayerComposition;
import net.runelite.api.TileObject;
import net.runelite.api.VarbitComposition;
import net.runelite.api.events.DecorativeObjectChanged;
import net.runelite.api.events.DecorativeObjectDespawned;
import net.runelite.api.events.DecorativeObjectSpawned;
import net.runelite.api.events.GameObjectChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GroundObjectChanged;
import net.runelite.api.events.GroundObjectDespawned;
import net.runelite.api.events.GroundObjectSpawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemSpawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WallObjectDespawned;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetHiddenChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

@Singleton
@Slf4j
public class DefinitionManager
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	private static final Multimap<Integer, Integer> VARBITS = HashMultimap.create();
	private static final Multimap<Integer, Integer> VARBIT_TO_ENTITYID = HashMultimap.create();
	private static final Multimap<Integer, TileObject> TRANSFORMING_OBJECTS = HashMultimap.create();

	public void init()
	{
		clientThread.invoke(() ->
		{
			IndexDataBase indexVarbits = client.getIndexConfig();
			final int[] varbitIds = indexVarbits.getFileIds(14);
			for (int id : varbitIds)
			{
				VarbitComposition varbit = client.getVarbit(id);
				if (varbit != null)
				{
					VARBITS.put(varbit.getIndex(), id);
				}
			}
		});
	}

	@Subscribe
	private void onNpcCompositionChanged(NPCCompositionChanged event)
	{
		NPC npc = Game.getClient().getCachedNPCs()[event.getNpcIndex()];
		if (npc == null)
		{
			return;
		}

		NPCComposition composition = npc.getComposition();
		if (composition != null
				&& composition.getTransformVarbit() != -1
				&& composition.getConfigs() != null)
		{
			VARBIT_TO_ENTITYID.put(composition.getTransformVarbit(), event.getNpcIndex());
			npc.setTransformedComposition(composition.transform());
		}
	}

	// TileObjects
	@Subscribe
	private void onSpawn(GameObjectSpawned event)
	{
		checkTransformObject(event.getGameObject());
	}

	@Subscribe
	private void onChange(GameObjectChanged event)
	{
		checkTransformObject(event.getGameObject());
	}

	@Subscribe
	private void onDespawn(GameObjectDespawned event)
	{
		TRANSFORMING_OBJECTS.remove(event.getGameObject().getId(), event.getGameObject());
	}

	@Subscribe
	private void onSpawn(WallObjectSpawned event)
	{
		checkTransformObject(event.getWallObject());
	}

	@Subscribe
	private void onChange(WallObjectSpawned event)
	{
		checkTransformObject(event.getWallObject());
	}

	@Subscribe
	private void onDespawn(WallObjectDespawned event)
	{
		TRANSFORMING_OBJECTS.remove(event.getWallObject().getId(), event.getWallObject());
	}

	@Subscribe
	private void onSpawn(DecorativeObjectSpawned event)
	{
		checkTransformObject(event.getDecorativeObject());
	}

	@Subscribe
	private void onChange(DecorativeObjectChanged event)
	{
		checkTransformObject(event.getDecorativeObject());
	}

	@Subscribe
	private void onDespawn(DecorativeObjectDespawned event)
	{
		TRANSFORMING_OBJECTS.remove(event.getDecorativeObject().getId(), event.getDecorativeObject());
	}

	@Subscribe
	private void onSpawn(GroundObjectSpawned event)
	{
		checkTransformObject(event.getGroundObject());
	}

	@Subscribe
	private void onChange(GroundObjectChanged event)
	{
		checkTransformObject(event.getGroundObject());
	}

	@Subscribe
	private void onDespawn(GroundObjectDespawned event)
	{
		TRANSFORMING_OBJECTS.remove(event.getGroundObject().getId(), event.getGroundObject());
	}

	// Items
	@Subscribe
	private void onItemSpawn(ItemSpawned event)
	{
		client.cacheItem(event.getItem().getId(), client.getItemDefinition(event.getItem().getId()));
	}

	@Subscribe
	private void onItemContainerChanged(ItemContainerChanged event)
	{
		ItemContainer container = event.getItemContainer();
		if (container == null)
		{
			return;
		}

		for (Item item : container.getItems())
		{
			if (item == null)
			{
				continue;
			}

			client.cacheItem(item.getId(), client.getItemDefinition(item.getId()));
		}
	}

	@Subscribe
	private void onPlayerSpawned(PlayerSpawned event)
	{
		PlayerComposition playerComposition = event.getPlayer().getPlayerComposition();
		if (playerComposition == null)
		{
			return;
		}

		for (int equipmentId : playerComposition.getEquipmentIds())
		{
			if (equipmentId < 512)
			{
				continue;
			}

			client.cacheItem(equipmentId, client.getItemDefinition(equipmentId));
		}
	}

	// Widgets
	@Subscribe
	private void onWidgetHiddenChanged(WidgetHiddenChanged event)
	{
		checkWidget(event.getWidget());
	}

	@Subscribe
	private void onWidgetLoaded(WidgetLoaded event)
	{
		for (Widget widget : Widgets.get(event.getGroupId()))
		{
			checkWidget(widget);
		}
	}

	@Subscribe
	private void onWidgetClosed(WidgetClosed event)
	{
		for (Widget widget : Widgets.get(event.getGroupId()))
		{
			checkWidget(widget);
		}
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		switch (gameStateChanged.getGameState())
		{
			case LOGIN_SCREEN:
			case HOPPING:
				client.clearItemCache();
				break;
		}
	}

	@Subscribe
	private void onVarbitChanged(VarbitChanged e)
	{
		if (VARBITS.isEmpty())
		{
			init();
		}

		var changedVarbits = VARBITS.get(e.getIndex());
		for (int varbitId : changedVarbits)
		{
			if (!VARBIT_TO_ENTITYID.containsKey(varbitId))
			{
				continue;
			}

			int configValue = Vars.getBit(varbitId);
			Collection<Integer> entityIds = VARBIT_TO_ENTITYID.get(varbitId);
			for (Integer entityId : entityIds)
			{
				if (entityId < client.getCachedNPCs().length)
				{
					NPC npc = client.getCachedNPCs()[entityId];
					if (npc != null && npc.getComposition() != null)
					{
						NPCComposition current = npc.getTransformedComposition();
						NPCComposition transformed = npc.getComposition().transform();
						if (current == transformed)
						{
							continue;
						}

						npc.setTransformedComposition(transformed);

						if (configValue == 0)
						{
							log.debug("NPC {} reverted to default state", entityId);
						}
						else
						{
							log.debug("NPC {} transformed", entityId);
						}

						continue;
					}
				}

				ObjectComposition objectComposition = client.getObjectDefinition(entityId);
				Collection<TileObject> cachedObjects = TRANSFORMING_OBJECTS.get(entityId);
				for (TileObject cachedObject : cachedObjects)
				{
					if (objectComposition != null && cachedObject != null)
					{
						ObjectComposition current = cachedObject.getTransformedComposition();
						ObjectComposition transformed = objectComposition.getImpostor();
						if (current == transformed)
						{
							continue;
						}

						cachedObject.setTransformedComposition(transformed);

						if (configValue == 0)
						{
							log.debug("[{}] reverted to default state", transformed.getName());
						}
						else
						{
							log.debug("[{}: {}] transformed into [{}: {}]", objectComposition.getId(), objectComposition.getName(), transformed.getId(), transformed.getName());
						}
					}
				}
			}
		}
	}

	private void checkWidget(Widget widget)
	{
		if (widget == null)
		{
			return;
		}

		boolean hidden = widget.isHidden();
		widget.setVisible(!hidden);
		if (!hidden)
		{
			int itemId = widget.getItemId();
			log.trace("Widget {}, {} is now visible", WidgetInfo.TO_GROUP(widget.getId()), WidgetInfo.TO_CHILD(widget.getId()));
			if (itemId != -1)
			{
				log.debug("Caching item {} from widget", itemId);
				client.cacheItem(itemId, client.getItemDefinition(itemId));
			}
		}

		checkWidgetChildren(widget.getDynamicChildren());
		checkWidgetChildren(widget.getStaticChildren());
		checkWidgetChildren(widget.getNestedChildren());
	}

	private void checkWidgetChildren(Widget[] widgets)
	{
		if (widgets == null)
		{
			return;
		}

		for (Widget widget : widgets)
		{
			checkWidget(widget);
		}
	}

	private void checkTransformObject(TileObject object)
	{
		if (object == null)
		{
			return;
		}

		ObjectComposition composition = client.getObjectDefinition(object.getId());
		if (composition == null)
		{
			return;
		}

		if (composition.getImpostorIds() != null && composition.getTransformVarbit() != -1)
		{
			VARBIT_TO_ENTITYID.put(composition.getTransformVarbit(), object.getId());
			TRANSFORMING_OBJECTS.put(object.getId(), object);
			object.setTransformedComposition(composition.getImpostor());
		}
		else
		{
			object.setTransformedComposition(composition);
		}
	}
}
