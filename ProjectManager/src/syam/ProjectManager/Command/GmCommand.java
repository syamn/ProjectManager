package syam.ProjectManager.Command;

import java.util.List;

import org.bukkit.GameMode;

import syam.ProjectManager.ProjectManager;
import syam.ProjectManager.Project.Project;
import syam.ProjectManager.Util.Actions;
import syam.ProjectManager.Util.Cuboid;

public class GmCommand extends BaseCommand{
	public GmCommand(){
		bePlayer = true;
		name = "gm";
		argLength = 0;
		usage = "<- toggle gamemode";
	}

	@Override
	public boolean execute() {
		List<Project> joined = plugin.getJoinedProject(player.getName());

		if (joined.size() == 0){
			Actions.message(null, player, "&c参加しているプロジェクトがありません！");
			return true;
		}

		boolean notAllowCreative = false;
		// 参加中のプロジェクトを回す
		for (Project project : joined){
			Cuboid region = project.getArea();
			if (region == null) continue;
			if (region.isIn(player.getLocation())){
				if (!project.getCreative()){
					notAllowCreative = true;
					continue;
				}

				// ゲームモード変更
				switch (player.getGameMode()){
					case SURVIVAL:
						player.setGameMode(GameMode.CREATIVE);
						break;
					case CREATIVE:
						player.setGameMode(GameMode.SURVIVAL);
						break;
					// 他モードは対応外
					case ADVENTURE:
					default:
						Actions.message(null, player, "&cあなたの現在のゲームモードはサポートされていません");
						return true;
				}

				Actions.message(null, player, "&aゲームモードを'&6"+player.getGameMode().name()+"&a'に変更しました！");
				return true;
			}
		}

		if (notAllowCreative){
			if (player.getGameMode() == GameMode.CREATIVE)
				player.setGameMode(GameMode.SURVIVAL);
			Actions.message(null, player, "&cこの地点のプロジェクトはクリエイティブが許可されていません！");
			return true;
		}

		Actions.message(null, player, "&c現在地点は参加しているプロジェクトのエリアではありません！");
		return true;
	}

	@Override
	public boolean permission() {
		return sender.hasPermission("pm.user.gm");
	}
}
