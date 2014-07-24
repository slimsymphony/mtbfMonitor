import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GetAllStationInfos {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Class.forName( "com.microsoft.sqlserver.jdbc.SQLServerDriver" );
			conn = DriverManager.getConnection( "jdbc:sqlserver://10.57.14.200:1433;databaseName=MTBF_MONITOR", "austere", "austere2" );
			ps = conn.prepareStatement( "select pcname,ip,mac,details from stationInfos order by pcname" );
			rs = ps.executeQuery();
			String pcName = null;
			String ip = null;
			String mac = null;
			String details = null;
			while( rs.next() ) {
				pcName = rs.getString(1);
				ip = rs.getString(2);
				mac = rs.getString(3);
				details = rs.getString(4);
				System.out.println("pcName:"+pcName+",ip:"+ip+",mac:"+mac+",details:"+details);
				if( details!=null && !details.isEmpty() ) {
					String PCSystemName = details.substring(details.indexOf("<PCSystemName>")+13, details.indexOf("</PCSystemName>"));
					String CPUName = details.substring(details.indexOf("<CPUName>")+9, details.indexOf("</CPUName>"));
					String Memory = details.substring(details.indexOf("<Memory>")+9, details.indexOf("</Memory>"));
					String AustereVerison = details.substring(details.indexOf("<AustereVersion>")+16, details.indexOf("</AustereVersion>"));
					String IowCard = details.substring(details.indexOf("<IowCard>")+9, details.indexOf("</IowCard>"));
					System.out.println(",PcSystemName:"+PCSystemName+",CPUName:"+CPUName+",Memory:"+Memory+",AustereVerison:"+AustereVerison+",IowCard:"+IowCard);
				}
			}
		} catch ( Exception e ) {
			e.printStackTrace();
			System.err.println( "Get Data Connection failed, " + e.getMessage() );
		}finally {
			rs.close();
			ps.close();
			conn.close();
		}

	}

}