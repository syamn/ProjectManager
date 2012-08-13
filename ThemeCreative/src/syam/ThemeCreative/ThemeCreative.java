package syam.ThemeCreative;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import syam.ThemeCreative.Command.BaseCommand;
import syam.ThemeCreative.Command.CreateCommand;
import syam.ThemeCreative.Command.HelpCommand;
import syam.ThemeCreative.Command.SelectCommand;
import syam.ThemeCreative.Theme.Theme;
import syam.ThemeCreative.Theme.ThemeManager;

public class ThemeCreative extends JavaPlugin{
	// ** Logger **
	public final static Logger log = Logger.getLogger("Minecraft");
	public final static String logPrefix = "[ThemeCreative] ";
	public final static String msgPrefix = "&6[ThemeCreative] &f";

	// ** Listener **

	// ** Private Classes **
	private ConfigurationManager config;
	private ThemeManager tm;

	// ** Commands **
	public static List<BaseCommand> commands = new ArrayList<BaseCommand>();

	// ** Variable **
	// 存在するテーマ <String 一意のテーマID, Theme>
	public HashMap<String, Theme> themes = new HashMap<String, Theme>();

	// ** Instance **
	private static ThemeCreative instance;

	/**
	 * プラグイン起動処理
	 */
	public void onEnable(){
		instance  = this;
		config = new ConfigurationManager(this);
		PluginManager pm = getServer().getPluginManager();

		// 設定読み込み
		try{
			config.loadConfig(true);
		}catch(Exception ex){
			log.warning(logPrefix+ "an error occured while trying to load the config file.");
			ex.printStackTrace();
		}

		// Register Listeners

		// コマンド登録
		registerCommands();

		// マネージャ
		tm = new ThemeManager(this);

		// メッセージ表示
		PluginDescriptionFile pdfFile=this.getDescription();
		log.info("["+pdfFile.getName()+"] version "+pdfFile.getVersion()+" is enabled!");
	}

	/**
	 * プラグイン停止処理
	 */
	public void onDisable(){
		// メッセージ表示
		PluginDescriptionFile pdfFile=this.getDescription();
		log.info("["+pdfFile.getName()+"] version "+pdfFile.getVersion()+" is disabled!");
	}

	/**
	 * コマンドを登録
	 */
	private void registerCommands(){
		// Intro Commands
		commands.add(new HelpCommand());

		// Start Commands

		// Admin Commands
		commands.add(new SelectCommand());
		commands.add(new CreateCommand());

	}

	/**
	 * コマンドが呼ばれた
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]){
		if (cmd.getName().equalsIgnoreCase("theme")){
			if(args.length == 0){
				// 引数ゼロはヘルプ表示
				args = new String[]{"help"};
			}

			outer:
			for (BaseCommand command : commands.toArray(new BaseCommand[0])){
				String[] cmds = command.name.split(" ");
				for (int i = 0; i < cmds.length; i++){
					if (i >= args.length || !cmds[i].equalsIgnoreCase(args[i])){
						continue outer;
					}
					// 実行
					return command.run(this, sender, args, commandLabel);
				}
			}
			// 有効コマンドなし ヘルプ表示
			new HelpCommand().run(this, sender, args, commandLabel);
			return true;
		}
		return false;
	}

	/* getter */

	/**
	 * テーマを返す
	 * @param themeName
	 * @return Theme
	 */
	public Theme getTheme(String themeName){
		if (!themes.containsKey(themeName)){
			return null;
		}else{
			return themes.get(themeName);
		}
	}

	/**
	 * テーママネージャを返す
	 * @return
	 */
	public ThemeManager getManager(){
		return tm;
	}

	/**
	 * 設定マネージャを返す
	 * @return ConfigurationManager
	 */
	public ConfigurationManager getConfigs(){
		return config;
	}

	/**
	 * インスタンスを返す
	 * @return ThemeCreativeインスタンス
	 */
	public static ThemeCreative getInstance(){
		return instance;
	}
}
