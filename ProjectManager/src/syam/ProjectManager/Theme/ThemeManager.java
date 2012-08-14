package syam.ProjectManager.Theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import syam.ProjectManager.ThemeCreative;

public class ThemeManager{
	// Logger
	public static final Logger log = ThemeCreative.log;
	private static final String logPrefix = ThemeCreative.logPrefix;
	private static final String msgPrefix = ThemeCreative.msgPrefix;

	private final ThemeCreative plugin;
	public ThemeManager(final ThemeCreative plugin){
		this.plugin = plugin;
	}
	// 選択中のテーマ
	private static Map<String, Theme> selectedTheme = new HashMap<String, Theme>();

	/* getter / setter */

	/**
	 * 指定したテーマを選択中にする
	 * @param player 対象プレイヤー
	 * @param game 対象テーマ
	 */
	public static void setSelectedTheme(Player player, Theme theme){
		selectedTheme.put(player.getName(), theme);
	}

	/**
	 * 選択中のテーマを返す
	 * @param player 対象のプレイヤー
	 * @return null または対象のテーマ
	 */
	public static Theme getSelectedTheme(Player player){
		if (player == null || !selectedTheme.containsKey(player.getName())){
			return null;
		}else{
			return selectedTheme.get(player.getName());
		}
	}
}
