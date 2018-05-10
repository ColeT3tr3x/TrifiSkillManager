package facejup.skillpack.main;

import facejup.skillpack.commands.CMDBind;
import facejup.skillpack.commands.CMDCast;
import facejup.skillpack.commands.CMDShop;
import facejup.skillpack.commands.CMDSkills;

public class CommandManager {

	private Main main;
	private CMDBind cmdbind;
	private CMDSkills cmdskills;
	private CMDCast cmdcast;
	private CMDShop cmdshop;
	
	public CommandManager(Main main) {
		this.main = main;
		cmdshop = new CMDShop(this);
		cmdbind = new CMDBind(this);
		cmdskills = new CMDSkills(this);
		cmdcast = new CMDCast(this);
	}
	
	public Main getMain()
	{
		return this.main;
	}
	
	public CMDSkills getSkillsCommand()
	{
		return this.cmdskills;
	}

}
