package syam.ThemeCreative.Command;

import syam.ThemeCreative.Theme.Theme;
import syam.ThemeCreative.Theme.ThemeManager;
import syam.ThemeCreative.Util.Actions;

public class SelectCommand extends BaseCommand{
	public SelectCommand(){
		bePlayer = true;
		name = "select";
		argLength = 0;
		usage = "[name] <- select exist theme";
	}

	@Override
	public boolean execute() {
		if (args.size() >= 1){
			// theme select (テーマ名) - 選択
			Theme theme = plugin.getTheme(args.get(0));
			if (theme != null){
				ThemeManager.setSelectedTheme(player, theme);
				Actions.message(null, player, "&aテーマ'"+theme.getName()+"'を選択しました！");
			}else{
				Actions.message(null, player, "&cテーマ'"+args.get(0)+"'が見つかりません！");
				return true;
			}
		}else{
			// theme select - 選択解除
			if (ThemeManager.getSelectedTheme(player) != null){
				ThemeManager.setSelectedTheme(player, null);
			}
			Actions.message(null, player, "&aテーマの選択を解除しました！");
		}
		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("theme.admin.select");
	}
}
