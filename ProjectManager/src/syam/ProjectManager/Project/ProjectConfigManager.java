package syam.ProjectManager.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import syam.ProjectManager.ProjectManager;

public class ProjectConfigManager{
	// Logger
	public static final Logger log = ProjectManager.log;
	private static final String logPrefix = ProjectManager.logPrefix;
	private static final String msgPrefix = ProjectManager.msgPrefix;

	private final ProjectManager plugin;
	public ProjectConfigManager(final ProjectManager plugin){
		this.plugin = plugin;
	}
	// 選択中のプロジェクト
	private static Map<String, Project> selectedProject = new HashMap<String, Project>();

	/* getter / setter */

	/**
	 * 指定したプロジェクトを選択中にする
	 * @param player 対象プレイヤー
	 * @param game 対象プロジェクト
	 */
	public static void setSelectedProject(Player player, Project project){
		selectedProject.put(player.getName(), project);
	}

	/**
	 * 選択中のプロジェクトを返す
	 * @param player 対象のプレイヤー
	 * @return null または対象のプロジェクト
	 */
	public static Project getSelectedProject(Player player){
		if (player == null || !selectedProject.containsKey(player.getName())){
			return null;
		}else{
			return selectedProject.get(player.getName());
		}
	}
}
