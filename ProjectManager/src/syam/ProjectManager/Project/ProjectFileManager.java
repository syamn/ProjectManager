package syam.ProjectManager.Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import syam.ProjectManager.ProjectManager;
import syam.ProjectManager.Enum.MemberType;
import syam.ProjectManager.Util.Actions;
import syam.ProjectManager.Util.Cuboid;

public class ProjectFileManager{
	// Logger
	public static final Logger log = ProjectManager.log;
	private static final String logPrefix = ProjectManager.logPrefix;
	private static final String msgPrefix = ProjectManager.msgPrefix;

	private final ProjectManager plugin;
	public ProjectFileManager(final ProjectManager plugin){
		this.plugin = plugin;
	}

	/* プロジェクトデータ保存/読み出し */
	public void saveProjects(){
		FileConfiguration confFile = new YamlConfiguration();
		String fileDir = plugin.getDataFolder() + System.getProperty("file.separator") +
				"projectData" + System.getProperty("file.separator");

		for (Project project : plugin.projects.values()){
			File file = new File(fileDir + project.getID() + ".yml");

			// マップデータをリストに変換
			List<String> playerList = convertPlayerMap(project.getPlayersMap());

			// 保存するデータ
			confFile.set("ProjectName", project.getID());
			confFile.set("Title", project.getTitle());
			confFile.set("Creative", project.getCreative());
			confFile.set("WarpLocation", convertPlayerLocation(project.getWarpLocation()));
			confFile.set("Region", convertCuboid(project.getArea()));

			confFile.set("Players", playerList);

			try{
				confFile.save(file);
			}catch (IOException ex){
				log.warning(logPrefix+ "Couldn't write project data!");
				ex.printStackTrace();
			}
		}
	}

	public void loadProjects(){
		FileConfiguration confFile = new YamlConfiguration();
		String fileDir = plugin.getDataFolder() + System.getProperty("file.separator") + "projectData";

		File dir = new File(fileDir);
		File[] files = dir.listFiles();

		// プロジェクトデータクリア
		plugin.projects.clear();

		// ファイルなし
		if (files == null || files.length == 0)
			return;

		// データ取得
		String name, title;
		for (File file : files){
			try{
				confFile.load(file);

				// 読むデータキー
				name = confFile.getString("ProjectName", null);
				title = confFile.getString("Title", null);

				// プロジェクト追加
				Project project = new Project(plugin, name, title);

				// ファイル名設定
				project.setFileName(file.getName());

				// 各設定データを追加
				project.setCreative(confFile.getBoolean("Creative", false));
				project.setWarpLocation(convertPlayerLocation(confFile.getString("WarpLocation", null)));
				project.setArea(convertCuboid(confFile.getString("Region")));

				project.setPlayersMap(convertPlayerMap(confFile.getStringList("Players")));

				log.info(logPrefix+ "Loaded Theme: "+ file.getName()+" ("+name+")");
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

	/* 各種データ変換 */
	private List<String> convertPlayerMap(Map<String, MemberType> map){
		List<String> ret = new ArrayList<String>();
		ret.clear();

		for (Map.Entry<String, MemberType> entry : map.entrySet()){
			// memberType@playerName リスト追加
			ret.add(entry.getValue().name() + "@" + entry.getKey());
		}

		return ret;
	}

	private Map<String, MemberType> convertPlayerMap(List<String> list){
		Map<String, MemberType> ret = new HashMap<String, MemberType>();
		ret.clear();

		String[] data;

		int line = 0;
		for (String s : list){
			line++;
			// デリミタ分割
			data = s.split("@");
			if (data.length != 2){
				log.warning(logPrefix+ "Skipping PlayerLine "+line+": incorrect format(@)");
				continue;
			}

			// data[0] : タイプチェック
			MemberType type = null;
			for (MemberType mt : MemberType.values()){
				if (mt.name().equalsIgnoreCase(data[0])){
					type = mt;
				}
			}
			if (type == null){
				log.warning(logPrefix+ "Skipping PlayerLine "+line+": undefined MemberType");
				continue;
			}

			// data[1] : プレイヤー名チェック
			final Pattern pattern = Pattern.compile("^\\w{2,16}$");
			if (!pattern.matcher(data[1]).matches()){
				log.warning(logPrefix+ "Skipping PlayerLine "+line+": incorrect PlayerName");
				continue;
			}

			ret.put(data[1], type);
		}
		return ret;
	}

	// プレイヤーのLocationオブジェクトから文字列に変換
	private String convertPlayerLocation(Location loc){
		if (loc == null) return null;
		return loc.getWorld().getName()+","+loc.getX()+","+loc.getY()+","+loc.getZ()+","+loc.getYaw()+","+loc.getPitch();
	}
	// convertPlayerLocationToStringで変換したプレイヤーLocationに戻す
	private Location convertPlayerLocation(String loc){
		if (loc == null) return null;
		String[] coord = loc.split(",");
		if (coord.length != 6) return null;
		World world = Bukkit.getWorld(coord[0]);
		if (world == null) return null;
		return new Location(
				world,
				Double.valueOf(coord[1]),
				Double.valueOf(coord[2]),
				Double.valueOf(coord[3]),
				Float.valueOf(coord[4]),
				Float.valueOf(coord[5])
				);
	}

	private String convertCuboid(Cuboid region){
		if (region == null) return null;

		Location pos1 = region.getPos1();
		Location pos2 = region.getPos2();

		if (!pos1.getWorld().equals(pos2.getWorld()))
			return null;

		return convertBlockLocation(pos1) + "@" + convertBlockLocation(pos2);
	}
	private Cuboid convertCuboid(String region){
		if (region == null) return null;

		String[] data = region.split("@");
		if (data.length != 2) return null;

		Location loc1 = convertBlockLocation(data[0]);
		Location loc2 = convertBlockLocation(data[1]);

		if (loc1 == null || loc2 == null)
			return null;

		if (!loc1.getWorld().equals(loc2.getWorld()))
			return null;

		return new Cuboid(loc1, loc2);
	}

	private String convertBlockLocation(Location loc){
		if (loc == null) return null;
		return loc.getWorld().getName()+","+loc.getX()+","+loc.getY()+","+loc.getZ();
	}
	private Location convertBlockLocation(String loc){
		if (loc == null) return null;
		String[] coord = loc.split(",");
		if (coord.length != 4) return null;
		World world = Bukkit.getWorld(coord[0]);
		if (world == null) return null;
		return new Location(
				world,
				Double.valueOf(coord[1]),
				Double.valueOf(coord[2]),
				Double.valueOf(coord[3])
				);
	}
}
