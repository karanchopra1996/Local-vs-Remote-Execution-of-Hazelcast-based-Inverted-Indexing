import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.query.Predicates;
import com.hazelcast.query.Predicate;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;

import com.hazelcast.core.HazelcastInstanceAware;
import java.util.concurrent.*;
import java.util.Date;
import java.io.Serializable;

public class LocalKeySearchCallable 
    implements Callable<String>, HazelcastInstanceAware, Serializable {

    private HazelcastInstance hz;

    @Override
    public void setHazelcastInstance( HazelcastInstance hz ) {
	this.hz = hz;
    }

    @Override
    public String call( ) throws Exception {
	IMap<String, String> map = hz.getMap( "files" );
	//Iterator<String> iter = map.keySet( ).iterator( );
	Iterator<String> iter = map.localKeySet( ).iterator( );

	String fileNames = "";
	int fileCount = 0;
	while ( iter.hasNext( ) ) {
	    String name = iter.next( );
	    System.out.println( name );
	    fileNames += name + "\n";
	    fileCount++;
	}
	return hz.getCluster( ).getLocalMember( ).toString( ) + " - " + "local file count = " + fileCount + "\n" + fileNames;
    }
}

