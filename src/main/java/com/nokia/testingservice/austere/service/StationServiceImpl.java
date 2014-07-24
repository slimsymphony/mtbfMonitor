package com.nokia.testingservice.austere.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.nokia.testingservice.austere.exception.ServiceException;
import com.nokia.testingservice.austere.model.Station;
import com.nokia.testingservice.austere.util.CommonUtils;
import com.nokia.testingservice.austere.util.DbUtils;
import com.nokia.testingservice.austere.util.LogUtils;

public class StationServiceImpl implements StationService {

	@Override
	public void createStation(Station station) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "insert into Stations(PcName,sitename,Status) values(?,?,?)";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, station.getPcName());
			ps.setString(2, station.getSiteName());
			ps.setInt(3, station.getUsed());
			ps.executeUpdate();
		} catch (Exception ex) {
			LogUtils.getServiceLog().error("Create Station failed:" + station,
					ex);
		} finally {
			if (ps != null)
				CommonUtils.closeQuitely(ps);
			if (conn != null)
				CommonUtils.closeQuitely(conn);
		}
	}

	private Station encap(ResultSet rs) throws SQLException {
		Station station = new Station();
		station.setPcName(rs.getString("PcName"));
		station.setSiteName(rs.getString("sitename"));
		station.setId(rs.getInt("StationID"));
		station.setUsed(rs.getInt("Status"));
		return station;
	}

	@Override
	public void updateStation(Station station) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "update Stations set PcName=?,Status=? where StationID=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, station.getPcName());
			ps.setInt(2, station.getUsed());
			ps.setInt(3, station.getId());
			ps.executeUpdate();
		} catch (Exception ex) {
			LogUtils.getServiceLog().error("Update Station failed:" + station,
					ex);
		} finally {
			if (ps != null)
				CommonUtils.closeQuitely(ps);
			if (conn != null)
				CommonUtils.closeQuitely(conn);
		}
	}
	
	private void updateStationStatus(PreparedStatement ps, int id, int status) throws SQLException {
		try {
			ps.setInt(1, status);
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (Exception ex) {
			LogUtils.getServiceLog().error("Update Station status failed:[" + id+":"+status+"]", ex);
		} finally {
			ps.clearParameters();
		}
	}

	@Override
	public void deleteStationByID(int stationID) throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "delete from Stations where StationID=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, stationID);
			ps.executeUpdate();
		} catch (Exception ex) {
			LogUtils.getServiceLog().error(
					"Delete Station By StationId failed:" + stationID, ex);
		} finally {
			if (ps != null)
				CommonUtils.closeQuitely(ps);
			if (conn != null)
				CommonUtils.closeQuitely(conn);
		}
	}

	@Override
	public void deleteStationByPcName(String site, String pcName)
			throws ServiceException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			String sql = "delete from Stations where sitename=? and PcName=?";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, site);
			ps.setString(2, pcName);
			ps.executeUpdate();
		} catch (Exception ex) {
			LogUtils.getServiceLog().error(
					"Delete Station by pcname failed:" + pcName, ex);
		} finally {
			if (ps != null)
				CommonUtils.closeQuitely(ps);
			if (conn != null)
				CommonUtils.closeQuitely(conn);
		}
	}

	@Override
	public List<Station> getAllStations() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Station> list = new ArrayList<Station>();
		try {
			String sql = "select * from Stations order by StationID";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(encap(rs));
			}
		} catch (Exception ex) {
			LogUtils.getServiceLog().error("Get all stations failed", ex);
		} finally {
			if (rs != null)
				CommonUtils.closeQuitely(rs);
			if (ps != null)
				CommonUtils.closeQuitely(ps);
			if (conn != null)
				CommonUtils.closeQuitely(conn);
		}
		return list;
	}

	@Override
	public List<Station> getAllStationsBySite(String site) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Station> list = new ArrayList<Station>();
		try {
			String sql = "select * from Stations where sitename=? order by pcName";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, site);
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(encap(rs));
			}
		} catch (Exception ex) {
			LogUtils.getServiceLog().error("Get stations by site failed, site="+site, ex);
		} finally {
			if (rs != null)
				CommonUtils.closeQuitely(rs);
			if (ps != null)
				CommonUtils.closeQuitely(ps);
			if (conn != null)
				CommonUtils.closeQuitely(conn);
		}
		return list;
	}

	@Override
	public List<Station> getStationsByStatus(String site, int status) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<Station> list = new ArrayList<Station>();
		try {
			String sql = "select * from Stations where sitename=? and status=? order by StationID";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, site);
			ps.setInt(2, status);
			rs = ps.executeQuery();
			while (rs.next()) {
				list.add(encap(rs));
			}
		} catch (Exception ex) {
			LogUtils.getServiceLog().error(
					"Get Station by status failed, site="+site+",status=" + status, ex);
		} finally {
			if (rs != null)
				CommonUtils.closeQuitely(rs);
			if (ps != null)
				CommonUtils.closeQuitely(ps);
			if (conn != null)
				CommonUtils.closeQuitely(conn);
		}
		return list;
	}

	@Override
	public Station getStationByPcName( String site, String pcName ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from Stations where sitename=? and PcName=? order by StationID";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, site);
			ps.setString(2, pcName);
			rs = ps.executeQuery();
			if (rs.next())
				return encap(rs);
		} catch (Exception ex) {
			LogUtils.getServiceLog().error(
					"Get Station by pcName failed:" + pcName, ex);
		} finally {
			if (rs != null)
				CommonUtils.closeQuitely(rs);
			if (ps != null)
				CommonUtils.closeQuitely(ps);
			if (conn != null)
				CommonUtils.closeQuitely(conn);
		}
		return null;
	}

	@Override
	public Station getStationById(int stationID) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sql = "select * from Stations where StationID=? order by StationID";
			conn = DbUtils.getCentralConnection();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, stationID);
			rs = ps.executeQuery();
			if (rs.next())
				return encap(rs);
		} catch (Exception ex) {
			LogUtils.getServiceLog().error(
					"Get Station by stationID failed:" + stationID, ex);
		} finally {
			if (rs != null)
				CommonUtils.closeQuitely(rs);
			if (ps != null)
				CommonUtils.closeQuitely(ps);
			if (conn != null)
				CommonUtils.closeQuitely(conn);
		}
		return null;
	}

	@Override
	public Station getStation(String siteName, int stationId) {
		Station station = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			if (siteName != null && !siteName.trim().isEmpty()) {
				conn = DbUtils.getMonitorConnection(siteName);
				String sql = "select * from stationInfos where id=?";
				ps = conn.prepareStatement(sql);
				ps.setInt(1, stationId);
				rs = ps.executeQuery();
				if (rs.next()) {
					station = injectStation(rs);
					station.setSiteName(siteName);
				}
			} else {
				Map<String, Connection> conns = DbUtils.getMonitorConnections();
				for (String site : conns.keySet()) {
					try {
						conn = conns.get(site);
						String sql = "select * from stationInfos where id=?";
						ps = conn.prepareStatement(sql);
						ps.setInt(1, stationId);
						rs = ps.executeQuery();
						if (rs.next()) {
							station = injectStation(rs);
							station.setSiteName(site);
							return station;
						}
					} finally {
						CommonUtils.closeQuitely(rs);
						CommonUtils.closeQuitely(ps);
						CommonUtils.closeQuitely(conn);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return station;
	}

	private Station injectStation(ResultSet rs) throws SQLException {
		Station s = new Station();
		s.setId(rs.getInt("id"));
		s.setDetails(rs.getString("details"));
		s.setIp(rs.getString("ip"));
		s.setMac(rs.getString("mac"));
		s.setPcName(rs.getString("pcname"));
		s.setUsed(rs.getInt("used"));
		return s;
	}

	@Override
	public float getValidRunningTime(String siteName, int week) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		float period = 0f;
		try {
			Date[] interval = CommonUtils.getIntervalFromWeek(week);
			if (siteName != null && !siteName.trim().isEmpty()) {
				conn = DbUtils.getMonitorConnection(siteName);
				String sql = "select starttime,lastUpdate from executionStates where starttime<? and lastUpdate>?";
				ps = conn.prepareStatement(sql);
				ps.setTimestamp(1, new Timestamp(interval[1].getTime()));
				ps.setTimestamp(2, new Timestamp(interval[0].getTime()));
				rs = ps.executeQuery();
				Timestamp s = null, e = null;
				while (rs.next()) {
					s = rs.getTimestamp(1);
					e = rs.getTimestamp(2);
					if (s.getTime() <= interval[0].getTime()) {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - interval[0].getTime();
						} else {
							period += interval[1].getTime()
									- interval[0].getTime();
						}
					} else {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - s.getTime();
						} else {
							period += interval[1].getTime() - s.getTime();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return period / (1000f * 60f * 60f);
	}

	@Override
	public float getValidRunningTimeForTask( String siteName, int week, int taskId ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn2 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		float period = 0f;
		try {
			Date[] interval = CommonUtils.getIntervalFromWeek(week);
			conn2 = DbUtils.getCentralConnection();
			String sql2 = "select distinct pcname from stations where sitename=?";
			if(taskId>0)
				sql2+=" and stationId in (select stationid from task_stations where taskId=?)";
			ps2 = conn2.prepareStatement(sql2);
			ps2.setString(1, siteName);
			if(taskId>0)
				ps2.setInt(2, taskId);
			rs2 = ps2.executeQuery();
			StringBuffer allPcs = new StringBuffer(200); 
			while(rs2.next()) {
				if(allPcs.length()>0)
					allPcs.append(",");
				allPcs.append("'").append(rs2.getString(1)).append("'");
			}
			if (siteName != null && !siteName.trim().isEmpty()) {
				conn = DbUtils.getMonitorConnection(siteName);
				String sql = "select starttime,lastUpdate from (select * from executionStates union select * from executionStates_history) as A left join stationInfos on A.stationId=stationInfos.id where pcname in (" +allPcs.toString()+ ") and starttime<? and lastUpdate>?";
				ps = conn.prepareStatement(sql);
				ps.setTimestamp(1, new Timestamp(interval[1].getTime()));
				ps.setTimestamp(2, new Timestamp(interval[0].getTime()));
				rs = ps.executeQuery();
				Timestamp s = null, e = null;
				while (rs.next()) {
					s = rs.getTimestamp(1);
					e = rs.getTimestamp(2);
					if (s.getTime() <= interval[0].getTime()) {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - interval[0].getTime();
						} else {
							period += interval[1].getTime()
									- interval[0].getTime();
						}
					} else {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - s.getTime();
						} else {
							period += interval[1].getTime() - s.getTime();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs2);
			CommonUtils.closeQuitely(ps2);
			CommonUtils.closeQuitely(conn2);
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return period / (1000f * 60f * 60f);
	}
	
	@Override
	public float getValidRunningTimeForProduct(String siteName, int week,
			String product) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		float period = 0f;
		try {
			Date[] interval = CommonUtils.getIntervalFromWeek(week);
			if (siteName != null && !siteName.trim().isEmpty()) {
				conn = DbUtils.getMonitorConnection(siteName);
				String sql = "select starttime,lastUpdate from executionStates where starttime<? and lastUpdate>? and product=?";
				ps = conn.prepareStatement(sql);
				ps.setTimestamp(1, new Timestamp(interval[1].getTime()));
				ps.setTimestamp(2, new Timestamp(interval[0].getTime()));
				ps.setString(3, product);
				rs = ps.executeQuery();
				Timestamp s = null, e = null;
				while (rs.next()) {
					s = rs.getTimestamp(1);
					e = rs.getTimestamp(2);
					if (s.getTime() <= interval[0].getTime()) {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - interval[0].getTime();
						} else {
							period += interval[1].getTime()
									- interval[0].getTime();
						}
					} else {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - s.getTime();
						} else {
							period += interval[1].getTime() - s.getTime();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return period / (1000f * 60f * 60f);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer, Map<String, Integer>> getIowCardsChanges(
			String siteName, int pre, int post) {
		Map<Integer, Map<String, Integer>> map = new LinkedHashMap<Integer, Map<String, Integer>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			for (int i = pre; i <= post; i++) {
				Map<String, Integer> detail = new LinkedHashMap<String, Integer>();
				Date[] interval = CommonUtils.getIntervalFromWeek(i);
				if (siteName != null && !siteName.trim().isEmpty()) {
					conn = DbUtils.getMonitorConnection(siteName);
					String sql = "select details from stationInfos where id in( select distinct stationId from executionStates where starttime<? and lastUpdate>?)";
					ps = conn.prepareStatement(sql);
					ps.setTimestamp(1, new Timestamp(interval[1].getTime()));
					ps.setTimestamp(2, new Timestamp(interval[0].getTime()));
					rs = ps.executeQuery();
					Document doc = null;
					String name = null;
					while (rs.next()) {
						doc = DocumentHelper.parseText(rs.getString(1));
						List<Element> cards = (List<Element>) doc
								.selectNodes("/StationDetails/IowCard/Name");
						for (Element ele : cards) {
							if (ele != null) {
								name = ele.getStringValue().trim();
								if (!detail.containsKey(name))
									detail.put(name, 0);
								detail.put(name, detail.get(name) + 1);
							}
						}
					}
				}
				map.put(i, detail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<Integer,Map<String,Integer>> getIowCardsChangesForTask( String siteName, int pre, int post, int taskId){
		Map<Integer, Map<String, Integer>> map = new LinkedHashMap<Integer, Map<String, Integer>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn2 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		try {
			conn2 = DbUtils.getCentralConnection();
			String sql2 = "select distinct pcname from stations where sitename=?";
			if(taskId>0)
				sql2+=" and stationId in (select stationid from task_stations where taskId=?)";
			ps2 = conn2.prepareStatement(sql2);
			ps2.setString(1, siteName);
			if(taskId>0)
				ps2.setInt(2, taskId);
			rs2 = ps2.executeQuery();
			StringBuffer allPcs = new StringBuffer(200); 
			while(rs2.next()) {
				if(allPcs.length()>0)
					allPcs.append(",");
				allPcs.append("'").append(rs2.getString(1)).append("'");
			}
			for (int i = pre; i <= post; i++) {
				Map<String, Integer> detail = new LinkedHashMap<String, Integer>();
				Date[] interval = CommonUtils.getIntervalFromWeek(i);
				if (siteName != null && !siteName.trim().isEmpty()) {
					conn = DbUtils.getMonitorConnection(siteName);
					String sql = "select details from stationInfos where id in( select distinct stationId from (select * from executionStates union select * from executionStates_history) as A left join stationInfos on A.stationId=stationInfos.id where pcname in ( "+ allPcs.toString()+" ) and starttime<? and lastUpdate>?)";
					ps = conn.prepareStatement(sql);
					ps.setTimestamp(1, new Timestamp(interval[1].getTime()));
					ps.setTimestamp(2, new Timestamp(interval[0].getTime()));
					rs = ps.executeQuery();
					Document doc = null;
					String name = null;
					while (rs.next()) {
						doc = DocumentHelper.parseText(rs.getString(1));
						List<Element> cards = (List<Element>) doc.selectNodes("/StationDetails/IowCard/Name");
						for (Element ele : cards) {
							if (ele != null) {
								name = ele.getStringValue().trim();
								if (!detail.containsKey(name))
									detail.put(name, 0);
								detail.put(name, detail.get(name) + 1);
							}
						}
					}
				}
				map.put(i, detail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs2);
			CommonUtils.closeQuitely(ps2);
			CommonUtils.closeQuitely(conn2);
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, Map<String, Integer>> getIowCardsChangesForProduct(
			String siteName, int pre, int post, String product) {
		Map<Integer, Map<String, Integer>> map = new LinkedHashMap<Integer, Map<String, Integer>>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			for (int i = pre; i <= post; i++) {
				Map<String, Integer> detail = new LinkedHashMap<String, Integer>();
				Date[] interval = CommonUtils.getIntervalFromWeek(i);
				if (siteName != null && !siteName.trim().isEmpty()) {
					conn = DbUtils.getMonitorConnection(siteName);
					String sql = "select details from stationInfos where id in( select distinct stationId from executionStates where starttime<? and lastUpdate>? and product=?)";
					ps = conn.prepareStatement(sql);
					ps.setTimestamp(1, new Timestamp(interval[1].getTime()));
					ps.setTimestamp(2, new Timestamp(interval[0].getTime()));
					ps.setString(3, product);
					rs = ps.executeQuery();
					Document doc = null;
					String name = null;
					while (rs.next()) {
						doc = DocumentHelper.parseText(rs.getString(1));
						List<Element> cards = (List<Element>) doc
								.selectNodes("/StationDetails/IowCard/Name");
						for (Element ele : cards) {
							if (ele != null) {
								name = ele.getStringValue().trim();
								if (!detail.containsKey(name))
									detail.put(name, 0);
								detail.put(name, detail.get(name) + 1);
							}
						}
					}
				}
				map.put(i, detail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}

	@Override
	public Map<Station, Float> getStationValidTimeByWeek(String siteName,
			int week) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<Station, Float> map = new HashMap<Station, Float>();
		try {
			Date[] interval = CommonUtils.getIntervalFromWeek(week);
			if (siteName != null && !siteName.trim().isEmpty()) {
				conn = DbUtils.getMonitorConnection(siteName);
				String sql = "select starttime,lastUpdate,stationInfos.* from (select * from executionStates union select * from executionStates_history) as A left join stationInfos on A.stationId=stationInfos.id where starttime<? and lastUpdate>? order by starttime";
				ps = conn.prepareStatement(sql);
				ps.setTimestamp(1, new Timestamp(interval[1].getTime()));
				ps.setTimestamp(2, new Timestamp(interval[0].getTime()));
				rs = ps.executeQuery();
				Timestamp s = null, e = null;
				Station station = null;
				while (rs.next()) {
					float period = 0f;
					s = rs.getTimestamp(1);
					e = rs.getTimestamp(2);
					if (s.getTime() <= interval[0].getTime()) {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - interval[0].getTime();
						} else {
							period += interval[1].getTime()
									- interval[0].getTime();
						}
					} else {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - s.getTime();
						} else {
							period += interval[1].getTime() - s.getTime();
						}
					}
					station = injectStation(rs);
					if (station == null || station.getId() == 0) {
						LogUtils.getServiceLog().error(
								"[getStationValidTimeByWeek] get a invalid station:"
										+ station);
						continue;
					}
					if (map.get(station) == null)
						map.put(station, 0f);
					map.put(station, map.get(station)
							+ (period / (1000f * 60f * 60f)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}
	
	@Override
	public Map<Station, Float> getStationValidTimeByWeekForTask( String siteName, int week, int taskId ) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn2 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		Map<Station, Float> map = new HashMap<Station, Float>();
		try {
			Date[] interval = CommonUtils.getIntervalFromWeek(week);
			conn2 = DbUtils.getCentralConnection();
			String sql2 = "select distinct pcname from stations where sitename=?";
			if(taskId>0)
				sql2+=" and stationId in (select stationid from task_stations where taskId=?)";
			ps2 = conn2.prepareStatement(sql2);
			ps2.setString(1, siteName);
			if(taskId>0)
				ps2.setInt(2, taskId);
			rs2 = ps2.executeQuery();
			StringBuffer allPcs = new StringBuffer(200); 
			while(rs2.next()) {
				if(allPcs.length()>0)
					allPcs.append(",");
				allPcs.append("'").append(rs2.getString(1)).append("'");
			}
			if (siteName != null && !siteName.trim().isEmpty()) {
				conn = DbUtils.getMonitorConnection(siteName);
				String sql = "select starttime,lastUpdate,stationInfos.* from (select * from executionStates union select * from executionStates_history) as A left join stationInfos on A.stationId=stationInfos.id where pcname in ("+ allPcs.toString() +") and starttime<? and lastUpdate>? order by starttime";
				ps = conn.prepareStatement(sql);
				ps.setTimestamp(1, new Timestamp(interval[1].getTime()));
				ps.setTimestamp(2, new Timestamp(interval[0].getTime()));
				rs = ps.executeQuery();
				Timestamp s = null, e = null;
				Station station = null;
				while (rs.next()) {
					float period = 0f;
					s = rs.getTimestamp(1);
					e = rs.getTimestamp(2);
					if (s.getTime() <= interval[0].getTime()) {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - interval[0].getTime();
						} else {
							period += interval[1].getTime()
									- interval[0].getTime();
						}
					} else {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - s.getTime();
						} else {
							period += interval[1].getTime() - s.getTime();
						}
					}
					station = injectStation(rs);
					if (station == null || station.getId() == 0) {
						LogUtils.getServiceLog().error(
								"[getStationValidTimeByWeek] get a invalid station:"
										+ station);
						continue;
					}
					if (map.get(station) == null)
						map.put(station, 0f);
					map.put(station, map.get(station)
							+ (period / (1000f * 60f * 60f)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs2);
			CommonUtils.closeQuitely(ps2);
			CommonUtils.closeQuitely(conn2);
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}

	@Override
	public Map<Station, Float> getStationValidTimeByWeekForProduct(
			String siteName, int week, String product) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<Station, Float> map = new HashMap<Station, Float>();
		try {
			Date[] interval = CommonUtils.getIntervalFromWeek(week);
			if (siteName != null && !siteName.trim().isEmpty()) {
				conn = DbUtils.getMonitorConnection(siteName);
				String sql = "select starttime,lastUpdate,stationInfos.* from executionStates left join stationInfos on executionStates.stationId=stationInfos.id where product=? and starttime<? and lastUpdate>? order by starttime";
				ps = conn.prepareStatement(sql);
				ps.setString(1, product);
				ps.setTimestamp(2, new Timestamp(interval[1].getTime()));
				ps.setTimestamp(3, new Timestamp(interval[0].getTime()));
				rs = ps.executeQuery();
				Timestamp s = null, e = null;
				Station station = null;
				while (rs.next()) {
					float period = 0f;
					s = rs.getTimestamp(1);
					e = rs.getTimestamp(2);
					if (s.getTime() <= interval[0].getTime()) {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - interval[0].getTime();
						} else {
							period += interval[1].getTime()
									- interval[0].getTime();
						}
					} else {
						if (e.getTime() <= interval[1].getTime()) {
							period += e.getTime() - s.getTime();
						} else {
							period += interval[1].getTime() - s.getTime();
						}
					}
					station = injectStation(rs);
					if (station == null || station.getId() == 0) {
						LogUtils.getServiceLog().error(
								"[getStationValidTimeByWeek] get a invalid station:"
										+ station);
						continue;
					}
					if (map.get(station) == null)
						map.put(station, 0f);
					map.put(station, map.get(station)
							+ (period / (1000f * 60f * 60f)));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(conn);
		}
		return map;
	}
	
	public Map<Station, String> syncStationStatus( String site, int taskId ) {
		Connection conn = null;
		Connection conn2 = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		PreparedStatement ps4 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		ResultSet rs3 = null;
		Map<Station,String> statuses = new LinkedHashMap<Station,String>();
		try {
			String sql = "update Stations set Status=? where StationID=?";
			String sql2 = "select PcName from task_stations left join stations on task_stations.stationId=stations.StationID where taskId=?"; 
			conn = DbUtils.getMonitorConnection(site);
			conn2 = DbUtils.getCentralConnection();
			
			if( taskId == -1 ) {
				ps = conn.prepareStatement("select COUNT(1) from stationInfos where pcname=?");
				ps2 = conn2.prepareStatement("select * from Stations where sitename=? and stationId not in (select stationId from task_stations) order by pcName");
				ps2.setString(1,site);
				rs2 = ps2.executeQuery();
				while(rs2.next()) {
					Station s = encap(rs2);
					ps.setString(1, s.getPcName());
					rs = ps.executeQuery();
					rs.next();
					if(rs.getInt(1)>0) {
						if(!statuses.containsKey(s))
							statuses.put(s, "NotRunning");
					}else {
						if(!statuses.containsKey(s))
							statuses.put(s, "NotExist");
					}
					CommonUtils.closeQuitely(rs);
					ps.clearParameters();
				}
				return statuses;
			}
			
			List<String> pcs = new ArrayList<String>();
			if(taskId>0) {
				ps4 = conn2.prepareStatement(sql2);
				ps4.setInt(1, taskId);
				rs3 = ps4.executeQuery();
				while(rs3.next()) {
					pcs.add(rs3.getString(1));
				}
			}
			ps3 = conn2.prepareStatement(sql);
			ps = conn.prepareStatement("select product,status from (select * from executionStates union select * from executionStates_history) as A left join stationInfos on A.stationId=stationInfos.id where pcname=? order by A.lastupdate desc");
			ps2 = conn.prepareStatement("select COUNT(1) from stationInfos where pcname=?");
			List<Station> sts = this.getAllStationsBySite(site);
			for( Station st : sts ) {
				if( taskId>0 && pcs.indexOf(st.getPcName())<0)
					continue;
				String status = "NotExist";
				int ns = -1;
				ps.setString(1, st.getPcName());
				rs = ps.executeQuery();
				if(rs.next()) {
					String sb = rs.getString(2);
					if(sb.equals("P")) {
						status = rs.getString(1);
						ns = 1;
					}else {
						status = "NotRunning";
						ns = 0;
					}
				}else {
					ps2.setString(1, st.getPcName());
					rs2 = ps2.executeQuery();
					rs2.next();
					int cnt = rs2.getInt(1);
					CommonUtils.closeQuitely(rs2);
					ps2.clearParameters();
					if(cnt>0) {
						status = "NotRunning";
						ns = 0;
					}
				}
				statuses.put(st, status);
				CommonUtils.closeQuitely(rs);
				ps.clearParameters();
				updateStationStatus(ps3,st.getId(),ns);
			}
		}catch( Exception ex) {
			ex.printStackTrace();
		}finally {
			CommonUtils.closeQuitely(rs3);
			CommonUtils.closeQuitely(ps4);
			CommonUtils.closeQuitely(rs);
			CommonUtils.closeQuitely(rs2);
			CommonUtils.closeQuitely(ps);
			CommonUtils.closeQuitely(ps2);
			CommonUtils.closeQuitely(ps3);
			CommonUtils.closeQuitely(conn);
			CommonUtils.closeQuitely(conn2);
		}
		return statuses;
	}
}
