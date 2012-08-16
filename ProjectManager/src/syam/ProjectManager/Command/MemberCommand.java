package syam.ProjectManager.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import syam.ProjectManager.Enum.MemberType;
import syam.ProjectManager.Project.Project;
import syam.ProjectManager.Project.ProjectConfigManager;
import syam.ProjectManager.Util.Actions;
import syam.ProjectManager.Util.Util;

public class MemberCommand extends BaseCommand{
	public MemberCommand(){
		bePlayer = true;
		name = "member";
		argLength = 0;
		usage = "<action> [player] <- manage project member";
	}

	/**
	 * コマンド実行時に呼ばれる
	 */
	@Override
	public boolean execute() {
		// プロジェクト取得
		Project project = ProjectConfigManager.getSelectedProject(player);
		if (project == null){
			Actions.message(null, player, "&c先に管理するプロジェクトを選択してください");
			return true;
		}

		// 引数チェック
		if (args.size() <= 0){
			Actions.message(sender, null, "&c実行する管理アクションを指定してください");
			sendAvailableAction();
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

		// Check Permission

		// プロジェクトマネージャ または管理権限を持っているプレイヤー以外からの設定を拒否
		if (ac.getNeedManager() && (!project.isManager(player.getName()) && !player.hasPermission("pm.admin.editAllProject"))){
			Actions.message(sender, null, "&cこの操作を行うにはマネージャ権限が必要です！");
			return true;
		}

		// Permission OK

		// アクションによって処理を分ける
		switch (ac){
			// メンバーリスト表示
			case LIST:
				return list(project);
			// メンバー追加
			case ADD:
				return add(project);
			// メンバー削除
			case DEL:
				return del(project);
			// マネージャにする
			case PROMOTION:
				return promotion(project);
			// マネージャを解除する
			case DEMOTION:
				return demotion(project);

			// 定義漏れ
			default:
				Actions.message(sender, null, "&cアクションが不正です。開発者にご連絡ください");
				log.warning(logPrefix+ "Undefined action! Please report this!");
				break;
		}

		return true;
	}

	/* ***** ここから各アクション分岐 ************************ */

	// LIST - メンバーリスト表示
	private boolean list(Project project){
		// 人数出力
		if (project.getPlayersMap().size() < 1){
			Actions.message(null, player,"&6このプロジェクトに参加しているプレイヤーはいません");
			return true;
		}else{
			Actions.message(null, player,"&6このプロジェクトには &c"+project.getPlayersMap().size()+"人 &6のプレイヤーが参加しています");
		}

		// マネージャ出力
		if (project.getPlayersByType(MemberType.MANAGER).size() >= 1){
			Actions.message(null, player,"マネージャ: " + Util.join(project.getPlayersByType(MemberType.MANAGER), ", "));
		}
		// 一般メンバー出力
		if (project.getPlayersByType(MemberType.MEMBER).size() >= 1){
			Actions.message(null, player,"メンバー: " + Util.join(project.getPlayersByType(MemberType.MEMBER), ", "));
		}

		return true;
	}

	// ADD - メンバー追加
	private boolean add(Project project){
		// プレイヤー名チェック
		if(args.size() == 1){
			Actions.message(sender, null, "&cアクションを行う対象のプレイヤー名を入力してください");
			return true;
		}
		final Pattern pattern = Pattern.compile("^\\w{2,16}$");
		if (!pattern.matcher(args.get(1)).matches()){
			Actions.message(sender, null, "&cプレイヤー名が不正です！");
			return true;
		}

		String name = args.get(1);

		// 既に参加状態かチェック
		if (project.isJoined(name)){
			Actions.message(null, player, "&cそのプレイヤーは既にメンバーになっています！");
			return true;
		}

		project.addMember(name);
		Actions.message(null, player, "&aプレイヤー '" + name + "' をプロジェクトメンバーに追加しました！");

		return true;
	}

	// DEL - メンバー削除
	private boolean del(Project project){
		// プレイヤー名チェック
		if(args.size() == 1){
			Actions.message(sender, null, "&cアクションを行う対象のプレイヤー名を入力してください");
			return true;
		}
		final Pattern pattern = Pattern.compile("^\\w{2,16}$");
		if (!pattern.matcher(args.get(1)).matches()){
			Actions.message(sender, null, "&cプレイヤー名が不正です！");
			return true;
		}

		String name = args.get(1);

		// 参加状態かチェック
		if (!project.isJoined(name)){
			Actions.message(null, player, "&cそのプレイヤーはこのプロジェクトに参加していません");
			return true;
		}

		if (name.equals(player.getName())){
			Actions.message(null, player, "&c自分をメンバーから削除することはできません");
			return true;
		}

		project.remPlayer(name);
		Actions.message(null, player, "&aプレイヤー '" + name + "' をプロジェクトから除名しました！");

		return true;
	}

	// PROMOTION - マネージャにする
	private boolean promotion(Project project){
		// プレイヤー名チェック
		if(args.size() == 1){
			Actions.message(sender, null, "&cアクションを行う対象のプレイヤー名を入力してください");
			return true;
		}
		final Pattern pattern = Pattern.compile("^\\w{2,16}$");
		if (!pattern.matcher(args.get(1)).matches()){
			Actions.message(sender, null, "&cプレイヤー名が不正です！");
			return true;
		}

		String name = args.get(1);

		if (project.isManager(name)){
			Actions.message(sender, null, "&cプレイヤー'"+name+"'は既にマネージャーになっています");
			return true;
		}
		if (!project.isJoined(name)){
			Actions.message(sender, null, "&cプレイヤー'"+name+"'はプロジェクトに参加していません");
			return true;
		}

		project.addManager(name);
		Actions.message(null, player, "&aプレイヤー '" + name + "' をプロジェクトマネージャに昇格させました！");

		return true;
	}

	// DEMOTION - マネージャを解除する
	private boolean demotion(Project project){
		// プレイヤー名チェック
		if(args.size() == 1){
			Actions.message(sender, null, "&cアクションを行う対象のプレイヤー名を入力してください");
			return true;
		}
		final Pattern pattern = Pattern.compile("^\\w{2,16}$");
		if (!pattern.matcher(args.get(1)).matches()){
			Actions.message(sender, null, "&cプレイヤー名が不正です！");
			return true;
		}

		String name = args.get(1);

		if (project.isJoined(name) && !project.isManager(name)){
			Actions.message(sender, null, "&cプレイヤー'"+name+"'はマネージャーではありません");
			return true;
		}
		if (!project.isJoined(name)){
			Actions.message(sender, null, "&cプレイヤー'"+name+"'はプロジェクトに参加していません");
			return true;
		}
		if (name.equals(player.getName())){
			Actions.message(null, player, "&c自分を降格させることはできません");
			return true;
		}

		project.addMember(name);
		Actions.message(null, player, "&aプレイヤー '" + name + "' をプロジェクトマネージャから降格させました！");

		return true;
	}


	/* ***** ここまで ***************************************** */

	/**
	 * 実行可能なアクションをsenderに送信する
	 */
	private void sendAvailableAction(){
		List<String> col = new ArrayList<String>();
		for (Action ac : Action.values()){
			col.add(ac.name());
		}

		Actions.message(sender, null, "&6 " + Util.join(col, ", ").toLowerCase());
	}

	/**
	 * 実行可能なメンバー管理アクション
	 * @author syam
	 */
	enum Action{
		LIST(false),	// メンバーリスト表示
		ADD(true),		// メンバー追加
		DEL(true),		// メンバー削除
		PROMOTION(true),// マネージャにする
		DEMOTION(true),	// マネージャを解除する
		;

		private boolean needManager;

		Action(boolean checkManager){
			this.needManager = checkManager;
		}

		public boolean getNeedManager(){
			return this.needManager;
		}
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("pm.user.member");
	}
}
