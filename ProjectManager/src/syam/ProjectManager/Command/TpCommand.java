package syam.ProjectManager.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import syam.ProjectManager.Project.Project;
import syam.ProjectManager.Util.Actions;
import syam.ProjectManager.Util.Cuboid;

public class TpCommand extends BaseCommand{
	public TpCommand(){
		bePlayer = true;
		name = "tp";
		argLength = 1;
		usage = "[player] <- tp to same project players";
	}

	@Override
	public boolean execute() {
		List<Project> joined = plugin.getJoinedProject(player.getName());

		if (joined.size() == 0){
			Actions.message(null, player, "&c参加しているプロジェクトがありません！");
			return true;
		}

		Player target = Bukkit.getPlayer(args.get(0));
		if (target == null){
			Actions.message(null, player, "&cプレイヤー "+args.get(0)+" が見つかりません！");
			return true;
		}

		if (player.equals(target)){
			Actions.message(null, player, "&c自分自身にテレポートすることはできません！");
			return true;
		}

		// ターゲットプレイヤーが居る位置の参加中プロジェクトを取得する
		List<Project> tJoined = plugin.getJoinedProject(target.getName());
		List<Project> tIsIn = new ArrayList<Project>();
		tIsIn.clear();
		for (Project p : tJoined){
			Cuboid region = p.getArea();
			if (region == null) continue;
			if (region.isIn(target.getLocation()))
				tIsIn.add(p);
		}

		if (tIsIn.size() == 0){
			Actions.message(null, player, "&cプレイヤー "+args.get(0)+" は現在プロジェクトエリアにいません！");
			return true;
		}

		// 自分の所蔵するプロジェクトと同じか確認する
		boolean allow = false;
		for (Project p : tIsIn){
			for (Project project2 : joined){
				if (p.getID().equals(project2.getID()))
					allow = true;
			}
		}

		// テレポート
		if (allow){
			player.teleport(target.getLocation(), TeleportCause.PLUGIN);
			if (player.getGameMode() != GameMode.SURVIVAL)
				player.setGameMode(GameMode.SURVIVAL);
			Actions.message(null, player, "&aプレイヤー '&6"+target.getName()+"&a' にテレポートしました！");
		}else{
			Actions.message(null, player, "&cプレイヤー '&6"+target.getName()+"&a' が現在いる地点のプロジェクトに参加していません！");
		}
		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("pm.user.tp");
	}
}
