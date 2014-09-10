package org.sfm.jdbc;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.Mapper;
import org.sfm.utils.RowHandler;
import org.sfm.utils.ListHandler;

public class DynamicJdbcMapperTest {
	
	final DynamicJdbcMapper<DbObject> mapper;
	
	public DynamicJdbcMapperTest() throws NoSuchMethodException, SecurityException, SQLException {
		mapper = (DynamicJdbcMapper<DbObject>) JdbcMapperFactory.newInstance().useAsm(false).newMapper(DbObject.class);
	}
	
	@Test
	public void testResultSetMapperForEachRS()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				List<DbObject> objects = mapper.forEach(ps.executeQuery(), new ListHandler<DbObject>()).getList();
				assertEquals(1, objects.size());
				DbHelper.assertDbObjectMapping(objects.get(0));
			}
		});
	}
	
	@Test
	public void testResultSetMapperMap()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				rs.next();
				DbObject object = mapper.map(rs);
				DbHelper.assertDbObjectMapping(object);
			}
		});
	}
	@Test
	public void testMapperCache() throws SQLException, ParseException, Exception {
		DbHelper.testDbObjectFromDb(new RowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				MapperKey key = MapperKey.valueOf(rs.getMetaData());
				Mapper<ResultSet, ?> delegate = mapper.getMapper(key);
				assertNull(delegate);
				mapper.forEach(rs, new ListHandler<DbObject>());
				delegate = mapper.getMapper(key);
				assertNotNull(delegate);
				mapper.forEach(rs, new ListHandler<DbObject>());
				assertSame(delegate, mapper.getMapper(key));
			}
		});
	}
}
