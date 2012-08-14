package syam.ProjectManager.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import syam.ProjectManager.Theme.Theme;
import syam.ProjectManager.Util.Actions;

public class TpCommand extends BaseCommand{
	public TpCommand(){
		bePlayer = true;
		name = "list";
		argLength = 0;
		usage = "[theme] <- tp to specific theme";
	}

	@Override
	public boolean execute() {
		Theme theme = null;
		Boolean joinedTheme = false;
		if (args.size() == 0){
			List<Theme> joined = plugin.getJoinedTheme(player.getName());
			if (joined.size() == 1){
				theme = joined.get(0);
				Actions.message(null, player, "&a参加中のプロジェクト'"+theme.getName()+"'にテレポートします！");
			}
			else if(joined.size() == 0){
				Actions.message(null, player, "&c参加中のプロジェクトがありません！ /theme tp ");
				return true;
			}
			else{
				Actions.message(null, player, "&c参加中のプロジェクトが複数あります！");
				return true;
			}
		}else{
			Theme t = plugin.getTheme(args.get(0));
			if (t != null){
				theme = t;
			}
		}




		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("theme.user.tp");
	}
}
