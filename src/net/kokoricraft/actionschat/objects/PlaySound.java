package net.kokoricraft.actionschat.objects;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaySound extends PlayBase{
	
	private Sound sound;
	private float volume;
	private float pitch;
	
	public PlaySound(Player player, int max_amount, int max_sleep, Sound sound, float volume, float pitch) {
		super(player, null, max_amount, max_sleep);
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
	}

	@Override
	public void send() {
		player.playSound(player.getLocation(), sound, volume, pitch);
	}
}
