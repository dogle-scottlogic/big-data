package storers.cassandra;

import com.datastax.driver.core.*;

/**
 * Created by dogle on 05/12/2016.
 */
public class Cassandra {

    private Cluster cluster;
    private Session session;

    public Cassandra(String host) {
        cluster = Cluster.builder().addContactPoint(host).build();
    }

    public boolean connect() {
        try {
            this.session = this.cluster.connect();
            return true;
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
    }

    public boolean close() {
        try {
            this.session.close();
            this.cluster.close();
        }catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean createKeySpace(String name) {
        try {
            String query = "CREATE KEYSPACE " + name + " WITH replication "
                    + "= {'class':'SimpleStrategy', 'replication_factor':1}; ";
            this.session.execute(query);
            this.session.execute("USE " + name);
        } catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }

    public boolean dropKeySpace(String name)
    {
        try {
            String query = "Drop KEYSPACE " + name;
            this.session.execute(query);
        }catch (Exception e) {
            System.out.println(e.fillInStackTrace());
            return false;
        }
        return true;
    }
}
