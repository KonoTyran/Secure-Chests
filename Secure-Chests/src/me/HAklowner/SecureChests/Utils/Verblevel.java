package me.HAklowner.SecureChests.Utils;

public class Verblevel {
	
	private boolean OWN = false;
	private boolean OTHERS = true;
	private boolean DEBUG = false;
	private boolean OVERRIDE = true;
	private boolean DENY = true;
	
	public boolean getOwn() {
		return OWN;
	}
	
	public boolean getOther() {
		return OTHERS;
	}
	
	public boolean getDebug() {
		return DEBUG;
	}
	
	public boolean getOverride() {
		return OVERRIDE;
	}
	
	public boolean getDeny() {
		return DENY;
	}
	
	public void setOwn(boolean own) {
		OWN = own;
	}
	
	public void setOther(boolean others) {
		OTHERS = others;
	}
	
	public void setDebug(boolean debug) {
		DEBUG = debug;
	}
	
	public void setOverride(boolean override) {
		OVERRIDE = override;
	}
	
	public void setDeny(boolean deny) {
		DENY = deny;
	}
}
