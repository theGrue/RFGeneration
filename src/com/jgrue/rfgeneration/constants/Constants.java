package com.jgrue.rfgeneration.constants;

public class Constants {
	public static final String BASE_URL = "http://www.rfgeneration.com";
	public static final String LOGIN_COOKIE = "SMFCookie10";
	public static final int TIMEOUT = 30000;
	
	public static final String FUNCTION_LOGIN = BASE_URL + "/forum/index.php?action=login2";
	public static final String FUNCTION_PROFILE = BASE_URL + "/cgi-bin/collection.pl?action=profile"; //&name=<username>
	public static final String FUNCTION_COLLECTION = BASE_URL + "/cgi-bin/collection.pl";
	public static final String FUNCTION_GAME_INFO = BASE_URL + "/cgi-bin/getinfo.pl"; //?ID=<rfgid>
	public static final String FUNCTION_HARDWARE_INFO = BASE_URL + "/PHP/gethwinfo.php"; //?ID=<rfgid>
	public static final String FUNCTION_SEARCH = BASE_URL + "/cgi-bin/search.pl?search=true&inputtype=title"; //&query=<query>
	public static final String FUNCTION_FOLDERS = BASE_URL + "/cgi-bin/collection.pl?action=managefolders";
	public static final String FUNCTION_CSV = BASE_URL + "/cgi-bin/collection.pl?printpage=export"; //&name=<username>&folder=<folder>
	public static final String FUNCTION_IMAGE = BASE_URL + "/images/games/"; // folder/type/rfgid.jpg
	public static final String FUNCTION_SCREENSHOT = BASE_URL + "/cgi-bin/screenshot.pl"; //?ID=<rfgid>
	public static final String FUNCTION_ADD_GAME = FUNCTION_COLLECTION;
	public static final String FUNCTION_EDIT_GAME = BASE_URL + "/cgi-bin/collection.pl?action=edit"; //&folder=<folder>&ID=<rfgid>
	public static final String FUNCTION_DELETE_GAME = BASE_URL + "/cgi-bin/collection.pl?action=delete"; //&folder=<folder>&ID=<rfgid>
	
	public static final String PARAM_USERNAME = "name";
	public static final String PARAM_FOLDER = "folder";
	public static final String PARAM_CONSOLE = "console";
	public static final String PARAM_TYPE = "type";
	public static final String PARAM_FIRST_RESULT = "firstresult";
	public static final String PARAM_RFGID = "ID";
	public static final String PARAM_QUERY = "query";
	
	public static final String PREFS_FILE = "RFGenerationPrefsFile";
	public static final String PREFS_USERNAME = "collectionUsername";
	public static final String PREFS_LOADCOMPLETE = "collectionLoaded";
	public static final String PREFS_LAST_SEARCH = "searchGame";
	public static final String PREFS_COOKIE = "loginCookie";
	
	public static final String COOKIE_USERNAME = "user";
	public static final String COOKIE_PASSWORD = "passwrd";
	
	public static final String INTENT_USERNAME = COOKIE_USERNAME;
	public static final String INTENT_PASSWORD = COOKIE_PASSWORD;
	public static final String INTENT_SEARCH = PARAM_QUERY;
	public static final String INTENT_FOLDER = PARAM_FOLDER;
	public static final String INTENT_GAME_ID = PARAM_RFGID;
	public static final String INTENT_GAME_RFGID = PREFS_LAST_SEARCH;
	public static final String INTENT_GAME_CONSOLE = PARAM_CONSOLE;
	public static final String INTENT_GAME_REGION = "GameRegion";
	public static final String INTENT_GAME_TYPE = PARAM_TYPE;
	public static final String INTENT_GAME_TITLE = "GameTitle";
	public static final String INTENT_GAME_PUBLISHER = "GamePublisher";
	public static final String INTENT_GAME_YEAR = "GameYear";
	public static final String INTENT_GAME_GENRE = "GameGenre";
	public static final String INTENT_CONSOLE_ID = PARAM_CONSOLE;
	public static final String INTENT_TYPE = PARAM_TYPE;
	public static final String INTENT_WEB_URL = "URL";
	public static final String INTENT_WEB_TITLE = "Title";
}
