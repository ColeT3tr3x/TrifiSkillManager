package facejup.skillpack.main;

import facejup.skillpack.listeners.BindCastListener;
import facejup.skillpack.listeners.ExperienceListener;
import facejup.skillpack.listeners.ShopListener;
import facejup.skillpack.listeners.SkillMenuListener;

public class EventManager {
	
	private Main main;
	public BindCastListener bindCastListener;
	public ExperienceListener experienceListener;
	public SkillMenuListener skillMenuListener;
	public ShopListener shopListener;
	
	public EventManager(Main main)
	{
		this.main = main;
		shopListener = new ShopListener(this);
		skillMenuListener = new SkillMenuListener(this);
		experienceListener = new ExperienceListener(this);
		bindCastListener = new BindCastListener(this);
	}
	
	public Main getMain()
	{
		return this.main;
	}
	
	public SkillMenuListener getSkillMenuListener()
	{
		return this.skillMenuListener;
	}

}
