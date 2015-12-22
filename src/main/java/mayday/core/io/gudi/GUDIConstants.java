package mayday.core.io.gudi;

public class GUDIConstants {

	// Key Strings for Properties
	public static final String IMPORTER_TYPE = "GUDI-ImporterType";
	public static final String FILESYSTEM_IMPORTER_TYPE = "GUDI-FilesystemImporterType";
	public static final String FILE_EXTENSIONS ="GUDI-FileExtensions";
	public static final String TYPE_DESCRIPTION = "GUDI-TypeDescription";
//	public static final String IMPORTER_DESCRIPTION = "GUDI-ImporterDescription";
	
	// Value constants	
	public static final int IMPORTERTYPE_FILESYSTEM = 0;
	public static final int IMPORTERTYPE_OTHER = 1;
	public static final int IMPORTERTYPE_QUICKLOAD = 2;
	
	public static final int DIRECTORY = 0;
	public static final int ONEFILE = 1;
	public static final int MANYFILES = 2;

}
