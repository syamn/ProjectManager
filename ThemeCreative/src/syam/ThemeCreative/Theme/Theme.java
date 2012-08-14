package syam.ThemeCreative.Theme;

import java.util.logging.Logger;

import syam.ThemeCreative.ThemeCreative;

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
	private boolean ready = false; // 待機状態フラグ
	private boolean started = false; // 開始状態フラグ

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


	/* ***** getter / setter ***** */

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
