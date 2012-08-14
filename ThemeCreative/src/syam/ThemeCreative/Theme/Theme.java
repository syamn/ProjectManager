package syam.ThemeCreative.Theme;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import syam.ThemeCreative.ThemeCreative;
import syam.ThemeCreative.Enum.MemberType;

public class Theme{
	// Logger
	public static final Logger log = ThemeCreative.log;
	private static final String logPrefix = ThemeCreative.logPrefix;
	private static final String msgPrefix = ThemeCreative.msgPrefix;

	// Instance
	private final ThemeCreative plugin;

	/* ***** テーマデータ ***** */
	private String themeID; // 一意なテーマID ログ用
	private String fileName; // テーマデータのファイル名
	private String themeName; // テーマ名
	private String themeTitle; // テーマタイトル

	/* ***** 参加プレイヤー関係 ***** */
	private Map<String, MemberType> playersMap = new ConcurrentHashMap<String, MemberType>();

	/**
	 * コンストラクタ
	 * @param plugin
	 * @param name
	 */
	public Theme(final ThemeCreative plugin, final String name, final String title){
		this.plugin = plugin;

		// テーマデータ設定
		this.themeName = name;
		this.themeTitle = title;

		// ファイル名設定
		this.fileName = this.themeName + ".yml";

		// ゲームをメインクラスに登録
		plugin.themes.put(this.themeName, this);
	}

	/* ***** 参加プレイヤー関係 ***** */
	// メンバー/マネージャ追加
	public void addPlayer(String player, MemberType type){
		playersMap.put(player, type);
	}
	public boolean addPlayer(Player player, MemberType type){
		if (player == null) return false;

		addPlayer(player.getName(), type);
		return true;
	}
	public void addMember(String player){
		addPlayer(player, MemberType.MEMBER);
	}
	public void addManager(String player){
		addPlayer(player, MemberType.MANAGER);
	}

	// 削除
	public void remPlayer(String player){
		if (!isJoined(player)) return;
		playersMap.remove(player);
	}

	// プレイヤーの権限グループを返す
	public MemberType getPlayerType(String player){
		if (!isJoined(player)) return null;

		return playersMap.get(player);
	}
	public Set<String> getPlayersByType(MemberType type){
		if (type == null) return null;

		Set<String> ret = new HashSet<String>();
		ret.clear();

		for (Map.Entry<String, MemberType> entry : playersMap.entrySet()){
			if (entry.getValue().equals(type)){
				ret.add(entry.getKey());
			}
		}

		return ret;
	}


	// isJoined/isManager
	public boolean isJoined(String player){
		if (player == null) return false;

		if (playersMap.containsKey(player))
			return true;
		else
			return false;
	}
	public boolean isManager(String player){
		if (!isJoined(player)) return false;

		if (getPlayerType(player) == MemberType.MANAGER)
			return true;
		else
			return false;
	}

	// PlayerMap getter/setter
	public Map<String, MemberType> getPlayersMap(){
		return this.playersMap;
	}
	public void setPlayersMap(Map<String, MemberType> map){
		playersMap.clear();
		this.playersMap = map;
	}

	/* ***** そのほかの getter / setter ***** */

	public void setFileName(String filename){
		this.fileName = filename;
	}
	public String getFileName(){
		return this.fileName;
	}

	public String getName(){
		return themeName;
	}

	public String getTitle(){
		return themeTitle;
	}

}
