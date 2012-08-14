package syam.ThemeCreative.Command;

import java.io.File;

import syam.ThemeCreative.Theme.Theme;
import syam.ThemeCreative.Util.Actions;

public class DeleteCommand extends BaseCommand{
	public DeleteCommand(){
		bePlayer = false;
		name = "delete";
		argLength = 1;
		usage = "<name> <- delete exist theme";
	}

	@Override
	public boolean execute() {
		Theme theme = plugin.getTheme(args.get(0));
		if (theme == null){
			Actions.message(sender, null, "&cそのテーマ名は存在しません！");
			return true;
		}

		// テーマリストから削除
		plugin.themes.remove(args.get(0));

		// テーマデータファイルを削除
		String fileDir = plugin.getDataFolder() + System.getProperty("file.separator") + "themeData";
		boolean deleted = false;
		try{
			File file = new File(fileDir + System.getProperty("file.separator") + theme.getFileName());
			if (file.exists()){
				deleted = file.delete();
			}
		}catch (Exception ex){
			deleted = false;
			ex.printStackTrace();
		}

		if (!deleted)
			Actions.message(sender, null, "&cテーマ'"+args.get(0)+"'のテーマデータファイル削除中にエラーが発生しました！");
		else
			Actions.message(sender, null, "&aテーマ'"+args.get(0)+"'を削除しました！");
		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("theme.admin.delete");
	}
}
