package syam.ThemeCreative.Command;

import syam.ThemeCreative.Theme.Theme;
import syam.ThemeCreative.Util.Actions;


public class ListCommand extends BaseCommand{
	public ListCommand(){
		bePlayer = false;
		name = "list";
		argLength = 0;
		usage = "<- show exist themes list";
	}

	@Override
	public boolean execute() {
		int count = plugin.themes.size();

		Actions.message(sender, null, "&a ===============&b ThemeList("+count+") &a===============");

		if (count <= 0){
			Actions.message(sender, null, " &7読み込まれているプロジェクトはありません");
		}else{
			for (Theme theme : plugin.themes.values()){
				String s = "&6"+theme.getName()+"&b:タイトル=&6"+theme.getTitle()+" &b参加者=&6"+theme.getPlayersMap().size()+"人";

				// メッセージ送信
				Actions.message(sender, null, s);
			}
		}

		Actions.message(sender, null, "&a ============================================");
		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("theme.user.list");
	}
}
