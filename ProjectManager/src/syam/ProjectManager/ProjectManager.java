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
import syam.ProjectManager.Command.TpCommand;
import syam.ProjectManager.Project.Project;
import syam.ProjectManager.Project.ProjectConfigManager;
import syam.ProjectManager.Project.ProjectFileManager;

public class ProjectManager extends JavaPlugin{
	// ** Logger **
	public final static Logger log = Logger.getLogger("Minecraft");
	public final static String logPrefix = "[ProjectManager] ";
	public final static String msgPrefix = "&6[ProjectManager] &f";

	// ** Listener **

	// ** Private Classes **
	private ConfigurationManager config;
	private ProjectConfigManager pcm;
	private ProjectFileManager pfm;

	// ** Commands **
	public static List<BaseCommand> commands = new ArrayList<BaseCommand>();

	// ** Variable **
	// 存在するプロジェクト <String 一意のプロジェクトID, Project>
	public HashMap<String, Project> projects = new HashMap<String, Project>();

	// ** Instance **
	private static ProjectManager instance;

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
		pcm = new ProjectConfigManager(this);
		pfm = new ProjectFileManager(this);

		// テーマ読み込み
		pfm.loadProjects();

		// メッセージ表示
		PluginDescriptionFile pdfFile=this.getDescription();
		log.info("["+pdfFile.getName()+"] version "+pdfFile.getVersion()+" is enabled!");
	}

	/**
	 * プラグイン停止処理
	 */
	public void onDisable(){
		// テーマ保存
		if (pfm != null){
			pfm.saveProjects();
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
		commands.add(new TpCommand());

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
		if (cmd.getName().equalsIgnoreCase("project")){
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
	 * プロジェクトを返す
	 * @param projectName
	 * @return Project
	 */
	public Project getProject(String projectName){
		if (!projects.containsKey(projectName)){
			return null;
		}else{
			return projects.get(projectName);
		}
	}

	/**
	 * プレイヤーが参加しているプロジェクトを返す
	 * @param player
	 * @return
	 */
	public List<Project> getJoinedProject(String player){
		List<Project> joined = new ArrayList<Project>();
		joined.clear();

		for (Project project : projects.values()){
			if (project.isJoined(player))
				joined.add(project);
		}

		return joined;
	}

	/**
	 * プロジェクト設定マネージャを返す
	 * @return
	 */
	public ProjectConfigManager getManager(){
		return pcm;
	}

	/**
	 * テーマファイルマネージャを帰す
	 * @return
	 */
	public ProjectFileManager getFileManager(){
		return pfm;
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
	 * @return ProjectManagerインスタンス
	 */
	public static ProjectManager getInstance(){
		return instance;
	}
}
