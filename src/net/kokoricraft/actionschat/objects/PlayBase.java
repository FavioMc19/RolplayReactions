package net.kokoricraft.actionschat.objects;

import java.util.List;

import org.bukkit.entity.Player;

public abstract class PlayBase {
	
	protected List<Player> players;
	protected Player player;
	private int max_amount;
	private int max_sleep;
	private boolean end;
	private boolean used_first;
	private int amount;
	private int sleep;
	
	public PlayBase(Player player, List<Player> players, int max_amount, int max_sleep) {
		this.player = player;
		this.players = players;
		this.max_amount = max_amount;
		this.max_sleep = max_sleep;
		this.end = false;
		this.used_first = false;
		this.amount = 0;
		this.sleep = 0;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public int getSleep() {
		return sleep;
	}
	
	public int getMaxSleep() {
		return max_sleep;
	}
	
	public void sleep() {
		sleep++;
	}
	
	public boolean ended() {
		return end;
	}
	
	public boolean usedFirst() {
		return used_first;
	}
	
	public void useFirst() {
		used_first = true;
		send();
	}
	
	public void sendAction() {
		 if(this.amount+1 >= max_amount-1)
			 end = true;
		 
		 amount++;
		 sleep = 0;
		 
		 send();
	}
	
	public abstract void send();
}
