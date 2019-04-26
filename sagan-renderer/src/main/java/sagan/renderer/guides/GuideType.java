package sagan.renderer.guides;

/**
 * Guide Types
 */
enum GuideType {

	GETTING_STARTED("getting-started", "gs-"), TUTORIAL("tutorial", "tut-"),
	TOPICAL("topical", "top-"), UNKNOWN("unknown", "");

	private final String name;
	private final String prefix;


	GuideType(String name, String prefix) {
		this.name = name;
		this.prefix = prefix;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
