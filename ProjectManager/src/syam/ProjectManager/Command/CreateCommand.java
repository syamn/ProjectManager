package syam.ProjectManager.Command;

import syam.ProjectManager.Project.Project;
import syam.ProjectManager.Project.ProjectConfigManager;
import syam.ProjectManager.Util.Actions;

public class CreateCommand extends BaseCommand{
	public CreateCommand(){
		bePlayer = false;
		name = "create";
		argLength = 2;
		usage = "<name> <title> <- create new project";
	}

	@Override
	public boolean execute() {
		Project project = plugin.getProject(args.get(0));
		if (project != null){
			Actions.message(sender, null, "&cそのプロジェクト名は既に存在します！");
			return true;
		}

		// 新規プロジェクト登録
		project = new Project(plugin, args.get(0), args.get(1));
		ProjectConfigManager.setSelectedProject(player, project);

		Actions.message(sender, null, "&a新規プロジェクト'"+project.getName()+"'を登録して選択しました！");
		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("pm.admin.create");
	}
}
