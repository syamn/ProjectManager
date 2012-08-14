package syam.ProjectManager.Command;

import syam.ProjectManager.Project.Project;
import syam.ProjectManager.Util.Actions;


public class ListCommand extends BaseCommand{
	public ListCommand(){
		bePlayer = false;
		name = "list";
		argLength = 0;
		usage = "<- show exist project list";
	}

	@Override
	public boolean execute() {
		int count = plugin.projects.size();

		Actions.message(sender, null, "&a ==============&b ProjectList("+count+") &a==============");

		if (count <= 0){
			Actions.message(sender, null, " &7読み込まれているプロジェクトはありません");
		}else{
			for (Project project : plugin.projects.values()){
				String s = "&6"+project.getName()+"&b:タイトル=&6"+project.getTitle()+" &b参加者=&6"+project.getPlayersMap().size()+"人";

				// メッセージ送信
				Actions.message(sender, null, s);
			}
		}

		Actions.message(sender, null, "&a ============================================");
		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("pm.user.list");
	}
}
