package syam.ThemeCreative.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import syam.ThemeCreative.Theme.Theme;
import syam.ThemeCreative.Theme.ThemeManager;
import syam.ThemeCreative.Util.Actions;
import syam.ThemeCreative.Util.Util;

public class MemberCommand extends BaseCommand{
	public MemberCommand(){
		bePlayer = true;
		name = "member";
		argLength = 0;
		usage = "<action> <player> <- manage theme member";
	}

	/**
	 * コマンド実行時に呼ばれる
	 */
	@Override
	public boolean execute() {
		// テーマ取得
		Theme theme = ThemeManager.getSelectedTheme(player);
		if (theme == null){
			Actions.message(null, player, "&c先に管理するテーマを選択してください");
			return true;
		}

		// 引数チェック
		if (args.size() <= 0){
			Actions.message(sender, null, "&c行う管理アクションを指定してください");
			sendAvailableAction();
			return true;
		}
		else if(args.size() == 1){
			Actions.message(sender, null, "&cアクションを行う対象のプレイヤー名を入力してください");
			return true;
		}

		// アクションを回す
		Action ac = null;
		for (Action check : Action.values()){
			if (check.name().equalsIgnoreCase(args.get(0))){
				ac = check;
				break;
			}
		}
		if (ac == null){
			Actions.message(sender, null, "&cそのアクションは存在しません！");
			sendAvailableAction();
			return true;
		}

		// プレイヤー名チェック
		final Pattern pattern = Pattern.compile("^\\w{2,16}$");
		if (!pattern.matcher(args.get(2)).matches()){
			Actions.message(sender, null, "&cプレイヤー名が不正です！");
			return true;
		}

		// アクションによって処理を分ける
		switch (ac){
			// メンバー追加
			case ADD:
				return add(theme);
			// メンバー削除
			case DEL:
				return del(theme);


			// 定義漏れ
			default:
				Actions.message(sender, null, "&cアクションが不正です。開発者にご連絡ください");
				log.warning(logPrefix+ "Undefined action! Please report this!");
				break;
		}

		return true;
	}

	/* ***** ここから各アクション分岐 ************************ */

	// ADD - メンバー追加
	private boolean add(Theme theme){
		String name = args.get(2);

		// 既に参加状態かチェック
		if (theme.isJoined(name)){
			Actions.message(null, player, "&cそのプレイヤーは既にメンバーになっています！");
			return true;
		}

		theme.addMember(name);
		Actions.message(null, player, "&aプレイヤー '" + name + "' をプロジェクトメンバーに追加しました！");

		return true;
	}

	// DEL - メンバー削除
	private boolean del(Theme theme){
		String name = args.get(2);

		// 参加状態かチェック
		if (!theme.isJoined(name)){
			Actions.message(null, player, "&cそのプレイヤーはこのプロジェクトに参加していません");
			return true;
		}

		theme.remPlayer(name);
		Actions.message(null, player, "&aプレイヤー '" + name + "' をプロジェクトから除名しました！");

		return true;
	}

	/* ***** ここまで***************************************** */

	/**
	 * 実行可能なアクションをsenderに送信する
	 */
	private void sendAvailableAction(){
		List<String> col = new ArrayList<String>();
		for (Action ac : Action.values()){
			col.add(ac.name());
		}

		Actions.message(sender, null, "&6 " + Util.join(col, "/").toLowerCase());
	}

	/**
	 * 実行可能なメンバー管理アクション
	 * @author syam
	 */
	enum Action{
		ADD,	// メンバー追加
		DEL,	// メンバー削除
		;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("theme.user.member");
	}
}
