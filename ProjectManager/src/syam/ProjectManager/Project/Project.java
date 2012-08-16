package syam.ProjectManager.Project;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import syam.ProjectManager.ProjectManager;
import syam.ProjectManager.Enum.MemberType;

public class Project{
	// Logger
	public static final Logger log = ProjectManager.log;
	private static final String logPrefix = ProjectManager.logPrefix;
	private static final String msgPrefix = ProjectManager.msgPrefix;

	// Instance
	private final ProjectManager plugin;

	/* ***** プロジェクトデータ ***** */
	private String fileName; // プロジェクトデータのファイル名
	private String projectID; // プロジェクト名
	private String projectTitle; // プロジェクトタイトル
	private boolean creative; // 参加者にクリエイティブを許可するか

	// 参加プレイヤー
	private Map<String, MemberType> playersMap = new ConcurrentHashMap<String, MemberType>();
	// ワープ地点
	private Location warpLoc = null;

	/**
	 * コンストラクタ
	 * @param plugin
	 * @param name
	 */
	public Project(final ProjectManager plugin, final String name, final String title){
		this.plugin = plugin;

		// プロジェクトデータ設定
		this.projectID = name;
		this.projectTitle = title;

		// ファイル名設定
		this.fileName = this.projectID + ".yml";

		// ゲームをメインクラスに登録
		plugin.projects.put(this.projectID, this);
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

	/* ワープ地点 */
	public void setWarpLocation(Location loc){
		this.warpLoc = loc;
	}
	public Location getWarpLocation(){
		return this.warpLoc;
	}


	/* ***** そのほかの getter / setter ***** */

	public void setFileName(String filename){
		this.fileName = filename;
	}
	public String getFileName(){
		return this.fileName;
	}

	public String getID(){
		return projectID;
	}

	public String getTitle(){
		return projectTitle;
	}


}
