package org.dbxp.matriximporter

class MatrixImporter {

	/**
	 * Imports a file using an existing MatrixReader. If no reader is found that
	 * is able to parse the file, null is returned.
	 * @param file	File to read
	 * @return		Two-dimensional data matrix with the contents of the file. The matrix has the structure:
	 * 				[ 
	 * 					[ 1, 3, 5 ] // First line
	 * 					[ 9, 1, 2 ] // Second line
	 * 				]
	 * 				The matrix is always rectangular
	 */
	public def importFile( File file, Map hints = null ) {
		// Always give a map to the readers
		if( !hints )
			hints = [:]
		
		// Loop through all registered readers, and parse the file using
		// the first reader that is able to parse the file.
		for( reader in readers ) {
			if( reader.canParse( file ) ) {
				def parsedFile = reader.parse( file, hints );
				
				// Only return the value if the file has been correctly parsed
				// (i.e. the structure != null). Otherwise, we should try to parse
				// the file using another reader (if applicable)
				if( parsedFile != null )
					return parsedFile
			}
		}
			
		// If parsing didn't work out, return null
		return null
	}
	
	/**
	 * Registers a reader with the importer, so the reader can be used to parse files
	 * @param c	Object that implements MatrixReader
	 */
	public void registerReader( MatrixReader c ) {
		if( c instanceof MatrixReader && !( c in readers ) ) 
			readers << c
	}
	
	/**
	 * Removes a reader from the importer, so the reader will not be used to parse files
	 * @param c		Class that implements MatrixReader 
	 */
	public void unregisterReader( Class c ) {
		readers = readers.findAll { !( it.class == c ) }
	}
	
	// Singleton instance
	private static MatrixImporter _instance = null;
	
	// List of registered readers
	private List readers = []
	
	// Private constructor in order to facilitate the singleton pattern
	private MatrixImporter() {
		registerReader( new ExcelReader() )
		registerReader( new CsvReader() )
	} 
	
	/**
	 * Returns the singleton instance of the MatrixImporter
	 * @return
	 */
	public static MatrixImporter getInstance() {
		if( _instance == null )
			_instance = new MatrixImporter();
		
		return _instance
	}
}
