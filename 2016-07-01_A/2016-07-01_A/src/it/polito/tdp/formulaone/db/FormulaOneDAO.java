package it.polito.tdp.formulaone.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import it.polito.tdp.formulaone.model.Adiacenza;
import it.polito.tdp.formulaone.model.Circuit;
import it.polito.tdp.formulaone.model.Constructor;
import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Season;


public class FormulaOneDAO {

	public List<Integer> getAllYearsOfRace() {
		
		String sql = "SELECT DISTINCT year FROM races ORDER BY year" ;
		
		try {
			Connection conn = ConnectDB.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Integer> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(rs.getInt("year"));
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Season> getAllSeasons() {
		
		String sql = "SELECT year, url FROM seasons ORDER BY year" ;
		
		try {
			Connection conn = ConnectDB.getConnection() ;

			PreparedStatement st = conn.prepareStatement(sql) ;
			
			ResultSet rs = st.executeQuery() ;
			
			List<Season> list = new ArrayList<>() ;
			while(rs.next()) {
				list.add(new Season(Year.of(rs.getInt("year")), rs.getString("url"))) ;
			}
			
			conn.close();
			return list ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}
	
	public List<Circuit> getAllCircuits() {

		String sql = "SELECT circuitId, name FROM circuits ORDER BY name";

		try {
			Connection conn = ConnectDB.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Circuit> list = new ArrayList<>();
			while (rs.next()) {
				list.add(new Circuit(rs.getInt("circuitId"), rs.getString("name")));
			}

			conn.close();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}
	
	public List<Constructor> getAllConstructors() {

		String sql = "SELECT constructorId, name FROM constructors ORDER BY name";

		try {
			Connection conn = ConnectDB.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			List<Constructor> constructors = new ArrayList<>();
			while (rs.next()) {
				constructors.add(new Constructor(rs.getInt("constructorId"), rs.getString("name")));
			}

			conn.close();
			return constructors;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}

	public List <Driver> listDrivers(Integer anno, Map<Integer, Driver> idMap) {
		String sql = "SELECT DISTINCT drivers.driverId, drivers.forename, drivers.surname " + 
				"FROM results, drivers, races " + 
				"WHERE results.driverId= drivers.driverId " + 
				"AND results.raceId= races.raceId " + 
				"AND races.YEAR= ? " + 
				"AND results.POSITION IS NOT null ";

		try {
			Connection conn = ConnectDB.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, anno);

			ResultSet rs = st.executeQuery();

			List<Driver> drivers = new ArrayList<>();
			while (rs.next()) {
				Driver d= new Driver(rs.getInt("drivers.driverId"), rs.getString("drivers.forename"), rs.getString("drivers.surname"));
				
				if (d!=null)
				{drivers.add(d);
				idMap.put(d.getDriverId(),d);}
			
			}

			conn.close();
			return drivers;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}
	}

	public List<Adiacenza> getEdges(Integer anno, Map<Integer, Driver> idMap) {
		
		String sql = "SELECT d1.driverId, d2.driverId, COUNT(DISTINCT res.raceId) AS cnt " + 
				"FROM drivers d1, drivers d2, results res, results res2, races ra, races ra2 " + 
				"WHERE ra.YEAR=? AND ra2.YEAR= ra.YEAR " + 
				"AND res.raceId= ra.raceId AND res2.raceId= ra2.raceId " + 
				"AND ra.raceId=ra2.raceId " + 
				"AND res.driverId= d1.driverId AND res2.driverId= d2.driverId " + 
				"AND res.POSITION<res2.POSITION " + 
				"AND d1.driverId!= d2.driverId " + 
				"and res.position is not null " + 
				"and res2.position is not null " + 
				"GROUP BY d1.driverId, d2.driverId ";

		try {
			Connection conn = ConnectDB.getConnection();

			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, anno);

			ResultSet rs = st.executeQuery();

			List<Adiacenza> archi = new ArrayList<>();
			while (rs.next()) {
				Adiacenza a= new Adiacenza(rs.getInt("d1.driverId"), rs.getInt("d2.driverId"), rs.getInt("cnt"));
				archi.add(a);
			
			}

			conn.close();
			return archi;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Query Error");
		}}
	

	
}
