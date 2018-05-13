package facejup.skillpack.skills;

public enum PaymentType {
	
	COIN,SKILLPOINT;
	
	public static PaymentType getPaymentByName(String str)
	{
		for(PaymentType type : PaymentType.values())
		{
			if(type.name().equalsIgnoreCase(str))
				return type;
		}
		return null;
	}

}
