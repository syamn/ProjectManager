package syam.ProjectManager.Util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import syam.ProjectManager.ProjectManager;

public class Actions{
	// Logger
	public static final Logger log = ProjectManager.log;
	private static final String logPrefix = ProjectManager.logPrefix;
	private static final String msgPrefix = ProjectManager.msgPrefix;

	private final ProjectManager plugin;

	public Actions(ProjectManager plugin){
		this.plugin = plugin;
	}

	/****************************************/
	// メッセージ送信系関数
	/****************************************/
	/**
	 * メッセージをユニキャスト
	 * @param sender Sender (null可)
	 * @param player Player (null可)l
	 * @param message メッセージ
	 */
	public static void message(CommandSender sender, Player player, String message){
		if (message != null){
			message = message
					.replaceAll("&([0-9a-fk-or])", "\u00A7$1")
					.replaceAll("%version", ProjectManager.getInstance().getDescription().getVersion());
			if (player != null){
				player.sendMessage(message);
			}
			else if (sender != null){
				sender.sendMessage(message);
			}
		}
	}
	/**
	 * メッセージをブロードキャスト
	 * @param message メッセージ
	 */
	public static void broadcastMessage(String message){
		if (message != null){
			message = message
					.replaceAll("&([0-9a-fk-or])", "\u00A7$1")
					.replaceAll("%version", ProjectManager.getInstance().getDescription().getVersion());
			//debug(message);//debug
			Bukkit.broadcastMessage(message);
		}
	}
	/**
	 * メッセージをワールドキャスト
	 * @param world
	 * @param message
	 */
	public static void worldcastMessage(World world, String message){
		if (world != null && message != null){
			message = message
					.replaceAll("&([0-9a-fk-or])", "\u00A7$1")
					.replaceAll("%version", ProjectManager.getInstance().getDescription().getVersion());
			for(Player player: world.getPlayers()){
				player.sendMessage(message);
			}
			log.info("[Worldcast]["+world.getName()+"]: " + message);
		}
	}
	/**
	 * メッセージをパーミッションキャスト(指定した権限ユーザにのみ送信)
	 * @param permission 受信するための権限ノード
	 * @param message メッセージ
	 */
	public static void permcastMessage(String permission, String message){
		// 動かなかった どうして？
		//int i = Bukkit.getServer().broadcast(message, permission);

		// OK
		int i = 0;
		for (Player player : Bukkit.getServer().getOnlinePlayers()){
			if (player.hasPermission(permission)){
				Actions.message(null, player, message);
				i++;
			}
		}

		log.info("Received "+i+"players: "+message);
	}

	/****************************************/
	// ユーティリティ
	/****************************************/
	/**
	 * 文字配列をまとめる
	 * @param s つなげるString配列
	 * @param glue 区切り文字 通常は半角スペース
	 * @return
	 */
	public static String combine(String[] s, String glue)
    {
      int k = s.length;
      if (k == 0){ return null; }
      StringBuilder out = new StringBuilder();
      out.append(s[0]);
      for (int x = 1; x < k; x++){
        out.append(glue).append(s[x]);
      }
      return out.toString();
    }
	/**
	 * コマンドをコンソールから実行する
	 * @param command
	 */
	public static void executeCommandOnConsole(String command){
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
	}
	/**
	 * 文字列の中に全角文字が含まれているか判定
	 * @param s 判定する文字列
	 * @return 1文字でも全角文字が含まれていればtrue 含まれていなければfalse
	 * @throws UnsupportedEncodingException
	 */
	public static boolean containsZen(String s)
			throws UnsupportedEncodingException {
		for (int i = 0; i < s.length(); i++) {
			String s1 = s.substring(i, i + 1);
			if (URLEncoder.encode(s1,"MS932").length() >= 4) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 現在の日時を yyyy-MM-dd HH:mm:ss 形式の文字列で返す
	 * @return
	 */
	public static String getDatetime(){

		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}
	/**
	 * 座標データを ワールド名:x, y, z の形式の文字列にして返す
	 * @param loc
	 * @return
	 */
	public static String getLocationString(Location loc){
		return loc.getWorld().getName()+":"+loc.getX()+","+loc.getY()+","+loc.getZ();
	}
	public static String getBlockLocationString(Location loc){
		return loc.getWorld().getName()+":"+loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
	}
	/**
	 * デバッグ用 syamnがオンラインならメッセージを送る
	 * @param msg
	 */
	public static void debug(String msg){
		OfflinePlayer syamn = Bukkit.getServer().getOfflinePlayer("syamn");
		if (syamn.isOnline()){
			Actions.message(null, (Player) syamn, msg);
		}
	}
	/**
	 * 引数の秒を読みやすい形式の文字列に変換して返す
	 * @param sec 正の秒数
	 * @return 変換後の文字列
	 */
	public static String getTimeString(int sec){
		// 負数は許容しない
		if (sec < 0) return "0秒";

		// 60秒以下はそのまま返す
		if (sec < 60) return sec + "秒";

		// 60秒で割り切れれば分だけを返す
		if (sec % 60 == 0) return sec / 60 + "分";

		// 当て嵌まらなければ n分n秒 で返す
		int m = sec / 60; // 小数点以下は切られるのでこれで問題ないはず..
		int s = sec % 60;
		return m + "分" + s + "秒";
	}


	/****************************************/
	/* ログ操作系 */
	/****************************************/
	/**
	 * ログファイルに書き込み
	 * @param file ログファイル名
	 * @param line ログ内容
	 */
	public static void log(String filepath, String line){
		TextFileHandler r = new TextFileHandler(filepath);
		try{
			r.appendLine("[" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "] " + line);
		} catch (IOException ex) {}
	}

	/****************************************/
	/* その他 */
	/****************************************/
	// プレイヤーがオンラインかチェックしてテレポートさせる
	public static void tpPlayer(Player player, Location loc){
		if (player == null || loc == null || !player.isOnline())
			return;
		player.teleport(loc);
	}

	// プレイヤーのインベントリをその場にドロップさせる
	public static void dropInventoryItems(Player player){
		if (player == null) return;

		PlayerInventory inv = player.getInventory();
		Location loc = player.getLocation();

		// インベントリアイテム
		for (ItemStack i : inv.getContents()) {
			if (i != null && i.getType() != Material.AIR) {
				inv.remove(i);
				player.getWorld().dropItemNaturally(loc, i);
			}
		}

		// 防具アイテム
		for (ItemStack i : inv.getArmorContents()){
			if (i != null && i.getType() != Material.AIR) {
				inv.remove(i);
				player.getWorld().dropItemNaturally(loc, i);
			}
		}
	}

	// オンラインのプレイヤーにだけ色を付けたリストを返す
	public static Set<String> coloringPlayerSet(Set<String> set, String onlinePrefix, String offlinePrefix){
		Set<String> ret = new HashSet<String>();
		ret.clear();

		for (String name : set){
			Player player = Bukkit.getPlayerExact(name);
			if (player == null){
				if (offlinePrefix == null) ret.add(name);
				else ret.add(offlinePrefix + name);
			}else{
				if (onlinePrefix == null) ret.add(name);
				else ret.add(onlinePrefix + name);
			}
		}

		return ret;
	}
}
