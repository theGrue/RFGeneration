package com.jgrue.rfgeneration.data;

import static android.provider.BaseColumns._ID;
import android.content.Context;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RFGenerationData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "rfgeneration.db";
	private static final int DATABASE_VERSION = 1;
	
	public RFGenerationData(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create "consoles" table
		db.execSQL("CREATE TABLE consoles (" + _ID + " INTEGER PRIMARY KEY, " +
				"console_name TEXT NOT NULL, console_abbv TEXT NOT NULL);");
		
		insertConsoles(db);
		
		// Create "folders" table
		db.execSQL("CREATE TABLE folders (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"folder_name TEXT NOT NULL, is_owned INTEGER NOT NULL, is_for_sale INTEGER NOT NULL, " + 
			"is_private INTEGER NOT NULL, last_load INT NOT NULL);");
			
		// games - console_id should be nullable, save console name for each game.
		db.execSQL("CREATE TABLE games (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"rfgid TEXT NOT NULL UNIQUE, console_id INT NOT NULL, console_name TEXT NOT NULL, " +
				"region_id TEXT NOT NULL, region TEXT NOT NULL, type TEXT NOT NULL, " +
				"title TEXT NOT NULL, publisher TEXT NOT NULL, year INT, genre TEXT NOT NULL);");
		
		// Create "collection" table
		db.execSQL("CREATE TABLE collection (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"folder_id INT NOT NULL, game_id INT NOT NULL, qty INT NOT NULL, " +
				"box INT NOT NULL, man INT NOT NULL);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	private void insertConsoles(SQLiteDatabase db) {
		// Create a single InsertHelper to handle this set of insertions.
		InsertHelper ih = new InsertHelper(db, "consoles");
        
        // Get the numeric indexes for each of the columns that we're updating.
		final int id = ih.getColumnIndex(_ID);
        final int name = ih.getColumnIndex("console_name");
        final int abbv = ih.getColumnIndex("console_abbv");
        
        // Do lots of inserts.
        ih.prepareForInsert(); ih.bind(id, 1); ih.bind(name, "Magnavox Odyssey"); ih.bind(abbv, "OD"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 2); ih.bind(name, "Fairchild Channel F"); ih.bind(abbv, "ChF"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 3); ih.bind(name, "Apple II / Apple III"); ih.bind(abbv, "AIIe"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 4); ih.bind(name, "Tandy TRS-80 / Color Computer"); ih.bind(abbv, "TRS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 5); ih.bind(name, "Atari 2600"); ih.bind(abbv, "2600"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 6); ih.bind(name, "RCA Studio II / MPT-02"); ih.bind(abbv, "StII"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 7); ih.bind(name, "Coleco Telstar Arcade"); ih.bind(abbv, "Tlst"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 8); ih.bind(name, "Bally Professional Arcade / Astrocade"); ih.bind(abbv, "Astr"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 9); ih.bind(name, "Magnavox Odyssey^2 / VideoPac"); ih.bind(abbv, "O2"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 10); ih.bind(name, "APF M1000 / MP1000"); ih.bind(abbv, "APF"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 11); ih.bind(name, "Milton Bradley MicroVision"); ih.bind(abbv, "MV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 13); ih.bind(name, "Mattel Intellivision"); ih.bind(abbv, "INTV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 14); ih.bind(name, "Commodore VIC-20"); ih.bind(abbv, "VIC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 15); ih.bind(name, "Texas Instruments TI-99/4A"); ih.bind(abbv, "TI99"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 16); ih.bind(name, "IBM PC"); ih.bind(abbv, "PC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 17); ih.bind(name, "GCE Vectrex / Bandai Kousokusen"); ih.bind(abbv, "Vec"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 18); ih.bind(name, "Commodore 64 / 128"); ih.bind(abbv, "C64"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 19); ih.bind(name, "Entex Adventure Vision"); ih.bind(abbv, "AV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 20); ih.bind(name, "Sinclair ZX80 / ZX81 / Timex Sinclair 1000"); ih.bind(abbv, "ZX81"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 21); ih.bind(name, "Mattel Aquarius"); ih.bind(abbv, "AQ"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 22); ih.bind(name, "Apple Macintosh"); ih.bind(abbv, "Mac"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 23); ih.bind(name, "Atari 5200"); ih.bind(abbv, "5200"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 24); ih.bind(name, "Coleco / CBS ColecoVision"); ih.bind(abbv, "CV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 25); ih.bind(name, "Coleco ADAM"); ih.bind(abbv, "ADAM"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 26); ih.bind(name, "Emerson Arcadia 2001 / MPT-03 / Palladium"); ih.bind(abbv, "Arcd"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 27); ih.bind(name, "Nintendo NES / Famicom"); ih.bind(abbv, "NES"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 28); ih.bind(name, "RDI Halcyon"); ih.bind(abbv, "Halc"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 29); ih.bind(name, "Atari ST / TT / Falcon"); ih.bind(abbv, "AST"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 30); ih.bind(name, "Commodore Amiga"); ih.bind(abbv, "Amig"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 31); ih.bind(name, "Sega Master System / Mark III"); ih.bind(abbv, "SMS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 32); ih.bind(name, "Atari 7800"); ih.bind(abbv, "7800"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 33); ih.bind(name, "Worlds of Wonder Action Max"); ih.bind(abbv, "AMax"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 34); ih.bind(name, "Atari 8-bit Family"); ih.bind(abbv, "AT8F"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 35); ih.bind(name, "LJN Video Art"); ih.bind(abbv, "VArt"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 36); ih.bind(name, "Mattel Captain Power"); ih.bind(abbv, "CPwr"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 37); ih.bind(name, "Nintendo Game Boy"); ih.bind(abbv, "GB"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 38); ih.bind(name, "Atari Lynx"); ih.bind(abbv, "Lynx"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 39); ih.bind(name, "NEC PC Engine / TurboGrafx-16"); ih.bind(abbv, "PCE"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 40); ih.bind(name, "Sega Genesis / Mega Drive"); ih.bind(abbv, "GEN/MD"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 41); ih.bind(name, "NEC PC Engine CD / TurboGrafx CD"); ih.bind(abbv, "PCED"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 42); ih.bind(name, "View-Master Interactive Vision"); ih.bind(abbv, "VMIV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 43); ih.bind(name, "SNK Neo Geo AES"); ih.bind(abbv, "AES"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 44); ih.bind(name, "Nintendo SNES / Super Famicom"); ih.bind(abbv, "SNES"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 45); ih.bind(name, "Sega Game Gear"); ih.bind(abbv, "GG"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 46); ih.bind(name, "Commodore Amiga CDTV"); ih.bind(abbv, "CDTV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 47); ih.bind(name, "Watara SuperVision"); ih.bind(abbv, "SV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 48); ih.bind(name, "Sega CD / Mega CD"); ih.bind(abbv, "SCD"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 49); ih.bind(name, "Philips CD-i"); ih.bind(abbv, "CD-i"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 50); ih.bind(name, "Dragon 32 / 64"); ih.bind(abbv, "D32"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 51); ih.bind(name, "Atari Jaguar"); ih.bind(abbv, "Jag"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 52); ih.bind(name, "3DO"); ih.bind(abbv, "3DO"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 53); ih.bind(name, "Pioneer LaserActive"); ih.bind(abbv, "LAct"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 55); ih.bind(name, "Sega 32X"); ih.bind(abbv, "32X"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 56); ih.bind(name, "Atari Jaguar CD"); ih.bind(abbv, "JgCD"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 57); ih.bind(name, "Commodore Amiga CD32"); ih.bind(abbv, "CD32"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 58); ih.bind(name, "Memorex VIS"); ih.bind(abbv, "VIS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 59); ih.bind(name, "Sega Pico"); ih.bind(abbv, "Pico"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 60); ih.bind(name, "Sega Saturn"); ih.bind(abbv, "Sat"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 61); ih.bind(name, "Sony PlayStation"); ih.bind(abbv, "PSX"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 62); ih.bind(name, "Tiger R-Zone"); ih.bind(abbv, "RZn"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 63); ih.bind(name, "Nintendo Virtual Boy"); ih.bind(abbv, "VB"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 64); ih.bind(name, "Nintendo 64"); ih.bind(abbv, "N64"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 65); ih.bind(name, "Bandai Pippin ATMARK / @WORLD"); ih.bind(abbv, "Pip"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 66); ih.bind(name, "SNK Neo Geo CD"); ih.bind(abbv, "NGCD"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 67); ih.bind(name, "Tiger Game.com"); ih.bind(abbv, "Gcom"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 69); ih.bind(name, "Nintendo Game Boy Color"); ih.bind(abbv, "GBC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 70); ih.bind(name, "SNK Neo Geo Pocket Color"); ih.bind(abbv, "NGPC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 71); ih.bind(name, "Sega Dreamcast"); ih.bind(abbv, "DC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 72); ih.bind(name, "Sony PlayStation 2"); ih.bind(abbv, "PS2"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 73); ih.bind(name, "Nuon Technology"); ih.bind(abbv, "Nuon"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 74); ih.bind(name, "Nintendo Game Boy Advance"); ih.bind(abbv, "GBA"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 75); ih.bind(name, "Microsoft Xbox"); ih.bind(abbv, "Xbox"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 76); ih.bind(name, "Nintendo GameCube"); ih.bind(abbv, "GC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 77); ih.bind(name, "Nintendo e-Reader"); ih.bind(abbv, "eRdr"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 78); ih.bind(name, "Nokia N-Gage"); ih.bind(abbv, "Nge"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 79); ih.bind(name, "Tapwave Zodiac"); ih.bind(abbv, "Zod"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 80); ih.bind(name, "Camerica Aladdin"); ih.bind(abbv, "Aldn"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 81); ih.bind(name, "IBM PC Jr."); ih.bind(abbv, "PCJr"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 82); ih.bind(name, "VideoBrain Family Computer"); ih.bind(abbv, "VBrn"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 83); ih.bind(name, "XaviX XaviXPORT"); ih.bind(abbv, "Xav"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 84); ih.bind(name, "Nintendo 64 DD"); ih.bind(abbv, "64DD"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 85); ih.bind(name, "NEC SuperGrafx"); ih.bind(abbv, "SGfx"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 86); ih.bind(name, "SNK Neo Geo Pocket"); ih.bind(abbv, "NGP"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 87); ih.bind(name, "Nintendo DS"); ih.bind(abbv, "DS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 88); ih.bind(name, "Nintendo Pokémon Mini"); ih.bind(abbv, "PKMN"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 89); ih.bind(name, "Tiger Gizmondo"); ih.bind(abbv, "Giz"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 90); ih.bind(name, "Cougar Boy / MegaDuck"); ih.bind(abbv, "CB"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 91); ih.bind(name, "Bandai WonderSwan"); ih.bind(abbv, "WS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 92); ih.bind(name, "Bandai WonderSwan Color / SwanCrystal"); ih.bind(abbv, "WSC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 93); ih.bind(name, "Nintendo Famicom Disk System"); ih.bind(abbv, "FCD"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 94); ih.bind(name, "VTech Socrates / Yeno Prof. Weiss-Alles"); ih.bind(abbv, "Soc"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 95); ih.bind(name, "Sony PSP"); ih.bind(abbv, "PSP"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 96); ih.bind(name, "Hartung Game Master"); ih.bind(abbv, "GMst"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 97); ih.bind(name, "SNK Neo Geo MVS"); ih.bind(abbv, "MVS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 98); ih.bind(name, "Bandai Playdia"); ih.bind(abbv, "Pld"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 99); ih.bind(name, "Bandai SuFami Turbo"); ih.bind(abbv, "SfTb"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 100); ih.bind(name, "Sharp X68000"); ih.bind(abbv, "X68k"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 101); ih.bind(name, "BIT Gamate"); ih.bind(abbv, "Gmte"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 102); ih.bind(name, "Romtec ColorVision"); ih.bind(abbv, "ClrV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 103); ih.bind(name, "Interton Video 2000"); ih.bind(abbv, "IV2K"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 104); ih.bind(name, "Philips Tele-spiel ES-2201"); ih.bind(abbv, "2201"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 105); ih.bind(name, "Mattel Childrens Discovery System"); ih.bind(abbv, "CDS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 106); ih.bind(name, "NEC PC-FX"); ih.bind(abbv, "PCFX"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 107); ih.bind(name, "Starpath Supercharger"); ih.bind(abbv, "SSC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 108); ih.bind(name, "EACA EG2000 Colour Genie"); ih.bind(abbv, "EG2K"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 109); ih.bind(name, "Exidy Sorcerer"); ih.bind(abbv, "Sorc"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 110); ih.bind(name, "EPOCH Cassette Vision"); ih.bind(abbv, "EpCV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 111); ih.bind(name, "EPOCH Super Cassette Vision / Yeno Super Cassette Vision"); ih.bind(abbv, "EpSC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 112); ih.bind(name, "Bandai Super Vision 8000"); ih.bind(abbv, "SV8K"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 113); ih.bind(name, "Entex Select-A-Game Machine"); ih.bind(abbv, "SaG"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 114); ih.bind(name, "Amstrad CPC 464"); ih.bind(abbv, "CPC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 115); ih.bind(name, "Microsoft Xbox 360"); ih.bind(abbv, "X360"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 116); ih.bind(name, "EPOCH Game Pocket Computer"); ih.bind(abbv, "EpGP"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 117); ih.bind(name, "Leapfrog iQuest"); ih.bind(abbv, "Quest"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 118); ih.bind(name, "Leapfrog Leapster"); ih.bind(abbv, "Leap"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 119); ih.bind(name, "Leapfrog Fly"); ih.bind(abbv, "Fly"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 120); ih.bind(name, "VTech V.Smile"); ih.bind(abbv, "SMIL"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 121); ih.bind(name, "Nikko digiBlast"); ih.bind(abbv, "digB"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 122); ih.bind(name, "MSX / MSX2 / Zemmix"); ih.bind(abbv, "MSX"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 123); ih.bind(name, "Sinclair ZX Spectrum"); ih.bind(abbv, "SZX"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 124); ih.bind(name, "Nintendo iQue"); ih.bind(abbv, "iQue"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 125); ih.bind(name, "Nintendo Game & Watch"); ih.bind(abbv, "NW&G"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 126); ih.bind(name, "Game Park GP32"); ih.bind(abbv, "GP32"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 127); ih.bind(name, "Ohio Arts Etch-A-Sketch Animator 2000"); ih.bind(abbv, "An2K"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 128); ih.bind(name, "Interton VC 4000"); ih.bind(abbv, "IV4K"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 130); ih.bind(name, "Toymax Arcadia II"); ih.bind(abbv, "ArcaII"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 131); ih.bind(name, "Sony PlayStation 3"); ih.bind(abbv, "PS3"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 132); ih.bind(name, "Nintendo Wii"); ih.bind(abbv, "Wii"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 133); ih.bind(name, "BBC Micro"); ih.bind(abbv, "BBC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 134); ih.bind(name, "FM Towns / Marty / Marty 2"); ih.bind(abbv, "FMT"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 135); ih.bind(name, "Olivetti Envision"); ih.bind(abbv, "OVE"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 136); ih.bind(name, "Casio PV-1000"); ih.bind(abbv, "CPV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 137); ih.bind(name, "Capcom CPS Changer"); ih.bind(abbv, "CPS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 138); ih.bind(name, "Casio Loopy"); ih.bind(abbv, "CSOLO"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 139); ih.bind(name, "Gakken Compact Vision"); ih.bind(abbv, "GCV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 140); ih.bind(name, "Commodore 16 / Plus 4 / 116"); ih.bind(abbv, "C16"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 141); ih.bind(name, "Palm OS"); ih.bind(abbv, "PaOS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 142); ih.bind(name, "Funtech Super A Can"); ih.bind(abbv, "FSAC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 143); ih.bind(name, "Sega SG-1000 / SC-3000"); ih.bind(abbv, "SSG"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 144); ih.bind(name, "ZAPiT Game Wave"); ih.bind(abbv, "ZGW"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 145); ih.bind(name, "Mattel Hyperscan"); ih.bind(abbv, "MHYP"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 146); ih.bind(name, "Casio PV-2000"); ih.bind(abbv, "CPV2"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 147); ih.bind(name, "GoGo TV Video Vision"); ih.bind(abbv, "GoTV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 148); ih.bind(name, "Tomy Pyuuta / Pyuuta Jr / Tutor / Dick Smith Wizzard"); ih.bind(abbv, "Tomy"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 149); ih.bind(name, "PC-50X Family"); ih.bind(abbv, "PC50X"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 150); ih.bind(name, "Microsoft Xbox Live Arcade"); ih.bind(abbv, "XBLA"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 151); ih.bind(name, "Sony PlayStation Network"); ih.bind(abbv, "PSN"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 152); ih.bind(name, "Nintendo Virtual Console / WiiWare"); ih.bind(abbv, "NVC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 153); ih.bind(name, "VTech V.Flash"); ih.bind(abbv, "V.Flash"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 154); ih.bind(name, "VTech Learning Pad, The"); ih.bind(abbv, "Learning Pad"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 155); ih.bind(name, "Fisher Price Pixter"); ih.bind(abbv, "Pixter"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 156); ih.bind(name, "TimeTop GameKing I / II / III"); ih.bind(abbv, "GK"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 157); ih.bind(name, "LeapFrog Didj"); ih.bind(abbv, "Didj"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 158); ih.bind(name, "Jazwares Disney Dream Sketcher"); ih.bind(abbv, "JDDS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 159); ih.bind(name, "VTech V.Smile Baby"); ih.bind(abbv, "VVSB"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 160); ih.bind(name, "Nintendo DSi"); ih.bind(abbv, "DSi"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 161); ih.bind(name, "Nintendo DSiWare"); ih.bind(abbv, "DSiW"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 162); ih.bind(name, "Cybiko Classic / Extreme"); ih.bind(abbv, "Cybi"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 163); ih.bind(name, "VTech CreatiVision"); ih.bind(abbv, "VTCV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 164); ih.bind(name, "Buzztime Home Trivia System"); ih.bind(abbv, "BUZZ"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 165); ih.bind(name, "Black Point System"); ih.bind(abbv, "BPS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 166); ih.bind(name, "Sharp X1"); ih.bind(abbv, "X1"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 167); ih.bind(name, "NEC PC-88"); ih.bind(abbv, "PC88"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 168); ih.bind(name, "SNK Hyper Neo Geo 64"); ih.bind(abbv, "HNG"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 169); ih.bind(name, "NEC PC-98"); ih.bind(abbv, "PC98"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 170); ih.bind(name, "Fujitsu FM-7"); ih.bind(abbv, "FM7"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 171); ih.bind(name, "Enterprise 64 / 128"); ih.bind(abbv, "E64"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 172); ih.bind(name, "NEC Trek / PC-6001"); ih.bind(abbv, "PC6001"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 173); ih.bind(name, "Sharp MZ Family"); ih.bind(abbv, "SMZ"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 174); ih.bind(name, "NEC PC-8001"); ih.bind(abbv, "PC8001"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 175); ih.bind(name, "Robotron KC 85"); ih.bind(abbv, "KC85"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 176); ih.bind(name, "Robotron KC 87"); ih.bind(abbv, "KC87"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 177); ih.bind(name, "Hasbro Net Jet"); ih.bind(abbv, "NJET"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 178); ih.bind(name, "Timex Sinclair 2068"); ih.bind(abbv, "TS2068"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 179); ih.bind(name, "Tec Toy Zeebo"); ih.bind(abbv, "TTZ"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 180); ih.bind(name, "Nichibutsu My Vision"); ih.bind(abbv, "NMV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 181); ih.bind(name, "Sears Talking Computron"); ih.bind(abbv, "STC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 182); ih.bind(name, "Nintendo 3DS"); ih.bind(abbv, "3DS"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 183); ih.bind(name, "Taito Cybercore System / F3 System"); ih.bind(abbv, "TCC"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 184); ih.bind(name, "Sammy Atomiswave"); ih.bind(abbv, "ATW"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 185); ih.bind(name, "Microsoft Xbox Live Indie Games"); ih.bind(abbv, "XBLI"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 186); ih.bind(name, "Thomson MO5"); ih.bind(abbv, "MO5"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 187); ih.bind(name, "Philips VG-5000"); ih.bind(abbv, "VG5K"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 188); ih.bind(name, "Nintendo eShop"); ih.bind(abbv, "eShop"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 189); ih.bind(name, "Sony PlayStation Vita"); ih.bind(abbv, "Vita"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 999); ih.bind(name, "Multiple Consoles"); ih.bind(abbv, "MULTI"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 1000/*D*/); ih.bind(name, "DVD"); ih.bind(abbv, "DVD"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 1001/*G*/); ih.bind(name, "Game & Watch Handhelds"); ih.bind(abbv, "G&W"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 1002/*H*/); ih.bind(name, "Stand-Alone Handhelds"); ih.bind(abbv, "Hand"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 1003/*J*/); ih.bind(name, "Plug & Play TV Games"); ih.bind(abbv, "TV"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 1004/*P*/); ih.bind(name, "Pong Consoles"); ih.bind(abbv, "Pong"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 1005/*T*/); ih.bind(name, "Tiger Handhelds"); ih.bind(abbv, "TigH"); ih.execute();
        ih.prepareForInsert(); ih.bind(id, 1006/*VHS*/); ih.bind(name, "Betamax / VHS"); ih.bind(abbv, "BVHS"); ih.execute();

	}
}
