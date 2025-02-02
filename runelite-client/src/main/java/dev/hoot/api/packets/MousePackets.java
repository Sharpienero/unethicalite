package dev.hoot.api.packets;

import dev.hoot.api.game.Game;
import net.runelite.api.Client;
import net.runelite.api.packets.ClientPacket;
import net.runelite.api.packets.PacketBufferNode;

public class MousePackets
{
	public static void queueClickPacket()
	{
		MousePackets.queueClickPacket(0, 0);
	}

	public static void queueClickPacket(int x, int y)
	{
		Game.getClient().setMouseLastPressedMillis(System.currentTimeMillis());
		int mousePressedTime = ((int) (Game.getClient().getMouseLastPressedMillis() - Game.getClient().getClientMouseLastPressedMillis()));
		if (mousePressedTime < 0)
		{
			mousePressedTime = 0;
		}
		if (mousePressedTime > 32767)
		{
			mousePressedTime = 32767;
		}
		Game.getClient().setClientMouseLastPressedMillis(Game.getClient().getMouseLastPressedMillis());
		int mouseInfo = (mousePressedTime << 1) + 1;
		MousePackets.queueClickPacket(mouseInfo, x, y);
	}

	public static void queueClickPacket(int mouseInfo, int x, int y)
	{
		Client client = Game.getClient();
		ClientPacket clientPacket = Game.getClientPacket();
		PacketBufferNode packetBufferNode = Game.getClient().preparePacket(clientPacket.EVENT_MOUSE_CLICK(), client.getPacketWriter().getIsaacCipher());
		packetBufferNode.getPacketBuffer().writeShort(mouseInfo);
		packetBufferNode.getPacketBuffer().writeShort(x);
		packetBufferNode.getPacketBuffer().writeShort(y);
		client.getPacketWriter().queuePacket(packetBufferNode);
	}
}