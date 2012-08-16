package syam.ProjectManager.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import syam.ProjectManager.Project.Project;
import syam.ProjectManager.Util.Actions;

public class TpCommand extends BaseCommand{
	public TpCommand(){
		bePlayer = true;
		name = "list";
		argLength = 0;
		usage = "[project] <- tp to specific project";
	}

	@Override
	public boolean execute() {
		Project project = null;
		Boolean joinedProject = false;
		if (args.size() == 0){
			List<Project> joined = plugin.getJoinedProject(player.getName());
			if (joined.size() == 1){
				project = joined.get(0);
				Actions.message(null, player, "&a参加中のプロジェクト'"+project.getID()+"'にテレポートします！");
			}
			else if(joined.size() == 0){
				Actions.message(null, player, "&c参加中のプロジェクトがありません！ /project tp <プロジェクト名>");
				return true;
			}
			else{
				Actions.message(null, player, "&c参加中のプロジェクトが複数あります！ /project tp <プロジェクト名>");
				return true;
			}
		}else{
			Project t = plugin.getProject(args.get(0));
			if (t == null){
				Actions.message(null, player, "&cプロジェクトID'"+args.get(0)+"'が見つかりません！");
				return true;
			}else{
				project = t;
			}
		}


		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("pm.user.tp");
	}
}
