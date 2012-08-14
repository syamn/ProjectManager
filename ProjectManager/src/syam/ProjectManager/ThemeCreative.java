package syam.ProjectManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import syam.ProjectManager.Command.BaseCommand;
import syam.ProjectManager.Command.CreateCommand;
import syam.ProjectManager.Command.DeleteCommand;
import syam.ProjectManager.Command.HelpCommand;
import syam.ProjectManager.Command.ListCommand;
import syam.ProjectManager.Command.MemberCommand;
import syam.ProjectManager.Command.SelectCommand;
import syam.ProjectManager.Command.SetCommand;
import syam.ProjectManager.Theme.Theme;
import syam.ProjectManager.Theme.ThemeFileManager;
import syam.ProjectManager.Theme.ThemeManager;

public class ThemeCreative extends JavaPlugin{
	// ** Logger **
	public final static Logger log = Logger.getLogger("Minecraft");
	public final static String logPrefix = "[ThemeCreative] ";
	public final static String msgPrefix = "&6[ThemeCreative] &f";

	// ** Listener **

	// ** Private Classes **
	private ConfigurationManager config;
	private ThemeManager tm;
	private ThemeFileManager tfm;

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

		// プラグインを無効にした場合進まないようにする
		if (!pm.isPluginEnabled(this)){
			return;
		}

		// Register Listeners

		// コマンド登録
		registerCommands();

		// マネージャ
		tm = new ThemeManager(this);
		tfm = new ThemeFileManager(this);

		// テーマ読み込み
		tfm.loadThemes();

		// メッセージ表示
		PluginDescriptionFile pdfFile=this.getDescription();
		log.info("["+pdfFile.getName()+"] version "+pdfFile.getVersion()+" is enabled!");
	}

	/**
	 * プラグイン停止処理
	 */
	public void onDisable(){
		// テーマ保存
		if (tfm != null){
			tfm.saveThemes();
		}

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

		// General Commands
		commands.add(new MemberCommand());
		commands.add(new SetCommand());
		commands.add(new ListCommand());

		// Admin Commands
		commands.add(new SelectCommand());
		commands.add(new CreateCommand());
		commands.add(new DeleteCommand());

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
	 * プレイヤーが参加しているテーマを返す
	 * @param player
	 * @return
	 */
	public List<Theme> getJoinedTheme(String player){
		List<Theme> joined = new ArrayList<Theme>();
		joined.clear();

		for (Theme theme : themes.values()){
			if (theme.isJoined(player))
				joined.add(theme);
		}

		return joined;
	}

	/**
	 * テーママネージャを返す
	 * @return
	 */
	public ThemeManager getManager(){
		return tm;
	}

	/**
	 * テーマファイルマネージャを帰す
	 * @return
	 */
	public ThemeFileManager getFileManager(){
		return tfm;
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
