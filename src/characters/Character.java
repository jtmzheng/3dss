package characters;

/**
 * Abstract character class for the player and enemies.
 * 
 * @author Adi
 */
public abstract class Character {
	/**
	 * Hit points for the character.
	 */
	protected float HP = 100f;
	
	/**
	 * Abstract move function.
	 */
	public abstract void move();
	
	/**
	 * Abstract damage function.
	 * 
	 * @param damageAmt The amount the character is damaged by.
	 */
	public abstract void damage(float damageAmt);
	
	/**
	 * Gets the number of hit points the character has.
	 * 
	 * @return HP hit points
	 */
	public float getHP() {
		return HP;
	}
	
	/**
	 * Sets the number of hit points the character has.
	 * 
	 * @param newHP hit points
	 */
	public void setHP(float newHP) {
		HP = newHP;
	}
}
