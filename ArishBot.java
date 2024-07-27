package bots;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;


import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The RandBot is a very basic Bot that moves and shoots randomly. Sometimes it overheats.
 * It trash talks when it kills someone.
 *
 * @author Sam Scott
 * @version 1.0 (March 3, 2011)
 */
public class ArishBot extends Bot {

	/**
	 * Next message to send, or null if nothing to send.
	 */
	private String nextMessage = null;
	/**
	 * An array of trash talk messages.
	 */
	private String[] killMessages = {"Woohoo!!!", "In your face!", "Pwned", "Take that.", "Gotcha!", "Too easy.", "Hahahahahahahahahaha :-)"};
	/**
	 * Bot image
	 */
	Image current, up, down, right, left;
	/**
	 * My name (set when getName() first called)
	 */
	private String name = "runny";
	/**
	 * Counter for timing moves in different directions
	 */
	private int moveCount = 99;
	/**
	 * Next move to make
	 */
	private int move = BattleBotArena.UP;
	/**
	 * Counter to pause before sending a victory message
	 */
	private int msgCounter = 0;
	/**
	 * Used to decide if this bot should overheat or not
	 */
	private int targetNum = (int)(Math.random()*BattleBotArena.NUM_BOTS);
	/**
	 * The amount to sleep to simulate overheating because of excessive CPU
	 * usage.
	 */
	private int sleep = (int)(Math.random()*5+1);
	/**
	 * Set to True if we are trying to overheat
	 */
	private boolean overheat = false;

	/**
	 * Return image names to load
	 */
	public String[] imageNames()
	{
		String[] paths = {"arish_up.png", "arish_down.png", "arish_right.png", "arish_left.png"};
		return paths;
	}

	/**
	 * Store the images loaded by the arena
	 */
	public void loadedImages(Image[] images)
	{
		if (images != null)
		{
			if (images.length > 0)
				up = images[0];
			if (images.length > 1)
				down = images[1];
			if (images.length > 2)
				right = images[2];
			if (images.length > 3)
				left = images[3];
			current = up;
		}
	}


	
 	/**
 	* Calculates the next action for Runny.
 	* This method decides the Runny's next move based on its current status, the status of other bots (both alive and dead), and bullets in the game arena.
 	* It uses various strategies such as bullet dodging, enemy targeting, obstacle avoidance, and random movement.
 	*
 	* @param me The BotInfo object representing the current bot's status, including its position.
 	* @param shotOK A boolean indicating whether the bot is currently allowed to shoot.
 	* @param liveBots An array of BotInfo objects representing the live bots in the game.
 	* @param deadBots An array of BotInfo objects representing the dead bots in the game.
 	* @param bullets An array of Bullet objects representing the bullets currently active in the game.
 	* @return An integer representing the action to be taken. Actions are defined in the BattleBotArena class, such as moving in a direction, shooting, or sending a message.
 	*/

	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets)
	{
		// for overheating
		if (overheat){try{Thread.sleep(sleep);}catch (Exception e){}}

		// increase the move counter
		moveCount++;

		// Is it time to send a message?
		if (--msgCounter == 0)
		{
			move = BattleBotArena.SEND_MESSAGE;
			moveCount = 99;
			return move;
		}
			// Bullet Dodging Logic: Iterate through all bullets to avoid them.
			for (Bullet bullet : bullets) {
        		// Check if the bullet is close to the bot on the X-axis.
				boolean bulletOnCollisionCourseX = Math.abs(bullet.getX() - me.getX()) < 20;
				// Check if the bullet is close to the bot on the Y-axis.
				boolean bulletOnCollisionCourseY = Math.abs(bullet.getY() - me.getY()) < 20;
				
				// If the bullet is close on either axis, decide on dodging.
				if (bulletOnCollisionCourseX || bulletOnCollisionCourseY) {
					// If the bullet is moving horizontally and is close on the Y-axis...
					if (bullet.getXSpeed() != 0 && bulletOnCollisionCourseY) {
						// ...move up if the bullet is below, or down if above.
						if (bullet.getY() > me.getY()) {
							move = BattleBotArena.UP; 
							current = up;
						} else {
							move = BattleBotArena.DOWN; 
							current = down;
						}
					}
					// If the bullet is moving vertically and is close on the X-axis...
					else if (bullet.getYSpeed() != 0 && bulletOnCollisionCourseX) {
						// ...move left if the bullet is to the right, or right if to the left.
						if (bullet.getX() > me.getX()) {
							move = BattleBotArena.LEFT;
							current = left;
						} else {
							move = BattleBotArena.RIGHT;
							current = right;
						}
					}
					// Return the dodge move.
					return move;
				}
			}

			// Shooting Logic: Target and shoot at enemy bots. Check if it's okay to shoot.
			if (shotOK) {
				// Iterate through all live bots to find a target.
				for (BotInfo botInfo : liveBots) {
					// Decide whether to fire based on the alignment and position of enemy bots.
					//Firing at X direction
					if (Math.abs(botInfo.getY() - me.getY()) < 20) { 
						if (botInfo.getX() > me.getX()) {
							move = BattleBotArena.FIRERIGHT;
							current = right;
						} else {
							move = BattleBotArena.FIRELEFT;
							current = left;
						}
						return move;
					}

					//Firing at Y direction
					if (Math.abs(botInfo.getX() - me.getX()) < 20) { 
						if (botInfo.getY() > me.getY()) {
							move = BattleBotArena.FIREDOWN;
							current = down;
						} else {
							move = BattleBotArena.FIREUP;
							current = up;
						}
						return move;
					}
				}
			}

		// Obstacle Avoidance: Avoid colliding with live bots.
		for (BotInfo bot : liveBots) {
			System.out.println(Math.abs(bot.getX() - me.getX()));
			// Move away from nearby live bots to avoid collisions.
			if (Math.abs(bot.getX() - me.getX()) <= 150 && (Math.abs(bot.getY() - me.getY()) <= 150)) {
				if (bot.getX() > me.getX()) {
					move = BattleBotArena.LEFT;
				} else if (bot.getX() < me.getX()) {
					move = BattleBotArena.RIGHT;
				} else if (bot.getY() > me.getY()) {
					move = BattleBotArena.UP;
				} else if (bot.getY() < me.getY()) {
					move = BattleBotArena.DOWN;
				}
				return move;
			}
		}

		// Obstacle Avoidance: Avoid colliding with dead bots.
		for (BotInfo bot : deadBots) {
			// Move away from nearby dead bots to avoid collisions.
			if (Math.abs(bot.getX() - me.getX()) <= 150 && (Math.abs(bot.getY() - me.getY()) <= 150)) {
				if (bot.getX() > me.getX()) {
					move = BattleBotArena.LEFT;
				} else if (bot.getX() < me.getX()) {
					move = BattleBotArena.RIGHT;
				} else if (bot.getY() > me.getY()) {
					move = BattleBotArena.UP;
				} else if (bot.getY() < me.getY()) {
					move = BattleBotArena.DOWN;
				}
				return move;
			}
		}
		// Border Collision Avoidance: Prevent the bot from hitting the arena edges.
    	// Adjust the bot's direction if it's too close to the edges of the arena.

    	// Top edge
    	if ((move == BattleBotArena.UP) && (me.getY() < BattleBotArena.TOP_EDGE + 13)){
        	move = BattleBotArena.DOWN;
    	} // Bottom edge
    	if ((move == BattleBotArena.DOWN) && (me.getY() > BattleBotArena.BOTTOM_EDGE - 13)){
        	move = BattleBotArena.UP;
    	} // Left edge
    	if ((move == BattleBotArena.LEFT) && (me.getX() < BattleBotArena.LEFT_EDGE + 13)){
        	move = BattleBotArena.RIGHT;
    	} // Right edge
    	if ((move == BattleBotArena.RIGHT) && (me.getX() > BattleBotArena.RIGHT_EDGE - 13)){
        	move = BattleBotArena.LEFT;
    	}
		
		// Default Movement: Choose a random direction if no other actions are needed.
		if (move == 0) {
			move = (int)(Math.random() * 5); // Randomly choose a direction or stay
		}
	
		return move;
	}


	
	/**
	 * Decide whether we are overheating this round or not
	 */
	public void newRound()
	{
		if (botNumber >= targetNum-3 && botNumber <= targetNum+3)
			overheat = true;
	}
	

	/**
	 * Send the message and then blank out the message string
	 */
	public String outgoingMessage()
	{
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}

	/**
	 * Construct and return my name
	 */
	public String getName()
	{
		if (name == null)
			name = "Rand"+(botNumber<10?"0":"")+botNumber;
		return name;
	}

	/**
	 * Team "Arena"
	 */
	public String getTeamName()
	{
		return "2good";
	}

	/**
	 * Draws the bot at x, y
	 * @param g The Graphics object to draw on
	 * @param x Left coord
	 * @param y Top coord
	 */
	public void draw (Graphics g, int x, int y)
	{
		if (current != null)
			g.drawImage(current, x, y, Bot.RADIUS*2, Bot.RADIUS*2, null);
		else
		{
			g.setColor(Color.lightGray);
			g.fillOval(x, y, Bot.RADIUS*2, Bot.RADIUS*2);
		}
	}

	/**
	 * If the message is announcing a kill for me, schedule a trash talk message.
	 * @param botNum ID of sender
	 * @param msg Text of incoming message
	 */
	public void incomingMessage(int botNum, String msg)
	{
		if (botNum == BattleBotArena.SYSTEM_MSG && msg.matches(".*destroyed by "+getName()+".*"))
		{
			int msgNum = (int)(Math.random()*killMessages.length);
			nextMessage = killMessages[msgNum];
			msgCounter = (int)(Math.random()*30 + 30);
		}
	}
}