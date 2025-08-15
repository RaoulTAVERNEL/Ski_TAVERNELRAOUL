package be.tav.pojo;

public enum Level {
    LEVEL_1("1"),
    LEVEL_2("2"),
    LEVEL_3("3"),
    LEVEL_4("4"),
	PETIT_SPIROU("Petit Spirou"),
	BRONZE("Bronze"),
	ARGENT("Argent"),
	OR("Or"),
	PLATINE("Platine"),
	DIAMANT("Diamant"),
	COMPETITION("Compétition"),
	HORS_PISTE("Hors-piste");
	
	private final String level;
	
	Level(String l) {
		this.level = l;
	}
	
	public String getLevel() { return this.level; }
}
