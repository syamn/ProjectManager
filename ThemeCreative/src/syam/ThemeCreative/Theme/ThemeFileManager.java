package syam.ThemeCreative.Theme;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import syam.ThemeCreative.ThemeCreative;

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

			// 保存するデータ
			confFile.set("ThemeName", theme.getName());
			confFile.set("Title", theme.getTitle());


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
				// do stuff..

				log.info(logPrefix+ "Loaded Theme: "+ file.getName()+" ("+name+")");
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

}
