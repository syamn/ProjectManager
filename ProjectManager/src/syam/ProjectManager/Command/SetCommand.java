package syam.ProjectManager.Command;

import java.util.ArrayList;
import java.util.List;

import syam.ProjectManager.Theme.Theme;
import syam.ProjectManager.Theme.ThemeManager;
import syam.ProjectManager.Util.Actions;
import syam.ProjectManager.Util.Util;

public class SetCommand extends BaseCommand{
	public SetCommand(){
		bePlayer = true;
		name = "set";
		argLength = 0;
		usage = "<option> [value] <- set option";
	}

	@Override
	public boolean execute() {
		if (args.size() <= 0){
			Actions.message(null, player, "&c設定項目を指定してください！");
			sendAvailableConf();
			return true;
		}

		// テーマ取得
		Theme theme = ThemeManager.getSelectedTheme(player);
		if (theme == null){
			Actions.message(null, player, "&c先に編集するテーマを選択してください");
			return true;
		}

		// 設定可能項目名を回す
		Configables conf = null;
		for (Configables check : Configables.values()){
			if (check.name().equalsIgnoreCase(args.get(0))){
				conf = check;
				break;
			}
		}
		if (conf == null){
			Actions.message(sender, null, "&cその設定項目は存在しません！");
			sendAvailableConf();
			return true;
		}

		// 設定項目によって処理を分ける
		switch (conf){
			case WARP: // ワープ地点設定
				return setWarp(theme);


			// 定義漏れ
			default:
				Actions.message(sender, null, "&c設定項目が不正です 開発者にご連絡ください");
				log.warning(logPrefix+ "Undefined configables! Please report this!");
				break;
		}

		return true;
	}

	/* ***** ここから各設定関数 ****************************** */

	private boolean setWarp(Theme theme){
		theme.setWarpLocation(player.getLocation());

		Actions.message(null, player, "&aテーマ'"+theme.getName()+"'のワープ地点を設定しました！");
		return true;
	}

	/* ***** ここまで **************************************** */

	private void sendAvailableConf(){
		List<String> col = new ArrayList<String>();
		for (Configables conf : Configables.values()){
			col.add(conf.name());
		}

		Actions.message(sender, null, "&6 " + Util.join(col, ", ").toLowerCase());
	}

	/**
	 * 設定可能項目の列挙体
	 * @author syam
	 */
	enum Configables{
		WARP("ワープ地点"),
		;

		private String name;

		Configables(String name){
			this.name = name;
		}

		public String getConfigName(){
			return this.name;
		}
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("theme.admin.set");
	}
}
