package net.kokoricraft.actionschat.objects;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class PlayParticle extends PlayBase{
	private Particle particle;
	private double random_size;
	
	public PlayParticle(Player player, List<Player> players, int max_amount, int max_sleep, Particle particle, double random_size) {
		super(player, players, max_amount, max_sleep);
		this.particle = particle;
		this.random_size = random_size;
	}

	@Override
	public void send() {
		Location location = random_size == -1 ? player.getLocation() : getRandomLocation();
		
		for(Player other_player : players) {
			other_player.spawnParticle(particle, location, 1);
		}
	}
	
	private Location getRandomLocation() {
		Location location = player.getLocation();
		
		double fixed_y = location.getY()+1;
		
		double x = ThreadLocalRandom.current().nextDouble(location.getX()-random_size, location.getX()+random_size);
		double z = ThreadLocalRandom.current().nextDouble(location.getZ()-random_size, location.getZ()+random_size);
		double y = ThreadLocalRandom.current().nextDouble(fixed_y-random_size, fixed_y+random_size);
		
		return new Location(location.getWorld(), x, y, z);
	}
}
