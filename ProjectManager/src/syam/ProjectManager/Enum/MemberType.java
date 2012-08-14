package syam.ProjectManager.Enum;

/**
 * プロジェクト参加者の権限種類を定めます
 * @author syam
 */
public enum MemberType {
	MEMBER("参加者"),		// 一般参加者
	MANAGER("マネージャ"),	// プロジェクトマネージャ
	;

	private String typeName;

	MemberType(String typeName){
		this.typeName = typeName;
	}

	/**
	 * 参加者の権限種類を返します
	 * @return
	 */
	public String getTypeName(){
		return this.typeName;
	}
}
