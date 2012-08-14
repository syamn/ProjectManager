package syam.ProjectManager.Command;

import syam.ProjectManager.Project.Project;
import syam.ProjectManager.Project.ProjectConfigManager;
import syam.ProjectManager.Util.Actions;

public class SelectCommand extends BaseCommand{
	public SelectCommand(){
		bePlayer = true;
		name = "select";
		argLength = 0;
		usage = "[name] <- select exist project";
	}

	@Override
	public boolean execute() {
		if (args.size() >= 1){
			// project select (プロジェクト名) - 選択
			Project project = plugin.getProject(args.get(0));
			if (project != null){
				ProjectConfigManager.setSelectedProject(player, project);
				Actions.message(null, player, "&aプロジェクト'"+project.getName()+"'を選択しました！");
			}else{
				Actions.message(null, player, "&cプロジェクト'"+args.get(0)+"'が見つかりません！");
				return true;
			}
		}else{
			// project select - 選択解除
			if (ProjectConfigManager.getSelectedProject(player) != null){
				ProjectConfigManager.setSelectedProject(player, null);
			}
			Actions.message(null, player, "&aプロジェクトの選択を解除しました！");
		}
		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("pm.admin.select");
	}
}
