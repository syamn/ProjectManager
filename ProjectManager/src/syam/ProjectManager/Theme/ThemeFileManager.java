package syam.ProjectManager.Theme;

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

import syam.ProjectManager.ThemeCreative;
import syam.ProjectManager.Enum.MemberType;
import syam.ProjectManager.Util.Actions;

public class ThemeFileManager{
	// Logger
	public static final Logger log = ThemeCreative.log;
	private static final String logPrefix = ThemeCreative.logPrefix;
	private static final String msgPrefix = ThemeCreative.msgPrefix;

	private final ThemeCreative plugin;
	public ThemeFileManager(final ThemeCreative plugin){
		this.plugin = plugin;
	}

	/* テーマデータ保存/読み出し */
	public void saveThemes(){
		FileConfiguration confFile = new YamlConfiguration();
		String fileDir = plugin.getDataFolder() + System.getProperty("file.separator") +
				"themeData" + System.getProperty("file.separator");

		for (Theme theme : plugin.themes.values()){
			File file = new File(fileDir + theme.getName() + ".yml");

			// マップデータをリストに変換
			List<String> playerList = convertPlayerMap(theme.getPlayersMap());

			// 保存するデータ
			confFile.set("ThemeName", theme.getName());
			confFile.set("Title", theme.getTitle());
			confFile.set("WarpLocation", convertPlayerLocation(theme.getWarpLocation()));

			confFile.set("Players", playerList);

			try{
				confFile.save(file);
			}catch (IOException ex){
				log.warning(logPrefix+ "Couldn't write theme data!");
				ex.printStackTrace();
			}
		}
	}

	public void loadThemes(){
		FileConfiguration confFile = new YamlConfiguration();
		String fileDir = plugin.getDataFolder() + System.getProperty("file.separator") + "themeData";

		File dir = new File(fileDir);
		File[] files = dir.listFiles();

		// テーマデータクリア
		plugin.themes.clear();

		// ファイルなし
		if (files == null || files.length == 0)
			return;

		// データ取得
		String name, title;
		for (File file : files){
			try{
				confFile.load(file);

				// 読むデータキー
				name = confFile.getString("ThemeName", null);
				title = confFile.getString("Title", null);

				// テーマ追加
				Theme theme = new Theme(plugin, name, title);

				// ファイル名設定
				theme.setFileName(file.getName());

				// 各設定データを追加
				theme.setWarpLocation(convertPlayerLocation(confFile.getString("WarpLocation", null)));
				theme.setPlayersMap(convertPlayerMap(confFile.getStringList("Players")));

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
}
