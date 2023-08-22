import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.Predicate;
import java.util.Collection;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.File;
import java.io.FileInputStream;

public class DBCreator {
    public static void main( String[] args ) {
	// validate arguments
	if ( args.length != 1 ) {
	    System.err.println( "usage: java DBCreator absolute_path" );
	    System.exit( -1 );
	}

	// retrieve a given directory
	String absolute_path = args[0];
	Path path = Paths.get( absolute_path );
	if ( !Files.exists( path ) ) {
	    System.err.println( absolute_path + " doesn't exist."  );
	    System.exit( -1 );
	}
	File directory = new File( absolute_path );

	// create a Hazelcast IMap 
	HazelcastInstance hz = Hazelcast.newHazelcastInstance( );
	IMap<String, String> files = hz.getMap( "files" );

	// read all files into IMap
	File filesList[ ] = directory.listFiles( );
	int fileCount = 0; // # all files that have been read into IMap
	for( File file : filesList ) {
	    String name = file.getName( );
	    int length = ( int ) file.length( );
	    System.out.println( "File name: " + name + ", length = " + length );

	    // read each file at once and store IMap.
	    try {
		FileInputStream fis = new FileInputStream( file );
		byte[ ] data = new byte[ length ];
		fis.read( data );
		fis.close();
		String str = new String( data, "UTF-8" );
		files.put( name, str );
		fileCount++;
	    } catch( Exception e ) {
		e.printStackTrace( );
	    }
	}	

	// print out # files that have been read in success
	System.out.println( "file count in " + absolute_path + " = " + fileCount );
    }
}
