package facejup.skillpack.main;

import facejup.skillpack.listeners.BindCastListener;
import facejup.skillpack.listeners.ExperienceListener;
import facejup.skillpack.listeners.SkillListener;
import facejup.skillpack.listeners.SkillListener;
import facejup.skillpack.listeners.ScrollUseListener;
import facejup.skillpack.listeners.ShopListener;
import facejup.skillpack.listeners.SkillMenuListener;

public class EventManager {
	
	private Main main;
	public BindCastListener bindCastListener;
	public ExperienceListener experienceListener;
	public SkillMenuListener skillMenuListener;
	public ShopListener shopListener;
	public ScrollUseListener scrollUseListener;
	public SkillListener passiveSkillListener;
	
	public EventManager(Main main)
	{
		this.main = main;
		this.passiveSkillListener = new SkillListener(this);
		scrollUseListener = new ScrollUseListener(this);
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
