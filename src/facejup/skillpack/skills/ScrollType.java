package facejup.skillpack.skills;

public enum ScrollType {
CAST, LEARN;
	
	public static ScrollType getScrollType(String str)
	{
		for(ScrollType type : ScrollType.values())
		{
			if(type.name().equalsIgnoreCase(str))
				return type;
		}
		return null;
	}
}
