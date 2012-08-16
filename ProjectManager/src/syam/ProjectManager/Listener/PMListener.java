package syam.ProjectManager.Listener;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

import syam.ProjectManager.ProjectManager;
import syam.ProjectManager.Project.Project;
import syam.ProjectManager.Util.Cuboid;

public class PMListener implements Listener{
	public final static Logger log = ProjectManager.log;
	public final static String logPrefix = ProjectManager.logPrefix;
	public final static String msgPrefix = ProjectManager.msgPrefix;

	private final ProjectManager plugin;

	public PMListener(final ProjectManager plugin){
		this.plugin = plugin;
	}

	/* **************************************** */

	/**
	 * ブロックを破壊した
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockBreak(final BlockBreakEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (!canBuild(block.getLocation(), player)){
			event.setCancelled(true);
		}
	}

	/**
	 * ブロックを設置した
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onBlockPlace(final BlockPlaceEvent event){
		Block block = event.getBlock();
		Player player = event.getPlayer();

		if (!canBuild(block.getLocation(), player)){
			event.setCancelled(true);
		}
	}

	/**
	 * バケツを空にした
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (!canBuild(block.getLocation(), player)){
			event.setCancelled(true);
		}
	}

	/* **************************************** */

	private boolean canBuild(Location loc, Player player){
		boolean inRegion = false;

		// すべてのプロジェクトをチェック
		for (Project project : plugin.projects.values()){
			// エリア未設定のプロジェクトは次を確認
			if (project.getArea() == null) continue;

			Cuboid region = project.getArea();
			// エリア外のプロジェクトは次を確認
			if (!region.isIn(loc)) continue;

			// エリア内 + プロジェクトメンバー なら次を確認
			if (project.isJoined(player.getName())){
				inRegion = true;
				continue;
			}

			// エリア内 + メンバー外 なら建築禁止
			else{
				return false;
			}
		}

		// プロジェクトエリア内なら既に建築可能か確定済み
		if (!inRegion){
			// 保護ワールドかチェック
			if (plugin.getConfigs().protectedWorlds.contains(loc.getWorld().getName())){
				return false;
			}
		}

		// 建築可能
		return true;
	}
}
