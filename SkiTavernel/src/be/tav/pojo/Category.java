package be.tav.pojo;

public enum Category {
	SKI_ENFANT("Ski enfant"),
	SNOWBOARD_ENFANT("Snowboard enfant"),
	SKI_ADULTE("Ski adulte"),
	SNOWBOARD_ADULTE("Snowboard adulte"),
	TELEMARK("Télémark"),
	SKI_DE_FOND("Ski de fond");
	
	private final String category;
	
	Category(String c) { this.category = c; }
	
	public String getCategory() { return this.category; }
}
