package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.packets.ClientPacket;
import net.runelite.api.packets.PacketBufferNode;

import java.util.List;

public class NPCPackets
{
	public static void npcFirstOption(NPC npc, boolean ctrlDown)
	{
		queueNPCAction1Packet(npc.getIndex(), ctrlDown);
	}

	public static void npcSecondOption(NPC npc, boolean ctrlDown)
	{
		queueNPCAction2Packet(npc.getIndex(), ctrlDown);
	}

	public static void npcThirdOption(NPC npc, boolean ctrlDown)
	{
		queueNPCAction3Packet(npc.getIndex(), ctrlDown);
	}

	public static void npcFourthOption(NPC npc, boolean ctrlDown)
	{
		queueNPCAction4Packet(npc.getIndex(), ctrlDown);
	}

	public static void npcFifthOption(NPC npc, boolean ctrlDown)
	{
		queueNPCAction5Packet(npc.getIndex(), ctrlDown);
	}

	public static void npcAction(NPC npc, String action, boolean ctrlDown)
	{
		List<String> actions = npc.getActions();
		int index = actions.indexOf(action);
		switch (index)
		{
			case 0:
				npcFirstOption(npc, ctrlDown);
				break;
			case 1:
				npcSecondOption(npc, ctrlDown);
				break;
			case 2:
				npcThirdOption(npc, ctrlDown);
				break;
			case 3:
				npcFourthOption(npc, ctrlDown);
				break;
			case 4:
				npcFifthOption(npc, ctrlDown);
				break;
		}
	}

	public static void spellOnNpc(int widgetId, NPC npc, boolean b)
	{
		queueSpellOnNpcPacket(npc.getIndex(), widgetId, b);
	}

	public static void queueItemOnNpcPacket(int npcIndex, int itemWidgetId, int itemId, int itemSlot, boolean ctrlDown)
	{
		createItemOnNpcPacket(npcIndex, itemWidgetId, itemId, itemSlot, ctrlDown).send();
	}

	public static void queueSpellOnNpcPacket(int npcIndex, int spellWidgetId, boolean ctrlDown)
	{
		createSpellOnNpcPacket(npcIndex, spellWidgetId, ctrlDown).send();
	}

	public static void queueNPCAction1Packet(int npcIndex, boolean ctrlDown)
	{
		createNpcFirstActionPacket(npcIndex, ctrlDown).send();
	}

	public static void queueNPCAction2Packet(int npcIndex, boolean ctrlDown)
	{
		createNpcSecondActionPacket(npcIndex, ctrlDown).send();
	}

	public static void queueNPCAction3Packet(int npcIndex, boolean ctrlDown)
	{
		createNpcThirdActionPacket(npcIndex, ctrlDown).send();
	}

	public static void queueNPCAction4Packet(int npcIndex, boolean ctrlDown)
	{
		createNpcFourthActionPacket(npcIndex, ctrlDown).send();
	}

	public static void queueNPCAction5Packet(int npcIndex, boolean ctrlDown)
	{
		createNpcFifthActionPacket(npcIndex, ctrlDown).send();
	}

	public static PacketBufferNode createItemOnNpcPacket(int npcIndex, int itemWidgetId, int itemId, int itemSlot,
											  boolean ctrlDown)
	{
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode packetBufferNode = Game.getClient().preparePacket(clientPacket.OPNPCU(), client.getPacketWriter().getIsaacCipher());
		packetBufferNode.getPacketBuffer().writeShortAdd(npcIndex);
		packetBufferNode.getPacketBuffer().writeShortAdd(itemSlot);
		packetBufferNode.getPacketBuffer().writeShortAdd(itemId);
		packetBufferNode.getPacketBuffer().writeIntIME(itemWidgetId);
		packetBufferNode.getPacketBuffer().writeByteNeg(ctrlDown ? 1 : 0);
		return packetBufferNode;
	}

	public static PacketBufferNode createSpellOnNpcPacket(int npcIndex, int spellWidgetId, boolean ctrlDown)
	{
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode packetBufferNode = Game.getClient().preparePacket(clientPacket.OPNPCT(), client.getPacketWriter().getIsaacCipher());
		packetBufferNode.getPacketBuffer().writeShortLE(-1);
		packetBufferNode.getPacketBuffer().writeShortAdd(npcIndex);
		packetBufferNode.getPacketBuffer().writeByteNeg(ctrlDown ? 1 : 0);
		packetBufferNode.getPacketBuffer().writeIntIME(spellWidgetId);
		packetBufferNode.getPacketBuffer().writeShortAddLE(-1);
		return packetBufferNode;
	}

	public static PacketBufferNode createNpcFirstActionPacket(int npcIndex, boolean ctrlDown)
	{
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode packetBufferNode = Game.getClient().preparePacket(clientPacket.OPNPC1(), client.getPacketWriter().getIsaacCipher());
		packetBufferNode.getPacketBuffer().writeShortLE(npcIndex);
		packetBufferNode.getPacketBuffer().writeByteAdd(ctrlDown ? 1 : 0);
		return packetBufferNode;
	}

	public static PacketBufferNode createNpcSecondActionPacket(int npcIndex, boolean ctrlDown)
	{
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode packetBufferNode = Game.getClient().preparePacket(clientPacket.OPNPC2(), client.getPacketWriter().getIsaacCipher());
		packetBufferNode.getPacketBuffer().writeByteSub(ctrlDown ? 1 : 0);
		packetBufferNode.getPacketBuffer().writeShortAddLE(npcIndex);
		return packetBufferNode;
	}

	public static PacketBufferNode createNpcThirdActionPacket(int npcIndex, boolean ctrlDown)
	{
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode packetBufferNode = Game.getClient().preparePacket(clientPacket.OPNPC3(), client.getPacketWriter().getIsaacCipher());
		packetBufferNode.getPacketBuffer().writeShortAddLE(npcIndex);
		packetBufferNode.getPacketBuffer().writeByte(ctrlDown ? 1 : 0);
		return packetBufferNode;
	}

	public static PacketBufferNode createNpcFourthActionPacket(int npcIndex, boolean ctrlDown)
	{
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode packetBufferNode = Game.getClient().preparePacket(clientPacket.OPNPC4(), client.getPacketWriter().getIsaacCipher());
		packetBufferNode.getPacketBuffer().writeShortAddLE(npcIndex);
		packetBufferNode.getPacketBuffer().writeByteNeg(ctrlDown ? 1 : 0);
		return packetBufferNode;
	}

	public static PacketBufferNode createNpcFifthActionPacket(int npcIndex, boolean ctrlDown)
	{
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode packetBufferNode = Game.getClient().preparePacket(clientPacket.OPNPC5(), client.getPacketWriter().getIsaacCipher());
		packetBufferNode.getPacketBuffer().writeShort(npcIndex);
		packetBufferNode.getPacketBuffer().writeByteAdd(ctrlDown ? 1 : 0);
		return packetBufferNode;
	}
}